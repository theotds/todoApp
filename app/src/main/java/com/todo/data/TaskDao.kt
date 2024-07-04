package com.todo.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("DELETE FROM task_table WHERE id = :taskId")
    suspend fun delete(taskId: Int)

    @Query("SELECT * FROM task_table ORDER BY isCompleted, dueTime")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task_table WHERE category LIKE :category ORDER BY isCompleted, dueTime")
    fun searchTasksByCategory(category: String): LiveData<List<Task>>

    @Query("SELECT * FROM task_table WHERE isCompleted = 0 ORDER BY dueTime ASC")
    fun getIncompleteTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task_table WHERE isCompleted = :isCompleted")
    fun getTasksFilteredByCompletion(isCompleted: Boolean): LiveData<List<Task>>

}
