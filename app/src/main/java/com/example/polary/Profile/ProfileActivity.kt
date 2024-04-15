package com.example.polary.Profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.polary.Class.HttpMethod
import com.bumptech.glide.Glide
import com.example.polary.R
import com.example.polary.authentication.SignIn
import com.example.polary.widgets.PolaryWidget
import com.google.firebase.auth.FirebaseAuth
import com.example.polary.dataClass.Profile
import com.example.polary.utils.ApiCallBack

class ProfileActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        HttpMethod().doGet<Profile>("/users/$userId", object : ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                user = data as Profile
                renderData()
                Log.d("ProfileActivity", "Successfully fetched user data ${user.username}")
            }
            override fun onError(error: Throwable) {
                Log.e("ProfileActivity", "Failed to fetch user data: $error")
            }
        })

        val usernameFragment = UsernameFragment()
        findViewById<android.widget.LinearLayout>(R.id.username_profile_item).setOnClickListener {
            usernameFragment.show(supportFragmentManager, UsernameFragment.TAG)
        }
    }

    private fun renderData() {
        findViewById<TextView>(R.id.usernameProfile).text = user.username
        if (user.avatar !== null) {
            findViewById<ImageView>(R.id.avatarProfile).apply {
                Glide.with(this@ProfileActivity)
                    .load(user.avatar)
                    .into(this)
            }
        }

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