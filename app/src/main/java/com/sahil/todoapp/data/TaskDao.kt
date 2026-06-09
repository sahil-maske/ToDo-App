package com.sahil.todoapp.data

import androidx.room.*
import com.sahil.todoapp.data.entity.TaskEntity

@Dao
interface TaskEntityDao {
    @Insert
    suspend fun insertTaskEntity(TaskEntity: TaskEntity)

    @Delete
    suspend fun deleteTaskEntity(TaskEntity: TaskEntity)

    @Update
    suspend fun  updateTaskEntity(TaskEntity: TaskEntity)

    @Query("SELECT * FROM TaskEntity")
    suspend fun getALLTaskEntity():List<TaskEntity>
}