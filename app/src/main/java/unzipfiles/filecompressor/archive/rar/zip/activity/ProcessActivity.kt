 package unzipfiles.filecompressor.archive.rar.zip.activity

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.SnackbarUtils
import com.bumptech.glide.Glide
import com.hzy.libp7zip.ExitCode
import com.hzy.libp7zip.P7ZipApi
import com.jakewharton.rxbinding4.view.clicks
import com.vapp.admoblibrary.ads.AdCallback
import com.vapp.admoblibrary.ads.AdmodUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import unzipfiles.filecompressor.archive.rar.zip.MyApplication
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.ads.AdsManager
import unzipfiles.filecompressor.archive.rar.zip.ads.Utils
import unzipfiles.filecompressor.archive.rar.zip.databinding.ActivityProcessBinding
import unzipfiles.filecompressor.archive.rar.zip.utils.*
import java.io.File
import java.util.concurrent.TimeUnit

 class ProcessActivity : AppCompatActivity() {

     lateinit var binding: ActivityProcessBinding
     var isBack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setThemeForActivity(this)
        binding = ActivityProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Glide.with(this).asGif().load(R.raw.processing_animation).into(binding.imageProcess)
        binding.imageProcess.setAnimation(R.raw.process_file)

        AdsManager.loadNative(this,AdsManager.nativeDirectory)
        AdsManager.loadInter(this,AdsManager.interProcess)

        val action = intent.getStringExtra(Common.INTENT_PROCESS)

        if (action.equals(Common.INTENT_COMPRESS)) {
            val type = Common.getTypeCompress
            val folder = Common.getFolderCompress
            val name = Common.getNameCompress
            val pass = Common.getPassCompress
            val listFile = Common.getListFile
            compressFile(listFile, type, "$folder/$name", pass) {
                if (it == getString(R.string.success) || it == "Success") {
//                    Glide.with(this).asGif().load(R.raw.process_completed).into(binding.imageProcess)
                    binding.imageProcess.visibility = View.INVISIBLE
                    binding.imageProcessSuccess.visibility = View.VISIBLE
                    binding.tvProcess.visibility = View.INVISIBLE
                    binding.btnView.visibility = View.VISIBLE
                    toast(getString(R.string.success))
                    checkShowRate()
                    isBack = true
                } else {
                    isBack = true
                    SnackbarUtils.with(binding.root).setMessage(it).show()
                    Handler().postDelayed(Runnable {
                        finish()
                    }, 3000)
                }
            }
        } else {
            val folder = Common.getFolderExtract
            val pass = Common.getPassExtract
            val listFile = Common.getListFile
            extractFile(listFile, folder, pass, this) {
                if (it == getString(R.string.success) || it == "Success") {
//                    Glide.with(this).asGif().load(R.raw.process_completed).into(binding.imageProcess)
                    binding.imageProcess.visibility = View.INVISIBLE
                    binding.imageProcessSuccess.visibility = View.VISIBLE
                    binding.tvProcess.visibility = View.INVISIBLE
                    binding.btnView.visibility = View.VISIBLE
                    toast(getString(R.string.success))
                    checkShowRate()
                    isBack = true
                } else {
                    isBack = true
                    SnackbarUtils.with(binding.root).setMessage(it).show()
                    Handler().postDelayed(Runnable {
                        finish()
                    }, 3000)
                }
            }
        }

        binding.btnView.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe() {
            logEvents("btn_view_file_1")
            AdsManager.showAdInter(this,AdsManager.interProcess,object : AdsManager.AdListenerNew{
                override fun onAdClosed() {
                    openDestination()
                }

                override fun onFailed() {
                    openDestination()
                }

            },"inter_process")
        }
        AdsManager.showNative(this,AdsManager.nativeProcess,binding.frNative)
    }

     private fun checkShowRate() {
         if(Utils.getShowRateFirstOpen(this)){
             Utils.showRate(this,false)
             Utils.setShowRateFirstOpen(this,false)
         }
     }

     override fun onBackPressed() {
         if (isBack) {
             super.onBackPressed()
         } else {
             SnackbarUtils.with(binding.root).setMessage(getString(R.string.do_not_exit)).show()
         }
     }

     private fun openDestination() {
//         MyApplication.showRate += 1
         val destinationFolder = MyApplication.instance.folder.value ?: return
         val i = Intent(this@ProcessActivity, InternalStorageActivity::class.java)
         i.putExtra(Constants.EXTRA_PATH, destinationFolder)
         startActivity(i)
         finish()
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
                 onFinish(getMultiResultMsg(multiResult))
                 if (multiResult == 0) MyApplication.instance.logEvents("extract_success")
             }
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

     private fun runCommand(cmd: String): Int {
         return P7ZipApi.executeCommand(cmd)
     }

     /**
      * Get result when extract multiple files
      */
     private fun getMultiResultMsg(result: Int): String {
         val responseMsg = when (result) {
             0 -> getString(R.string.success)
             else -> getString(R.string.an_error_occurred)
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
                 R.string.msg_ret_warning
             ExitCode.EXIT_FATAL -> responseMsg =
                 R.string.msg_ret_fault
             ExitCode.EXIT_CMD_ERROR -> responseMsg =
                 R.string.msg_ret_command
             ExitCode.EXIT_MEMORY_ERROR -> responseMsg =
                 R.string.msg_ret_memmory
             ExitCode.EXIT_NOT_SUPPORT -> responseMsg =
                 R.string.msg_ret_user_stop
             else -> responseMsg =
                 R.string.msg_ret_fault
         }
         return context.getString(responseMsg)
     }
     override fun onWindowFocusChanged(hasFocus: Boolean) {
         super.onWindowFocusChanged(hasFocus)
         window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                 or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                 or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                 or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                 or View.SYSTEM_UI_FLAG_FULLSCREEN
                 or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
     }
}