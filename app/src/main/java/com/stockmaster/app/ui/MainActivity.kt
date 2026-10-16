package com.stockmaster.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stockmaster.app.R
import com.stockmaster.app.data.Session
import com.stockmaster.app.databinding.ActivityMainBinding
import com.stockmaster.app.ui.dashboard.DashboardFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> { show(DashboardFragment()); true }
                R.id.nav_inventory -> { show(makePlaceholder("Stock")); true }
                R.id.nav_sell      -> { show(makePlaceholder("Sell")); true }
                R.id.nav_customers -> { show(makePlaceholder("Customers")); true }
                R.id.nav_more      -> { show(makePlaceholder("More")); true }
                else -> false
            }
        }

        // Home base on first launch
        if (savedInstanceState == null) {
            show(DashboardFragment())
        }
    }

    // Dashboard's quick-action buttons land here: flip the nav switch, the listener does the rest
    fun navigateTo(itemId: Int) {
        binding.bottomNav.selectedItemId = itemId
    }

    private fun show(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Signpost screen until its real batch arrives — built in code, needs no XML
    private fun makePlaceholder(title: String): Fragment {
        return object : Fragment() {
            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View {
                val root = FrameLayout(requireContext())
                val label = TextView(requireContext())
                label.text = "$title 🚧 under construction — arrives in a future batch"
                label.textSize = 18f
                label.gravity = Gravity.CENTER
                root.addView(
                    label,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                    )
                )
                return root
            }
        }
    }
}
