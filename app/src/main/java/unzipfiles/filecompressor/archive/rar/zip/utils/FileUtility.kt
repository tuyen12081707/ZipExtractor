package unzipfiles.filecompressor.archive.rar.zip.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import unzipfiles.filecompressor.archive.rar.zip.R
import java.io.File

object FileUtility {

    private fun getFileList(path: String): Array<File>? {
        val file = File(path)
        return if (!file.isDirectory) null
        else file.listFiles()
    }

    suspend fun scanForFile(
        files: MutableList<File>, type: String,
        path: String = Constants.EXTERNAL_DIRECTORY.path
    ) {
        val list = getFileList(path)
        if (list != null) {
            for (file in list) {
                if (file.isDirectory) {
                    scanForFile(files, type, file.absolutePath)
                } else {
                    if (file.isHidden) continue
                    when (type) {
                        Type.DOCUMENTS.name -> if (isDocument(file)) files.add(file)
                        Type.PDF.name -> if (isPdf(file)) files.add(file)
                        Type.EXCEL.name -> if (isExcel(file)) files.add(file)
                        Type.PPT.name -> if (isPpt(file)) files.add(file)
                        Type.WORD.name -> if (isWord(file)) files.add(file)
                        Type.TXT.name -> if (isTxt(file)) files.add(file)

                        Type.DOWNLOAD.name -> files.add(file)
                        Type.ARCHIVER.name -> if (isArchiver(file)) files.add(file)
                        Type.AUDIOS.name -> if (isAudio(file)) files.add(file)
                        Type.VIDEOS.name -> if (isVideo(file)) files.add(file)
                        Type.IMAGES.name -> if (isImage(file)) files.add(file)
                    }
                }
            }
        }
    }

    fun isArchiver(file: File): Boolean {
        return file.path.endsWith(".zip") ||
                file.path.endsWith(".rar") ||
                file.path.endsWith(".7z") ||
                file.path.endsWith(".bz2") ||
                file.path.endsWith(".bzip2") ||
                file.path.endsWith(".tbz2") ||
                file.path.endsWith(".tbz") ||
                file.path.endsWith(".gz") ||
                file.path.endsWith(".gzip") ||
                file.path.endsWith(".tgz") ||
                file.path.endsWith(".tar") ||
                file.path.endsWith(".wim") ||
                file.path.endsWith(".swm") ||
                file.path.endsWith(".xz") ||
                file.path.endsWith(".txz") ||
                file.path.endsWith(".zipx") ||
                file.path.endsWith(".jar") ||
                file.path.endsWith(".xpi")
    }

    fun isAudio(file: File): Boolean {
        return file.path.endsWith(".mp3") ||
                file.path.endsWith(".wav") ||
                file.path.endsWith(".m4a") ||
                file.path.endsWith(".wma") ||
                file.path.endsWith(".ogg") ||
                file.path.endsWith(".aac") ||
                file.path.endsWith(".amr") ||
                file.path.endsWith(".flac")
    }

    fun isDocument(file: File): Boolean {
        return isPdf(file) || isTxt(file) || isWord(file) || isExcel(file) || isPpt(file)
    }

    fun isPdf(file: File): Boolean {
        return file.path.endsWith(".pdf")
    }

    fun isTxt(file: File): Boolean {
        return file.path.endsWith(".txt")
    }

    fun isWord(file: File): Boolean {
        return file.path.endsWith(".doc") ||
                file.path.endsWith(".docm") ||
                file.path.endsWith(".dot") ||
                file.path.endsWith(".docx")
    }

    fun isExcel(file: File): Boolean {
        return file.path.endsWith(".xls") ||
                file.path.endsWith(".xlsx") ||
                file.path.endsWith(".xlsb") ||
                file.path.endsWith(".xltx")
    }

    fun isPpt(file: File): Boolean {
        return file.path.endsWith(".ppt") ||
                file.path.endsWith(".pptx") ||
                file.path.endsWith(".pptm")
    }

    fun isVideo(file: File): Boolean {
        return file.path.endsWith(".mp4") ||
                file.path.endsWith(".wmv") ||
                file.path.endsWith(".hevc") ||
                file.path.endsWith(".avi") ||
                file.path.endsWith(".mov") ||
                file.path.endsWith(".f4v") ||
                file.path.endsWith(".mkv") ||
                file.path.endsWith(".ts") ||
                file.path.endsWith(".3gp") ||
                file.path.endsWith(".mpeg-2") ||
                file.path.endsWith(".webm")
    }

    fun isImage(file: File): Boolean {
        return file.path.endsWith(".jpg") ||
                file.path.endsWith(".jpeg") ||
                file.path.endsWith(".png") ||
                file.path.endsWith(".gif") ||
                file.path.endsWith(".bmp") ||
                file.path.endsWith(".heic") ||
                file.path.endsWith(".heif") ||
                file.path.endsWith(".webp")
    }

    fun isApk(file: File): Boolean {
        return file.path.endsWith(".apk")
    }

    fun openFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )
            val mime = context.contentResolver.getType(uri)
//            mime?.log()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setDataAndType(uri, mime)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } catch (e: Exception) {
            context.toast(context.getString(R.string.file_error))
        }
    }

    fun Context.share(list: List<File>, context: Context) {
        val uris = arrayListOf<Uri>()
//        val uri = Uri.parse("file://${file.path}")
//        uris.addAll(list.map { Uri.parse("file://${it.path}") })
uris.addAll(list.map { FileProvider.getUriForFile(
    context,
    "unzipfiles.filecompressor.archive.rar.zip.provider",
    it) })
        val intent = Intent()
            .setAction(Intent.ACTION_SEND_MULTIPLE)
            .setType("*/*")
            .putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        startActivity(intent)
    }

}