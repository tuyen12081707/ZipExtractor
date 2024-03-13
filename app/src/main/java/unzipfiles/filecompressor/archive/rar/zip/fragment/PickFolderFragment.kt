package unzipfiles.filecompressor.archive.rar.zip.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unzipfiles.filecompressor.archive.rar.zip.activity.PickFolderActivity
import unzipfiles.filecompressor.archive.rar.zip.adapter.FileManagerAdapter
import unzipfiles.filecompressor.archive.rar.zip.databinding.FragmentFileListBinding
import unzipfiles.filecompressor.archive.rar.zip.utils.gone
import unzipfiles.filecompressor.archive.rar.zip.utils.visible
import java.io.File

class PickFolderFragment : Fragment() {
    private lateinit var binding: FragmentFileListBinding
    private lateinit var adapter: FileManagerAdapter
    var path = ""

    companion object {
        fun newInstance(path: String) = PickFolderFragment().apply {
            arguments = Bundle(1).apply { putString("path", path) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFileListBinding.inflate(layoutInflater)

        path = arguments?.getString("path") ?: ""
        adapter = FileManagerAdapter({ onItemSelected(it) }, hideCheckbox = true)
        binding.recyclerView.adapter = adapter

        scanForFiles()
        binding.tvEmpty.text = "No folder found"

        return binding.root
    }

    private fun onItemSelected(file: File) {
        if (file.isDirectory) {
            val frag = newInstance(file.absolutePath)
            (activity as PickFolderActivity).replaceFragment(frag, file.absolutePath)
        }
    }

    private fun scanForFiles() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val folders = File(path).listFiles()?.filter { it.isDirectory && !it.isHidden }
                ?.sortedBy { it.name }
            if (folders.isNullOrEmpty()) {
                binding.tvEmpty.visible()
                adapter.submitList(null)
            } else {
                binding.tvEmpty.gone()
                adapter.submitList(folders)
                binding.recyclerView.scrollToPosition(0)
            }
        }
    }
}