package unzipfiles.filecompressor.archive.rar.zip.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding4.view.clicks
import unzipfiles.filecompressor.archive.rar.zip.MyApplication
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.databinding.ItemFileBinding
import unzipfiles.filecompressor.archive.rar.zip.databinding.ItemNativeBinding
import unzipfiles.filecompressor.archive.rar.zip.utils.*
import java.io.File
import java.util.concurrent.TimeUnit

class FileManagerAdapter(
    val onClick: (File) -> Unit,
    val hideCheckbox: Boolean = false,
    val enableNative: Boolean = false,
    var loadNative: Boolean = true

) :
    ListAdapter<File, RecyclerView.ViewHolder>(DiffCallback) {

    private var filterList = listOf<File>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) NativeHolder(ItemNativeBinding.inflate(inflater, parent, false))
        else FileHolder(ItemFileBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) (holder as NativeHolder).bind()
        else (holder as FileHolder).bind(getItem(position - 1))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun filter(query: String?) {
        if (query == null) return
        if (filterList.isEmpty()) filterList = currentList
        submitList(filterList.filter { it.name.contains(query, true) })
        Log.e("SSS", "filter size" + currentList.size)
        loadNative = currentList.size > 1
    }


    fun updateCheckbox() {
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    inner class NativeHolder(private val binding: ItemNativeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.frNative.gone()
        }
    }

    inner class FileHolder(private val binding: ItemFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File) {
            binding.tvName.text = file.name
            if (hideCheckbox) binding.checkbox.gone()
            val details =
                "${file.lastModified().convertTime("dd/MM/yyyy | hh:mm a")} | " +
                        file.getSize().convertSize()
            if (file.isDirectory) {
//                val fileList = file.listFiles(FileFilter { !it.isHidden })
//                fileList?.size?.let { details += (" | $it files") }
                binding.ivIcon.setImageResource(R.drawable.ic_folder)
            } else {
                if (FileUtility.isWord(file)) {
                    binding.ivIcon.setImageResource(R.drawable.ic_doc)
                } else if (FileUtility.isExcel(file)) {
                    binding.ivIcon.setImageResource(R.drawable.ic_excel)
                } else if (FileUtility.isPpt(file)) {
                    binding.ivIcon.setImageResource(R.drawable.ic_ppt)
                } else if (FileUtility.isPdf(file)) {
                    binding.ivIcon.setImageResource(R.drawable.ic_pdf)
                } else if (FileUtility.isTxt(file)) {
                    binding.ivIcon.setImageResource(R.drawable.ic_txt)
                } else if (FileUtility.isArchiver(file)) {
                    binding.ivIcon.setImageResource(R.drawable.ic_compress)
                } else if (FileUtility.isAudio(file)) {
                    binding.ivIcon.setImageResource(R.drawable.ic_audio_file)
                } else if (FileUtility.isVideo(file) || FileUtility.isImage(file)) {
                    Glide.with(binding.root.context)
                        .load(file.path)
//                    .error(R.drawable.ic_file2)
                        .into(binding.ivIcon)
                }
            }
            binding.tvDetails.text = details
            binding.root.setOnClickListener {
                if (file.isDirectory) {
                    onClick(file)
                } else {
                    binding.checkbox.isChecked = !binding.checkbox.isChecked
                }
            }
            binding.checkbox.isChecked =
                MyApplication.instance.selectedFiles.value?.contains(file) == true

            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    MyApplication.addFile(file)
                } else {
                    MyApplication.removeFile(file)
                }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.absolutePath == newItem.absolutePath
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem == newItem
        }
    }
}