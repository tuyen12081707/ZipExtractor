package unzipfiles.filecompressor.archive.rar.zip.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
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

class CompressExtractFragment : Fragment() {
    private lateinit var adapter: FileManagerAdapter
    private lateinit var binding: FragmentCompressExtractBinding

    companion object {
        fun newInstance(type: String) = CompressExtractFragment().apply {
            arguments = Bundle(1).apply { putString("type", type) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompressExtractBinding.inflate(layoutInflater)

        AdsManager.loadNative(requireActivity(),AdsManager.nativeProcess)

        adapter = FileManagerAdapter({ this.openFile(it) }, enableNative = true)
        binding.rvList.adapter = adapter
        binding.rvList.itemAnimator = null

        observeDatabase()
        setupSorting()

        MyApplication.instance.selectedFiles.observe(
            viewLifecycleOwner
        ) { onItemCheckChanged(it) }

        setupMenu()
//        requireActivity().showNativeCustom(
//            requireActivity(),
//            Ads.NATIVE_ID_1.adId,
//            Ads.NATIVE_ID_2.adId,
//            Ads.NATIVE_ID_3.adId,
//            binding.flNative
//        )
        AdsManager.showNative(requireActivity(),AdsManager.nativeId,binding.flNative)
        return binding.root
    }

    private fun observeDatabase() {
        val type = arguments?.getString("type")
        AppDatabase.getALlAsLiveData(requireContext(), type).observe(viewLifecycleOwner) { list ->
            log("type: $type, size: ${list.size}")
            val files = mutableListOf<File>()
            list.forEach {
                val file = File(it.path)
                if (!file.exists()) AppDatabase.delete(requireContext(), it)
                else files.add(file)
            }
            when (binding.sort.rbGroup.checkedRadioButtonId) {
                R.id.rbName -> files.sortedBy { it.name.lowercase() }
                R.id.rbDate -> files.sortedByDescending { it.lastModified() }
                R.id.rbSize -> files.sortedBy { it.length() }
            }
            binding.clEmpty.setVisible(list.isNullOrEmpty())
            adapter.submitList(files.toList())
        }
    }

    private fun setupMenu() {
        binding.menu.tvCompress.setOnClickListener {
            val list = MyApplication.getSelectedFiles().toMutableList()
            context?.logEvents("compress_click")
            Command.showCompressDialog(
                viewLifecycleOwner,
                requireContext(),
                list,
                requireActivity()
            )
        }
        binding.menu.tvExtract.setOnClickListener {
            val list = MyApplication.getSelectedFiles().toMutableList()
            context?.logEvents("extract_click")
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

    private fun saveHistoryDatabase(list: MutableList<File>, type: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val historyList = list.map {
                History(it.path, System.currentTimeMillis(), type)
            }
            AppDatabase.saveHistory(requireContext(), historyList)
        }
    }

    private fun onItemCheckChanged(list: MutableList<File>) {
        adapter.updateCheckbox()
        binding.menu.root.setVisible(list.isNotEmpty())
        binding.menu.tvItems.text = "${list.size} " + getString(R.string.items)
        binding.menu.tvExtract.alpha = 1f
        binding.menu.tvExtract.isEnabled = true

        if (list.size <= 0) {
            binding.menu.tvExtract.alpha = 0.5f
            binding.menu.tvExtract.isEnabled = false

            binding.menu.tvCompress.alpha = 0.5f
            binding.menu.tvCompress.isEnabled = false

            binding.menu.tvMore.alpha = 0.5f
            binding.menu.tvMore.isEnabled = false
        } else {
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
            if (list.size > 0)
                openFile(list[0])
        }
        dialogBinding.tvRename.setOnClickListener {
            dialog.dismiss()
            showRenameDialog(list[0])
        }
        dialogBinding.tvDelete.setOnClickListener {
            dialog.dismiss()
            showDeleteDialog(list)
        }
        dialogBinding.tvShare.setOnClickListener {
            dialog.dismiss()
            if (list.size > 0) {
                disableOnResume(requireActivity().javaClass)
                context?.share(list, requireContext())
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
                    Common.isDelete = true
                }
                else -> context?.toast(getString(R.string.failed))
            }
        }
        dialog.show()
    }

    private fun setupSorting() {
        val type = arguments?.getString("type")
        binding.sort.rbGroup.setOnCheckedChangeListener { _, id ->
            AppDatabase.getALl(requireContext(), type).let { list ->
                val files = when (id) {
                    R.id.rbName -> list.map { File(it.path) }.sortedBy { it.name.lowercase() }
                    R.id.rbDate -> list.map { File(it.path) }
                        .sortedByDescending { it.lastModified() }
                    else -> list.map { File(it.path) }.sortedBy { it.getSize() }
                }
                adapter.submitList(files)
                runDelay({ binding.rvList.scrollToPosition(0) }, 10)
                binding.clEmpty.setVisible(files.isNullOrEmpty())
            }
            binding.sort.root.gone()
        }
        binding.sort.rbDate.isChecked = true
    }

    private fun openFile(file: File) {
        if (FileUtility.isArchiver(file)) {
            Command.showExtractDialog(this, requireContext(), listOf(file), requireActivity())
        } else {
            disableOnResume(requireActivity().javaClass)
            FileUtility.openFile(requireContext(), file)
        }
    }
}