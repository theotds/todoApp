package com.todo.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.todo.R
import com.todo.TaskApplication
import com.todo.data.Task
import com.todo.databinding.AddTaskDialogBinding
import java.text.SimpleDateFormat
import java.util.*

class AddTaskDialogFragment : DialogFragment() {

    private var _binding: AddTaskDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            (requireActivity().application as TaskApplication).repository,
            requireContext()
        )    }

    private var dueDateCalendar = Calendar.getInstance()
    private var taskToEdit: Task? = null
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = AddTaskDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSharedPreferences()

        taskToEdit?.let { task ->
            binding.apply {
                editTextTitle.setText(task.title)
                editTextDescription.setText(task.description)
                editTextDueDate.setText(task.dueTime)
                editTextCategory.setText(task.category)
                buttonAdd.text = getString(R.string.update)
            }
        }

        binding.editTextDueDate.setOnClickListener {
            showDateTimePicker()
        }

        binding.buttonAdd.setOnClickListener {
            val title = binding.editTextTitle.text.toString()
            val description = binding.editTextDescription.text.toString()
            val dueDate = binding.editTextDueDate.text.toString()
            val category = binding.editTextCategory.text.toString()

            if (title.isNotEmpty() && dueDate.isNotEmpty() && category.isNotEmpty()) {
                val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val dueDateParsed = sdf.parse(dueDate)

                if (dueDateParsed != null && dueDateParsed.after(Date())) {
                    val task = Task(
                        id = taskToEdit?.id ?: 0,
                        title = title,
                        description = description,
                        creationTime = taskToEdit?.creationTime ?: currentDate,
                        dueTime = dueDate,
                        isCompleted = taskToEdit?.isCompleted ?: false,
                        hasNotification = taskToEdit?.hasNotification ?: false,
                        category = category,
                        attachmentPath = taskToEdit?.attachmentPath
                    )
                    val notificationTimingMinutes = prefs.getInt("notificationTimingMinutes", 2)
                    if (taskToEdit == null) {
                        viewModel.insert(task,notificationTimingMinutes)
                    } else {
                        viewModel.update(task,notificationTimingMinutes)
                    }
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Due date must be later than the current date and time.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSharedPreferences() {
        prefs = requireActivity().getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
    }

    private fun showDateTimePicker() {
        val currentDate = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            dueDateCalendar.set(year, month, dayOfMonth)
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                dueDateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                dueDateCalendar.set(Calendar.MINUTE, minute)
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                binding.editTextDueDate.setText(sdf.format(dueDateCalendar.time))
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show()
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(task: Task? = null): AddTaskDialogFragment {
            val fragment = AddTaskDialogFragment()
            fragment.taskToEdit = task
            return fragment
        }
    }
}
