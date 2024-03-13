package unzipfiles.filecompressor.archive.rar.zip.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

class CategoryFragment : Fragment() {
    private lateinit var adapter: FileManagerAdapter
    private lateinit var binding: FragmentCompressExtractBinding

    companion object {
        fun newInstance(type: String) = CategoryFragment().apply {
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

        setupSortingAndSubmitList()
        setupCompressMenu()
        if (!isDocumentFragment()) setHasOptionsMenu(true)

        MyApplication.instance.selectedFiles.observe(
            viewLifecycleOwner
        ) { onItemCheckChanged(it) }
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

    override fun onResume() {
        super.onResume()
        if (isDocumentFragment()) setHasOptionsMenu(isVisible)
    }

    override fun onPause() {
        super.onPause()
        binding.sort.root.gone()
    }

    private fun isDocumentFragment(): Boolean {
        val type = arguments?.getString("type") ?: Type.ARCHIVER.name
        return type == Type.DOCUMENTS.name ||
                type == Type.PDF.name ||
                type == Type.WORD.name ||
                type == Type.EXCEL.name ||
                type == Type.PPT.name ||
                type == Type.TXT.name
    }

    private fun setupCompressMenu() {
        binding.menu.tvCompress.setOnClickListener {
            context?.logEvents("compress_click")
            val list = MyApplication.getSelectedFiles().toMutableList()
            Command.showCompressDialog(
                viewLifecycleOwner,
                requireContext(),
                list,
                requireActivity()
            )
        }
        binding.menu.tvExtract.setOnClickListener {
            context?.logEvents("extract_click")
            val list = MyApplication.getSelectedFiles().toMutableList()
            Command.showExtractDialog(this, requireContext(), list, requireActivity())
        }
        binding.menu.tvMore.setOnClickListener {
            showFileDialog(MyApplication.getSelectedFiles())
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                val searchView = item.actionView as SearchView
                val magImage =
                    searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
                magImage.layoutParams = LinearLayout.LayoutParams(0, 0)
                searchView.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ripple_corner_10_stroke)
                searchView.setIconifiedByDefault(false)
                searchView.queryHint = getString(R.string.search)

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter(newText)
                        return false
                    }
                })
                true
            }
            R.id.action_sort -> {
                binding.sort.root.setVisible(!binding.sort.root.isVisible)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun scanAndSubmitList(scrollTop: Boolean = false , isRename: Boolean = false) {
        val type = arguments?.getString("type") ?: Type.ARCHIVER.name
//        val sortBy = binding.spinSort.selectedItemPosition
        val list = mutableListOf<File>()
        lifecycleScope.launch(Dispatchers.IO) {
            when (type) {
                Type.DOWNLOAD.name -> FileUtility.scanForFile(
                    list, type, Constants.DOWLOAD_DIRECTORY.path
                )
                else -> FileUtility.scanForFile(list, type)
            }
            when (binding.sort.rbGroup.checkedRadioButtonId) {
                R.id.rbName -> list.sortBy { it.name.lowercase() }
                R.id.rbDate -> list.sortByDescending { it.lastModified() }
                R.id.rbSize -> list.sortBy { it.length() }
            }

            withContext(Dispatchers.Main) {
                adapter.submitList(list)
                binding.clEmpty.setVisible(list.isEmpty())
                if (scrollTop) runDelay({ binding.rvList.scrollToPosition(0) }, 10)
                Common.isDelete = true
                if (isRename){
                    runDelay({ adapter.notifyDataSetChanged() }, 30)
                }
            }
        }
    }

    private fun onItemCheckChanged(list: MutableList<File>) {
//        if (Common.isDelete) {
//            adapter.updateCheckbox()
//            Common.isDelete = false
//        }
        adapter.updateCheckbox()
        binding.menu.root.setVisible(true)
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
//        dialog.window?.setGravity(Gravity.BOTTOM)

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
            openFile(list[0])
        }
        dialogBinding.tvRename.setOnClickListener {
            dialog.dismiss()
            if (list.size > 0) {
                showRenameDialog(list[0])
            }
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
            list.forEach {
                if (it.isDirectory) it.deleteRecursively()
                else it.delete()
            }
            context?.toast(getString(R.string.deleted))
            MyApplication.removeAll()
            scanAndSubmitList(false)
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
                    scanAndSubmitList(isRename = true)
                }
            }
        }
        dialog.show()
    }

    private fun setupSortingAndSubmitList() {
        binding.sort.rbGroup.setOnCheckedChangeListener { _, _ ->
            scanAndSubmitList(true)
            binding.sort.root.gone()
        }
        binding.sort.rbName.isChecked = true
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