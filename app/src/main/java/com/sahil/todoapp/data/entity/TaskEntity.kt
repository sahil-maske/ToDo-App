package com.sahil.todoapp.data.entity

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val description: String,
    val isComplete: Boolean,

    val createdAt : Long = System.currentTimeMillis()

)