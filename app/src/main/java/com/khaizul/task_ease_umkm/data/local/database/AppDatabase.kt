package com.khaizul.task_ease_umkm.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.khaizul.task_ease_umkm.data.local.dao.TaskDao
import com.khaizul.task_ease_umkm.data.local.entity.TaskEntity
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date


@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(AppDatabase.Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_ease_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
    class Converters {
        @TypeConverter
        fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

        @TypeConverter
        fun dateToTimestamp(date: Date?): Long? = date?.time
    }

}