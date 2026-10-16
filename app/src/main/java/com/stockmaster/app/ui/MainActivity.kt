package com.stockmaster.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.stockmaster.app.data.Session
import com.stockmaster.app.databinding.ActivityMainBinding
import com.stockmaster.app.ui.dashboard.DashboardFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Tab 1 is real, the rest are signposts until their batches arrive
    private val tabs = listOf(
        "Dashboard" to 1,
        "Sales" to 2,
        "Inventory" to 3,
        "Staff" to 4,
        "Reports" to 5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // No session? Nobody loiters in the command center.
        if (Session.currentStaff == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = tabs.size
            override fun createFragment(position: Int): Fragment =
                if (position == 0) DashboardFragment()
                else PlaceholderFragment.newInstance(tabs[position].first, tabs[position].second)
        }

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = tabs[position].first
        }.attach()
    }
}
