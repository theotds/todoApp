package com.todo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            viewPager = findViewById(R.id.viewPager)
            tabLayout = findViewById(R.id.tabLayout)

            val viewPagerAdapter = ViewPagerAdapter(this)
            viewPager.adapter = viewPagerAdapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "Main"
                    1 -> tab.text = "Settings"
                }
            }.attach()

            if (savedInstanceState != null) {
                viewPager.currentItem = savedInstanceState.getInt("currentTab", 0)
            }

    }
}
