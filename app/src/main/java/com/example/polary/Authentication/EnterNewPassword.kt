package com.example.polary.Authentication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.polary.R
import com.example.polary.utils.Validate
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EnterNewPassword : AppCompatActivity() {
    private lateinit var password: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var saveMatBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_new_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm_password)
        saveMatBtn = findViewById(R.id.save)

        saveMatBtn.setOnClickListener {
            if (Validate.validatePassword(
                    password.text.toString(),
                    confirmPassword.text.toString()
                )
            ) {
                // implement later
                val intent = Intent(this, SignIn::class.java)
                startActivity(intent)
            } else {
                // error on text input
                val passwordLayout = findViewById<TextInputLayout>(R.id.password_layout)
                val confirmPasswordLayout = findViewById<TextInputLayout>(R.id.confirm_password_layout)
                passwordLayout.isErrorEnabled = true
                passwordLayout.error = "Passwords do not match"
                confirmPasswordLayout.isErrorEnabled = true
                confirmPasswordLayout.error = "Passwords do not match"

                // Remove error after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    passwordLayout.isErrorEnabled = false
                    passwordLayout.error = null
                    confirmPasswordLayout.isErrorEnabled = false
                    confirmPasswordLayout.error = null
                }, 2000) // 2000 milliseconds = 2 seconds
            }
        }
    }
}