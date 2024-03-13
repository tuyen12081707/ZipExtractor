package unzipfiles.filecompressor.archive.rar.zip.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert(onConflict = REPLACE)
    fun saveExtracted(list: List<History>)

    @Query("SELECT * FROM history")
    fun getAll(): LiveData<List<History>>

    @Query("SELECT * FROM history WHERE type = 1")
    fun getAllCompressed(): LiveData<List<History>>

    @Query("SELECT * FROM history WHERE type = 2")
    fun getAllExtracted(): LiveData<List<History>>

    @Query("SELECT * FROM history WHERE type = :type")
    fun getAllByType(type: Int): List<History>


    @Query("UPDATE History SET path=:newPath WHERE path = :oldPath")
    fun update(oldPath: String, newPath: String)

    @Delete
    fun delete(history: History)

    @Query("DELETE FROM history WHERE path = :path")
    fun delete(path: String)
}