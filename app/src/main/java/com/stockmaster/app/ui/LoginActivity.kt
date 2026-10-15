package com.stockmaster.app.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.stockmaster.app.StockMasterApp
import com.stockmaster.app.data.Session
import com.stockmaster.app.databinding.ActivityLoginBinding
import com.stockmaster.app.util.PasswordHasher
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val pin = StringBuilder()
    private var busy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Already signed in? Skip the door entirely.
        if (Session.currentStaff != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val keys = listOf(
            binding.key1, binding.key2, binding.key3,
            binding.key4, binding.key5, binding.key6,
            binding.key7, binding.key8, binding.key9,
            binding.key0
        )

        keys.forEach { key ->
            key.setOnClickListener { addDigit(key.text.toString()) }
        }

        binding.keyBack.setOnClickListener {
            if (pin.isNotEmpty()) {
                pin.deleteCharAt(pin.length - 1)
                renderPin()
            }
        }
        binding.keyBack.setOnLongClickListener {
            pin.clear()
            renderPin()
            true
        }

        renderPin()
    }

    private fun addDigit(d: String) {
        if (busy || pin.length >= 4) return
        pin.append(d)
        renderPin()
        if (pin.length == 4) attemptLogin()
    }

    private fun attemptLogin() {
        busy = true
        lifecycleScope.launch {
            val staffDao = StockMasterApp.instance.database.staffDao()

            // Try the typed PIN against every staff member's salt+hash
            val staff = staffDao.getAllStaffOnce().firstOrNull { candidate ->
                PasswordHasher.verify(pin.toString(), candidate.salt, candidate.pin)
            }

            if (staff != null) {
                Session.currentStaff = staff
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                binding.errorText.text = "Incorrect PIN"
                shake(binding.pinDots)
                pin.clear()
                renderPin()
                busy = false
            }
        }
    }

    private fun shake(v: View) {
        ObjectAnimator.ofFloat(
            v, "translationX",
            0f, -18f, 18f, -12f, 12f, -6f, 6f, 0f
        ).start()
    }

    private fun renderPin() {
        val dots = listOf(binding.dot1, binding.dot2, binding.dot3, binding.dot4)
        dots.forEachIndexed { i, tv ->
            tv.alpha = if (i < pin.length) 1f else 0.25f
        }
        if (pin.isEmpty()) binding.errorText.text = ""
    }
}
