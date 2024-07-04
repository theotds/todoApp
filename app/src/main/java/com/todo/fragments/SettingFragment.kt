package com.todo.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.todo.databinding.SettingFragmentBinding

class SettingFragment : Fragment() {
    private var _binding: SettingFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SettingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)

        setupHideCompletedSwitch(prefs)
        setupNotificationTimingSpinner(prefs)
    }

    private fun setupHideCompletedSwitch(prefs: SharedPreferences) {
        binding.switchHideCompletedTasks.isChecked = prefs.getBoolean("hideCompleted", false)
        binding.switchHideCompletedTasks.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("hideCompleted", isChecked).apply()
        }
    }

    private fun setupNotificationTimingSpinner(prefs: SharedPreferences) {
        val timingOptions = arrayOf("2 minutes before", "5 minutes before", "10 minutes before", "15 minutes before", "30 minutes before", "1 hour before")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, timingOptions)
        binding.spinnerNotificationTiming.adapter = spinnerAdapter

        binding.spinnerNotificationTiming.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val timeMinutes = when (position) {
                    0 -> 2  // 2 minutes before
                    1 -> 5  // 5 minutes before
                    2 -> 10 // 10 minutes before
                    3 -> 15 // 15 minutes before
                    4 -> 30 // 30 minutes before
                    5 -> 60 // 60 minutes (1 hour) before
                    else -> 0
                }
                // Save the selected time in minutes in SharedPreferences
                prefs.edit().putInt("notificationTimingMinutes", timeMinutes).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
