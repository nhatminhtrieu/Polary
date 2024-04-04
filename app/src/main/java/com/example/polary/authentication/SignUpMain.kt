package com.example.polary.authentication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.polary.R
import com.example.polary.utils.Validate
import com.example.polary.utils.applyClickableSpan
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUpMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val signUpButton: MaterialButton = findViewById(R.id.signUp)
        signUpButton.setOnClickListener {
            val emailLayout: TextInputLayout = findViewById(R.id.email_layout)
            val emailEditText: TextInputEditText = findViewById(R.id.email)
            val email = emailEditText.text.toString()

            if (email.isEmpty() || !Validate.validateEmail(email)) {
                emailLayout.isErrorEnabled = true
                emailLayout.error = "Please enter a valid email address"
                return@setOnClickListener
            }

            val intent = Intent(this, VerifyOTP::class.java)
            intent.putExtra("email", email)
            intent.putExtra("message", "signUp")
            startActivity(intent)
        }

        applyClickableSpan(findViewById(R.id.signIn_text), "Sign in", this, SignIn::class.java)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpTo(this, intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}