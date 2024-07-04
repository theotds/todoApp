package com.todo.fragments

import android.content.Context
import androidx.lifecycle.*
import com.todo.data.Task
import com.todo.data.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val allTasks: LiveData<List<Task>> = repository.allTasks
    val hideCompletedTasks: LiveData<List<Task>> = repository.hideCompletedTasks
    private val showCompleted = MutableLiveData<Boolean>()

    fun insert(task: Task, notificationTimingMinutes: Int) = viewModelScope.launch {
        repository.insert(task, notificationTimingMinutes)
    }

    fun update(task: Task, notificationTimingMinutes: Int) = viewModelScope.launch {
        repository.update(task, notificationTimingMinutes)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }

    fun setShowCompleted(show: Boolean) {
        showCompleted.value = show
    }

    fun searchTasksByCategory(category: String): LiveData<List<Task>> {
        return repository.searchTasksByCategory(category)
    }
    init {
        setShowCompleted(true)
    }
}

class TaskViewModelFactory(private val repository: TaskRepository, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}