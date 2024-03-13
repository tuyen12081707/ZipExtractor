package unzipfiles.filecompressor.archive.rar.zip.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import unzipfiles.filecompressor.archive.rar.zip.MyApplication
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.activity.InternalStorageActivity
import unzipfiles.filecompressor.archive.rar.zip.adapter.FileManagerAdapter
import unzipfiles.filecompressor.archive.rar.zip.adapter.SelectedAdapter
import unzipfiles.filecompressor.archive.rar.zip.ads.AdsManager
import unzipfiles.filecompressor.archive.rar.zip.command.Command
import unzipfiles.filecompressor.archive.rar.zip.database.AppDatabase
import unzipfiles.filecompressor.archive.rar.zip.database.History
import unzipfiles.filecompressor.archive.rar.zip.databinding.*
import unzipfiles.filecompressor.archive.rar.zip.utils.*
import unzipfiles.filecompressor.archive.rar.zip.utils.FileUtility.share
import java.io.File
import java.io.FileFilter

class InternalStorageFragment : Fragment() {
    private lateinit var binding: FragmentFileListBinding
    private lateinit var adapter: FileManagerAdapter
    private var path = ""
    private var selectedDir: File? = null

    companion object {
        fun newInstance(path: String) = InternalStorageFragment().apply {
            arguments = Bundle(1).apply { putString("path", path) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFileListBinding.inflate(layoutInflater)

        AdsManager.loadNative(requireActivity(), AdsManager.nativeProcess)

        path = arguments?.getString("path") ?: ""
        adapter = FileManagerAdapter({ openFile(it) })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = null
        scanForFiles(true)
        setupMenu()
        MyApplication.instance.selectedFiles.observe(
            viewLifecycleOwner
        ) { onItemCheckChanged(it) }
        AdsManager.showNative(requireActivity(),AdsManager.nativeDirectory,binding.flNative)
        return binding.root
    }

    private fun onItemCheckChanged(list: MutableList<File>) {
//        if (Common.isDelete) {
//            adapter.updateCheckbox()
//            Common.isDelete = false
//        }
        adapter.updateCheckbox()
        binding.menu.root.setVisible(true)
        binding.menu.tvItems.text = "${list.size} "+ getString(R.string.items)
        binding.menu.tvExtract.alpha = 1f
        binding.menu.tvExtract.isEnabled = true


        if(list.size <= 0){
            binding.menu.tvExtract.alpha = 0.5f
            binding.menu.tvExtract.isEnabled = false

            binding.menu.tvCompress.alpha = 0.5f
            binding.menu.tvCompress.isEnabled = false

            binding.menu.tvMore.alpha = 0.5f
            binding.menu.tvMore.isEnabled = false
        }else{
            binding.menu.tvExtract.alpha = 1f
            binding.menu.tvExtract.isEnabled = true

            binding.menu.tvCompress.alpha = 1f
            binding.menu.tvCompress.isEnabled = true

            binding.menu.tvMore.alpha = 1f
            binding.menu.tvMore.isEnabled = true
        }

        list.forEach {
            if (!FileUtility.isArchiver(it)) {
                binding.menu.tvExtract.alpha = 0.5f
                binding.menu.tvExtract.isEnabled = false
            }
        }
    }

    private fun setupMenu() {
        binding.menu.tvCompress.setOnClickListener {
            val list = MyApplication.getSelectedFiles().toMutableList()
            context?.logEvents("compress_click")
            Command.showCompressDialog(viewLifecycleOwner, requireContext(), list, requireActivity())
        }
        binding.menu.tvExtract.setOnClickListener {
            val list = MyApplication.getSelectedFiles().toMutableList()
            context?.logEvents("extract_click")
            log("list size: ${list.size}")
            Command.showExtractDialog(this, requireContext(), list, requireActivity())
        }
        binding.menu.tvMore.setOnClickListener {
            val list = MyApplication.getSelectedFiles()
            showFileDialog(list)
        }
        binding.menu.tvItems.setOnClickListener {
            showSelectedFiles()
        }
    }

    private fun toastAndOpenDestination() {
        context?.toast(getString(R.string.success))
        val destinationFolder = MyApplication.instance.folder.value ?: return
        val i = Intent(requireContext(), InternalStorageActivity::class.java)
        i.putExtra(Constants.EXTRA_PATH, destinationFolder)
        startActivity(i)
    }

    private fun showSelectedFiles() {
        val dialogBinding =
            DialogSelectedFilesBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val adapter = SelectedAdapter()
        dialogBinding.rvList.adapter = adapter
        dialogBinding.rvList.itemAnimator = null
        MyApplication.instance.selectedFiles.observe(
            viewLifecycleOwner
        ) {
            adapter.submitList(it.toList())
        }
        dialogBinding.ivDelete.setOnClickListener {
            MyApplication.removeAll()
            Common.isDelete = true
        }
        dialog.show()
    }

    private fun showFileDialog(list: MutableList<File>) {
        val dialogBinding = DialogFileOptionBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        if (list.size > 1) {
            dialogBinding.tvOpen.gone()
            dialogBinding.tvRename.gone()
        }
        dialogBinding.tvOpen.setOnClickListener {
            dialog.dismiss()
            if (list.size > 0) {
                openFile(list[0])

            }
        }
        dialogBinding.tvRename.setOnClickListener {
            dialog.dismiss()
            showRenameDialog(list[0])
        }
        dialogBinding.tvDelete.setOnClickListener {
            dialog.dismiss()
            if (list.size > 0) {
                showDeleteDialog(list)

            }
        }
        dialogBinding.tvShare.setOnClickListener {
            dialog.dismiss()
            if (list.size > 0) {
                disableOnResume(requireActivity().javaClass)
                context?.share(list,requireContext())
            }

        }
        dialog.show()
    }

    private fun showDeleteDialog(list: MutableList<File>) {
        val dialogBinding = DialogDeleteBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnDelete.setOnClickListener {
            dialog.dismiss()
            list.forEach { file ->
                if (file.isDirectory) file.deleteRecursively()
                else {
                    file.delete()
                    AppDatabase.delete(requireContext(), file.path)
                }
            }
            scanForFiles()
            context?.toast(getString(R.string.deleted))
            MyApplication.removeAll()
            Common.isDelete = true
        }
        dialog.show()
    }

    private fun showRenameDialog(file: File) {
        val dialogBinding = DialogRenameBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.edtName.setText(file.nameWithoutExtension)
        dialogBinding.btnSave.setOnClickListener {
            val newName = dialogBinding.edtName.text.toString()
            val newFile = File(file.parent, "${newName}.${file.extension}")
            when {
                newName.isBlank() -> {
                    context?.toast(getString(R.string.name_error_1))
                    return@setOnClickListener
                }
                newFile.exists() -> {
                    context?.toast(getString(R.string.name_error_2))
                    return@setOnClickListener
                }
                file.renameTo(newFile) -> {
                    dialog.dismiss()
                    context?.toast(getString(R.string.succeeded))
                    MyApplication.removeAll()
                    AppDatabase.update(requireContext(), file.path, newFile.path)
                    scanForFiles()
                    Common.isDelete = true
                }
                else -> context?.toast(getString(R.string.failed))
            }
        }
        dialog.show()
    }

    private fun saveHistoryDatabase(list: MutableList<File>, type: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            log("saving history: ${list.size} - $type")
            val historyList = list.map {
                History(it.path, System.currentTimeMillis(), type)
            }
            AppDatabase.saveHistory(requireContext(), historyList)
        }
    }

    fun scanForFiles(scrollTop: Boolean = false) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val showHiddenFiles = false
            val listFiles = if (showHiddenFiles) {
                File(path).listFiles()
            } else {
                File(path).listFiles(FileFilter { !it.isHidden })
            }
            if (listFiles.isNullOrEmpty()) {
                binding.tvEmpty.visible()
                adapter.submitList(null)
            } else {
                binding.tvEmpty.gone()
//                val list =
//                    listFiles.sortedBy { it.name.lowercase() }.sortedByDescending { it.isDirectory }
                val list =
                    listFiles.sortedWith(compareByDescending<File> { it.lastModified() }.thenBy { it.name.lowercase() })
                adapter.submitList(list)
                if (scrollTop) runDelay({ binding.recyclerView.scrollToPosition(0) }, 100)
                runTryCatch {
                    selectedDir?.let { adapter.notifyItemChanged(list.indexOf(it)) }
                }
            }
        }
    }

    private fun openFile(file: File) {
        when {
            file.isDirectory -> {
                selectedDir = file
                val frag = newInstance(file.absolutePath)
                (activity as InternalStorageActivity).addFragment(frag)
            }
            FileUtility.isArchiver(file) -> {
                Command.showExtractDialog(this, requireContext(), listOf(file), requireActivity())
            }
            else -> {
                disableOnResume(requireActivity().javaClass)
                FileUtility.openFile(requireContext(), file)
            }
        }
    }
}