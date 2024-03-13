package unzipfiles.filecompressor.archive.rar.zip.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import unzipfiles.filecompressor.archive.rar.zip.MyApplication
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.databinding.ItemSelectedFileBinding
import unzipfiles.filecompressor.archive.rar.zip.utils.*
import java.io.File

class SelectedAdapter : ListAdapter<File, SelectedAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemSelectedFileBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(private val binding: ItemSelectedFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File) {
            binding.tvName.text = file.name
            val details =
                file.lastModified().convertTime("dd/MM/yyyy | hh:mm a | ") +
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
            binding.ivClose.setOnClickListener {
                MyApplication.removeFile(file)
                Common.isDelete = true
            }
            binding.tvDetails.text = details
//            binding.root.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe {
//                disableOnResume(requireActivity().javaClass)
//                FileUtility.openFile(binding.root.context, file.path)
//            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.absolutePath == newItem.absolutePath
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem == newItem
        }
    }

}