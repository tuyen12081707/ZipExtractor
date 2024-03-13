package unzipfiles.filecompressor.archive.rar.zip.database

import androidx.room.Entity
import androidx.room.PrimaryKey

const val TYPE_COMPRESS = 1
const val TYPE_EXTRACT = 2

@Entity
data class History(
    @PrimaryKey
    val path: String,
    val timeStamp: Long,
    val type: Int
)
