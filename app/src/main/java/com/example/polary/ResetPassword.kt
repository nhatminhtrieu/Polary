package com.example.polary

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ResetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val email = findViewById<TextInputEditText>(R.id.email)
        val emailLayout = findViewById<TextInputLayout>(R.id.email_layout)

        val continueMatBtn = findViewById<MaterialButton>(R.id.continueMatBtn)
        continueMatBtn.setOnClickListener {
            val emailText = email.text.toString()
            if (emailText.isEmpty() || !Validate.validateEmail(emailText)) {
                emailLayout.isErrorEnabled = true
                emailLayout.error = "Please enter a valid email address"
                return@setOnClickListener
            }
            val intent = Intent(this, VerifyOTP::class.java)
            intent.putExtra("email", emailText)
            intent.putExtra("message", "resetPassword")
            startActivity(intent)
        }

    }
}