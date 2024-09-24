package com.goldcompany.apps.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.goldcompany.apps.data.db.converter.LongConverter
import com.goldcompany.apps.data.db.schedule.ScheduleDao
import com.goldcompany.apps.data.db.schedule.ScheduleEntity
import com.goldcompany.apps.data.db.todo.TodoDao
import com.goldcompany.apps.data.db.todo.TodoEntity

@Database(entities = [TodoEntity::class, ScheduleEntity::class], version = 1, exportSchema = false)
@TypeConverters(LongConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        fun getInstance(context: Context): AppDatabase {
            return buildDatabase(context)
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "tasks.db")
                .build()
        }
    }
}