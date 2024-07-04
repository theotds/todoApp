package com.todo.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.todo.NotificationWorker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class TaskRepository(private val taskDao: TaskDao, private val context: Context) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()
    val hideCompletedTasks: LiveData<List<Task>> = taskDao.getIncompleteTasks()

    suspend fun insert(task: Task, notificationTimingMinutes: Int) {
        taskDao.insert(task)
        scheduleNotification(task, notificationTimingMinutes, context)
    }

    suspend fun update(task: Task, notificationTimingMinutes: Int) {
        taskDao.update(task)
        scheduleNotification(task, notificationTimingMinutes ,context)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task.id)
    }

    fun searchTasksByCategory(category: String): LiveData<List<Task>> {
        return taskDao.searchTasksByCategory("%$category%")
    }

    fun getFilteredTasks(showCompleted: Boolean): LiveData<List<Task>> {
        return if (showCompleted) {
            taskDao.getTasksFilteredByCompletion(false) // Only active tasks
        } else {
            allTasks // All tasks
        }
    }

    private fun scheduleNotification(task: Task, notificationTimingMinutes: Int, context: Context) {
        val taskTime = LocalDateTime.parse(task.dueTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val notificationTime = taskTime.minusMinutes(notificationTimingMinutes.toLong())
        val now = LocalDateTime.now()

        val delay = java.time.Duration.between(now, notificationTime).toMillis()
        Log.d("scheduled at","$delay")
        if (delay > 0) {
            val data = Data.Builder()
                .putInt("task_id", task.id)
                .putString("task_title", task.title)
                .putInt("task_time", notificationTimingMinutes)
                .build()

            val notificationRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueue(notificationRequest)
        }
    }


}
