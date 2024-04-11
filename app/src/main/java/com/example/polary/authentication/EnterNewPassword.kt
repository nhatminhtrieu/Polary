package com.example.polary.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.ultils.ApiCallBack
import com.example.polary.utils.Validate
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

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
                val requestBody = mapOf(
                    "email" to intent.getStringExtra("email"),
                    "password" to password.text.toString()
                )
                updatePassword(requestBody)
            } else {
                showError()
            }
        }
    }

    private fun updatePassword(requestBody: Any) {
        lifecycleScope.launch {
            try {
                val httpMethod = HttpMethod()
                httpMethod.doPost("auth/update-password", requestBody, object : ApiCallBack<Any> {
                    override fun onSuccess(data: Any) {
                        val intent = Intent(this@EnterNewPassword, SignIn::class.java)
                        startActivity(intent)
                    }

                    override fun onError(error: Throwable) {
                        // Handle error
                    }
                })
            } catch (error: Throwable) {
                // Handle error
            }
        }
    }

    private fun showError() {
        val passwordLayout = findViewById<TextInputLayout>(R.id.password_layout)
        val confirmPasswordLayout = findViewById<TextInputLayout>(R.id.confirm_password_layout)
        passwordLayout.isErrorEnabled = true
        passwordLayout.error = "Passwords do not match"
        confirmPasswordLayout.isErrorEnabled = true
        confirmPasswordLayout.error = "Passwords do not match"

        // Remove error after 2 seconds
        passwordLayout.postDelayed({
            passwordLayout.isErrorEnabled = false
            passwordLayout.error = null
            confirmPasswordLayout.isErrorEnabled = false
            confirmPasswordLayout.error = null
        }, 2000) // 2000 milliseconds = 2 seconds
    }
}