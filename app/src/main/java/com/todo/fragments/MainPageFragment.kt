package com.todo.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.todo.R
import com.todo.TaskApplication
import com.todo.data.Task
import com.todo.data.TaskAdapter
import com.todo.databinding.MainPageFragmentBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainPageFragment : Fragment() {
    private var _binding: MainPageFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory((requireActivity().application as TaskApplication).repository, requireContext())
    }
    private lateinit var prefs: SharedPreferences
    private lateinit var adapter: TaskAdapter
    private var notificationTimingMinutes: Int = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = MainPageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupSharedPreferences()
        setupUI(view)
    }

    private fun setupAdapter() {
        adapter = TaskAdapter(
            onEditClick = { task -> showEditTaskDialog(task) },
            onDeleteClick = { task -> viewModel.delete(task) },
            onTaskStatusChange = { task -> viewModel.update(task, notificationTimingMinutes) }
        )
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MainPageFragment.adapter
        }
    }

    private fun setupSharedPreferences() {
        prefs = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
        notificationTimingMinutes = prefs.getInt("notificationTimingMinutes", 2)
        prefs.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "hideCompleted") {
                updateTaskList()
            }
            if (key == "notificationTimingMinutes") {
                notificationTimingMinutes = sharedPreferences.getInt(key, 2)
            }
        }
        updateTaskList()  // Initial update on view creation
    }

    private fun updateTaskList() {
        val showCompleted = prefs.getBoolean("hideCompleted", true)
        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            val filteredTasks = if (!showCompleted) tasks.filter { !it.isCompleted } else tasks
            adapter.submitList(filteredTasks)
        }
    }

    private fun setupUI(view: View) {
        binding.fabAddTask.setOnClickListener {
            AddTaskDialogFragment().show(childFragmentManager, "AddTaskDialogFragment")
        }
        binding.searchViewCategory.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterTasks(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterTasks(newText)
                return false
            }
        })
    }

    private fun filterTasks(query: String?) {
        if (query.isNullOrEmpty()) {
            updateTaskList()
        } else {
            viewModel.searchTasksByCategory(query).observe(viewLifecycleOwner) { tasks ->
                val showCompleted = prefs.getBoolean("hideCompleted", true)
                val filteredTasks = if (!showCompleted) tasks.filter { !it.isCompleted } else tasks
                adapter.submitList(filteredTasks)
            }
        }
    }

    private fun showEditTaskDialog(task: Task) {
        AddTaskDialogFragment.newInstance(task).show(childFragmentManager, "EditTaskDialogFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getNotificationTimingMinutes(): Int {
        return prefs.getInt("notificationTimingMinutes", 2) // Default to 2 minutes if not set
    }
}
