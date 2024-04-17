package com.example.polary

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.polary.Photo.TakePhotoActivity
import com.example.polary.authentication.SignIn
import com.example.polary.authentication.SignUpMain
import com.example.polary.utils.SessionManager
import com.example.polary.widgets.PolaryWidget
import com.google.android.material.button.MaterialButton

class OnBoarding : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // Enable dark mode

        sessionManager = SessionManager(getSharedPreferences("user", MODE_PRIVATE))

        val isLoggedIn = sessionManager.isLoggedIn()

        if (isLoggedIn) {
            val intent = Intent(this, TakePhotoActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            setContentView(R.layout.activity_on_boarding)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            val signInButton = findViewById<MaterialButton>(R.id.signInMatBtn)
            signInButton.setOnClickListener {
                val intent = Intent(this, SignIn::class.java)
                startActivity(intent)
            }

            val signUpButton = findViewById<MaterialButton>(R.id.signUpMatBtn)
            signUpButton.setOnClickListener {
                val intent = Intent(this, SignUpMain::class.java)
                startActivity(intent)
            }
        }

        // Update widgets
        val polaryWidget = PolaryWidget()
        polaryWidget.updateWidgets(this)
    }
}