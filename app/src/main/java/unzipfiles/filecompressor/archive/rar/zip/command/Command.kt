package unzipfiles.filecompressor.archive.rar.zip.command

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import com.hzy.libp7zip.ExitCode
import com.hzy.libp7zip.P7ZipApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import unzipfiles.filecompressor.archive.rar.zip.MyApplication
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.activity.PickFolderActivity
import unzipfiles.filecompressor.archive.rar.zip.activity.ProcessActivity
import unzipfiles.filecompressor.archive.rar.zip.database.AppDatabase
import unzipfiles.filecompressor.archive.rar.zip.database.History
import unzipfiles.filecompressor.archive.rar.zip.database.TYPE_COMPRESS
import unzipfiles.filecompressor.archive.rar.zip.database.TYPE_EXTRACT
import unzipfiles.filecompressor.archive.rar.zip.databinding.DialogCompressBinding
import unzipfiles.filecompressor.archive.rar.zip.databinding.DialogExtractBinding
import unzipfiles.filecompressor.archive.rar.zip.utils.*
import java.io.File

object Command {
    val types = listOf("zip", "7z", "bzip2", "gzip", "tar", "wim", "xz")
    private var dialog: ProgressDialog? = null

    fun showProgressDialog(context: Context) {
        dialog = ProgressDialog(context)
        dialog!!.setTitle(R.string.progress_title)
        dialog!!.setMessage(context.getText(R.string.progress_message))
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    fun dismissProgressDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    fun getExtractCmd(filePath: String, outPath: String, pass: String): String {
        return if (pass.isEmpty()) String.format("7z x '%s' '-o%s' -aoa", filePath, outPath)
        else String.format("7z x '%s' '-o%s' -aoa -p$pass", filePath, outPath)
    }

    private fun getCompressCmd(
        files: List<File>, outPath: String,
        type: String, pass: String?
    ): String {
        val command = StringBuilder(String.format("7z a -t%s '%s'", type, outPath))
        for (file in files) {
            command.append(" '").append(file.path).append("'")
        }

        if (!pass.isNullOrBlank()) command.append(" -p$pass")
        return command.toString()
    }

    fun showCompressDialog(
        lifecycle: LifecycleOwner,
        context: Context,
        list: List<File>,
        activity: Activity,
//        onFinish: (String) -> Unit
    ) {
        val dialogBinding = DialogCompressBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context).create()
        dialog.setView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerFormat.adapter = adapter
        dialogBinding.spinnerFormat.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if (position == 2) context.toast(context.getString(R.string.toast_error))
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
//        dialogBinding.tvFolder.text = list[0].parent
        dialogBinding.ivFolder.setOnClickListener {
            context.addActivity(PickFolderActivity::class.java)
        }
        MyApplication.instance.folder.observe(lifecycle) {
            dialogBinding.tvFolder.text = it
            dialogBinding.tvFolder.scrollTo(0, 0)
        }
        val defaultPath = Constants.EXTERNAL_DIRECTORY.path + "/ZipFileOpener"
        MyApplication.instance.folder.postValue(defaultPath)
        dialogBinding.tvFolder.movementMethod = ScrollingMovementMethod()
//        dialogBinding.edtName.setText("Compress")

        var hidePassword = true
        dialogBinding.ivShowPass.setOnClickListener {
            if (hidePassword) {
                hidePassword = false
                dialogBinding.ivShowPass.setImageResource(R.drawable.ic_hide_pass)
                dialogBinding.edtPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                hidePassword = true
                dialogBinding.ivShowPass.setImageResource(R.drawable.ic_show_pass)
                dialogBinding.edtPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }

        dialogBinding.btnCompress.setOnClickListener {
            context.logEvents("btn_compress")
            dialog.dismiss()
            val type = dialogBinding.spinnerFormat.selectedItem.toString()
            val folder = dialogBinding.tvFolder.text.toString()
            val newName = dialogBinding.edtName.text.toString()
            val pass = dialogBinding.edtPassword.text.toString()
            Common.getTypeCompress = type
            Common.getFolderCompress = folder
            Common.getNameCompress = newName
            Common.getPassCompress = pass
            Common.getListFile = list
            saveHistoryDatabase(list.toMutableList(), TYPE_COMPRESS, context)
            MyApplication.removeAll()
            Common.isDelete = true

            val intent = Intent(context, ProcessActivity::class.java)
            intent.putExtra(Common.INTENT_PROCESS, Common.INTENT_COMPRESS)
            context.startActivity(intent)

//                    AdmodUtils.getInstance().loadAndShowAdInterstitialWithCallbackMultiAds(activity as AppCompatActivity, Ads.INTER_PROCESS_1.adId, Ads.INTER_PROCESS_2.adId, Ads.INTER_PROCESS_3.adId, object : AdCallback{
//                override fun onAdClosed() {
//                    context.logEvents("inter_compress_close")
//                    val intent = Intent(context, ProcessActivity::class.java)
//                    intent.putExtra(Common.INTENT_PROCESS, Common.INTENT_COMPRESS)
//                    context.startActivity(intent)
//                    AdmodUtils.getInstance().dismissAdDialog()
//                }
//
//                override fun onAdFail() {
//                    val intent = Intent(context, ProcessActivity::class.java)
//                    intent.putExtra(Common.INTENT_PROCESS, Common.INTENT_COMPRESS)
//                    context.startActivity(intent)
//                    AdmodUtils.getInstance().dismissAdDialog()
//                }
//
//            }, true)
//
        }
        dialog.show()
    }

    fun showExtractDialog(
        lifecycle: LifecycleOwner,
        context: Context,
        list: List<File>,
        activity: Activity
//        onFinish: (String) -> Unit
    ) {
        val dialogBinding = DialogExtractBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context).create()
        dialog.setView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var hidePassword = true
        dialogBinding.ivShowPass.setOnClickListener {
            if (hidePassword) {
                hidePassword = false
                dialogBinding.ivShowPass.setImageResource(R.drawable.ic_hide_pass)
                dialogBinding.edtPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                hidePassword = true
                dialogBinding.ivShowPass.setImageResource(R.drawable.ic_show_pass)
                dialogBinding.edtPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }

        dialogBinding.ivFolder.setOnClickListener {
            context.addActivity(PickFolderActivity::class.java)
        }
        MyApplication.instance.folder.observe(lifecycle) {
            dialogBinding.tvFolder.text = it
            dialogBinding.tvFolder.scrollTo(0, 0)
        }
//        val defaultPath = if (list[0].parent.isNullOrBlank()) Constants.EXTERNAL_DIRECTORY.path else list[0].parent!!
        val defaultPath = Constants.EXTERNAL_DIRECTORY.path + "/ZipFileOpener"
        MyApplication.instance.folder.postValue(defaultPath)
//        dialogBinding.tvFolder.text =
//            if (list[0].parent.isNullOrBlank()) Constants.EXTERNAL_DIRECTORY.path else list[0].parent

        dialogBinding.btnExtract.setOnClickListener {
            context.logEvents("btn_extract")
            dialog.dismiss()
            val folder = dialogBinding.tvFolder.text.toString()
            val pass = dialogBinding.edtPassword.text.toString()
            Common.getFolderExtract = folder
            Common.getPassExtract = pass
            Common.getListFile = list
            saveHistoryDatabase(list.toMutableList(), TYPE_EXTRACT, context)
            MyApplication.removeAll()
            Common.isDelete = true
            context.logEvents("inter_extract_load")

            val intent = Intent(context, ProcessActivity::class.java)
            intent.putExtra(Common.INTENT_PROCESS, Common.INTENT_EXTRACT)
            context.startActivity(intent)
//            AdmodUtils.getInstance().dismissAdDialog()

//            AdmodUtils.getInstance().loadAndShowAdInterstitialWithCallbackMultiAds(activity as AppCompatActivity, Ads.INTER_PROCESS_1.adId, Ads.INTER_PROCESS_2.adId, Ads.INTER_PROCESS_3.adId, object : AdCallback{
//                override fun onAdClosed() {
//                    context.logEvents("inter_extract_close")
//                    val intent = Intent(context, ProcessActivity::class.java)
//                    intent.putExtra(Common.INTENT_PROCESS, Common.INTENT_EXTRACT)
//                    context.startActivity(intent)
//                    AdmodUtils.getInstance().dismissAdDialog()
//                }
//
//                override fun onAdFail() {
//                    val intent = Intent(context, ProcessActivity::class.java)
//                    intent.putExtra(Common.INTENT_PROCESS, Common.INTENT_EXTRACT)
//                    context.startActivity(intent)
//                    AdmodUtils.getInstance().dismissAdDialog()
//                }
//
//            }, true)
//
        }
        dialog.show()
    }

    private fun compressFile(
        files: List<File>, type: String, folder: String,
        pass: String? = null, onFinish: (String) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val cmd = getCompressCmd(files, folder, type, pass)
            cmd.log()
            val result = runCommand(cmd)
            withContext(Dispatchers.Main) {
                onFinish(getResultMsg(result))
                if (result == 0) MyApplication.instance.logEvents("compress_success")
            }
        }
    }

    private fun extractFile(
        files: List<File>, folder: String,
        pass: String, context: Context, onFinish: (String) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            var multiResult = 0
            files.forEach {
                val outPath = "$folder/${it.name}-ext"
                val cmd = getExtractCmd(it.path, outPath, pass)
                cmd.log()
                val result = runCommand(cmd)
                if (result != 0) File(outPath).deleteRecursively()
                multiResult += result
            }
            withContext(Dispatchers.Main) {
                onFinish(getMultiResultMsg(multiResult,context))
                if (multiResult == 0) MyApplication.instance.logEvents("extract_success")
            }
        }
    }

    private fun runCommand(cmd: String): Int {
        return P7ZipApi.executeCommand(cmd)
    }

    /**
     * Get result when extract multiple files
     */
    private fun getMultiResultMsg(result: Int, context: Context): String {
        val responseMsg = when (result) {
            0 -> context.getString(R.string.success)
            else -> context.getString(R.string.an_error_occurred)
        }
        return responseMsg
    }

    /**
     * Get result when compress or extract just 1 file
     */
    private fun getResultMsg(result: Int): String {
        val context = MyApplication.instance
        var responseMsg = R.string.msg_ret_success
        when (result) {
            ExitCode.EXIT_OK -> responseMsg =
                R.string.msg_ret_success
            ExitCode.EXIT_WARNING -> responseMsg =
                unzipfiles.filecompressor.archive.rar.zip.R.string.msg_ret_warning
            ExitCode.EXIT_FATAL -> responseMsg =
                unzipfiles.filecompressor.archive.rar.zip.R.string.msg_ret_fault
            ExitCode.EXIT_CMD_ERROR -> responseMsg =
                unzipfiles.filecompressor.archive.rar.zip.R.string.msg_ret_command
            ExitCode.EXIT_MEMORY_ERROR -> responseMsg =
                unzipfiles.filecompressor.archive.rar.zip.R.string.msg_ret_memmory
            ExitCode.EXIT_NOT_SUPPORT -> responseMsg =
                unzipfiles.filecompressor.archive.rar.zip.R.string.msg_ret_user_stop
        }
        return context.getString(responseMsg)
    }

    private fun saveHistoryDatabase(list: MutableList<File>, type: Int, context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            val historyList = list.map {
                History(it.path, System.currentTimeMillis(), type)
            }
            AppDatabase.saveHistory(context, historyList)
        }
    }

}