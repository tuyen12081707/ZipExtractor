package unzipfiles.filecompressor.archive.rar.zip.utils

import android.os.Environment

enum class Type { ARCHIVER, DOWNLOAD, DOCUMENTS, AUDIOS, VIDEOS, IMAGES, COMPRESSED, EXTRACTED, PDF, EXCEL, PPT, WORD, TXT }

object Constants {
    const val EXTRA_PATH = "intent_extra_path"
    const val EXTRA_TYPE = "intent_extra_file_type"
    const val IS_SHOW_RATE = "prefs_is_show_rate"

    val EXTERNAL_DIRECTORY = Environment.getExternalStorageDirectory()
    val DOCUMENT_DIRECTORY =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    val DOWLOAD_DIRECTORY =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

}