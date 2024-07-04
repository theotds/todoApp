package com.todo.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.todo.databinding.TaskItemBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onEditClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit,
    private val onTaskStatusChange: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, onEditClick, onDeleteClick, onTaskStatusChange)
    }

    class TaskViewHolder(
        private val binding: TaskItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task, onEditClick: (Task) -> Unit, onDeleteClick: (Task) -> Unit, onTaskStatusChange: (Task) -> Unit) {
            binding.apply {
                tvTaskTitle.text = task.title
                tvTaskDescription.text = task.description
                tvTaskCreationTime.text = task.creationTime
                tvTaskDueTime.text = task.dueTime
                tvTaskCategory.text = task.category

                // Remove old listeners before setting checked state
                checkBoxDone.setOnCheckedChangeListener(null)
                checkBoxDone.isChecked = task.isCompleted

                val currentDate = Date()
                val taskDueDate = parseDate(task.dueTime)
                checkBoxDone.isEnabled = taskDueDate.after(currentDate)

                // Set new listener after state is set
                checkBoxDone.setOnCheckedChangeListener { _, isChecked ->
                    if (checkBoxDone.isEnabled) {
                        task.isCompleted = isChecked
                        onTaskStatusChange(task) // Notify that task status has changed
                    }
                }

                btnEditTask.setOnClickListener {
                    onEditClick(task)
                }

                btnRemoveTask.setOnClickListener {
                    onDeleteClick(task)
                }
            }
        }

        private fun parseDate(dateString: String): Date {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            return format.parse(dateString) ?: Date()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
