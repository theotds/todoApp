package com.todo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 2, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                    .fallbackToDestructiveMigration() // This line will drop and recreate the database if no migration is provided
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
