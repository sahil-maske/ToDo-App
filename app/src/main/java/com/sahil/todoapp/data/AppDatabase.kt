package com.sahil.todoapp.data


import androidx.room.Database
import androidx.room.RoomDatabase
import com.sahil.todoapp.data.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskEntityDao
}