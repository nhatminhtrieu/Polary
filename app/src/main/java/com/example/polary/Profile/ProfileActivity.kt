package com.example.polary.Profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.polary.R
import com.example.polary.authentication.SignIn
import com.example.polary.widgets.PolaryWidget
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)

        val signOutBtn = findViewById<TextView>(R.id.signOutBtn)
        signOutBtn.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        with(sharedPreferences.edit()) {
            clear()
            putBoolean("isLoggedIn", false)
            apply()
        }

        FirebaseAuth.getInstance().signOut()

        // Update widget
        val context = applicationContext
        val intent = Intent(applicationContext, PolaryWidget::class.java).apply {
            action = PolaryWidget.ACTION_UPDATE_WIDGET
        }
        context.sendBroadcast(intent)

        val intentSignIn = Intent(this, SignIn::class.java)
        startActivity(intentSignIn)
        finish()
    }
}