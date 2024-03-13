package unzipfiles.filecompressor.archive.rar.zip.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import unzipfiles.filecompressor.archive.rar.zip.utils.Type

@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private val DATABASE_NAME = "zip_opener.db"
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }

        fun saveHistory(context: Context, list: List<History>) {
            getInstance(context).historyDao().saveExtracted(list)
        }

        fun getALlAsLiveData(context: Context, type: String?): LiveData<List<History>> {
            return if (type == Type.COMPRESSED.name) getInstance(context).historyDao()
                .getAllCompressed()
            else getInstance(context).historyDao().getAllExtracted()
        }

        fun getALl(context: Context, type: String?): List<History> {
            return if (type == Type.COMPRESSED.name) {
                getInstance(context).historyDao().getAllByType(1)
            } else {
                getInstance(context).historyDao().getAllByType(2)
            }
        }

        fun delete(context: Context, history: History) {
            getInstance(context).historyDao().delete(history)
        }

        fun delete(context: Context, path: String) {
            getInstance(context).historyDao().delete(path)
        }

        fun update(context: Context, oldPath: String, newPath: String) {
            getInstance(context).historyDao().update(oldPath, newPath)
        }

    }

    abstract fun historyDao(): HistoryDao
}