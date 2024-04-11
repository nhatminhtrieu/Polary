package com.example.polary.Authentication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.polary.PostView.PostActivity
import com.example.polary.R
import com.example.polary.Photo.TakePhotoActivity
import com.example.polary.utils.applyClickableSpan
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class SignIn : AppCompatActivity() {
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var forgotPassword: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val signUpText: MaterialTextView = findViewById(R.id.signup_text)

        forgotPassword = findViewById(R.id.forgot_password)
        forgotPassword.setOnClickListener {
            val intent = Intent(this, ResetPassword::class.java)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.login).setOnClickListener {
            val intent = Intent(this, TakePhotoActivity::class.java)
            startActivity(intent)
        }

        applyClickableSpan(signUpText, "Sign up", this, SignUpMain::class.java)
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
