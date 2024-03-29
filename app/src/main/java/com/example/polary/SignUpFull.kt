package com.example.polary

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUpFull : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_full)

        val email = intent.getStringExtra("email")
        val password: TextInputEditText = findViewById(R.id.password)
        val confirmPassword: TextInputEditText = findViewById(R.id.confirm_password)
        val signUpButton: MaterialButton = findViewById(R.id.signUp)

        findViewById<TextInputEditText>(R.id.email).apply {
            setText(email)
            isEnabled = false
        }

        signUpButton.setOnClickListener {
            val passwordText = password.text.toString()
            val confirmPasswordText = confirmPassword.text.toString()

            if (!Validate.validatePassword(passwordText, confirmPasswordText)) {
                findViewById<TextInputLayout>(R.id.password_layout).apply {
                    isErrorEnabled = true
                    error = "Passwords do not match"

                    Handler(Looper.getMainLooper()).postDelayed({
                        isErrorEnabled = false
                        error = null
                    }, 2000) // 2000 milliseconds = 2 seconds
                }

                return@setOnClickListener
            }

            if (Validate.validateSignUp(email, passwordText, {})) {
                Intent(this, SignIn::class.java).also { startActivity(it) }
            }
        }
    }
}