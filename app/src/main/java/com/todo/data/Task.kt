package com.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val creationTime: String,
    val dueTime: String,
    var isCompleted: Boolean = false, // Ensure default value is false
    val hasNotification: Boolean,
    val category: String,
    val attachmentPath: String?
)