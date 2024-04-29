package com.example.polary.Profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.authentication.SignIn
import com.example.polary.widgets.PolaryWidget
import com.google.firebase.auth.FirebaseAuth
import com.example.polary.dataClass.Profile
import com.example.polary.dataClass.User
import com.example.polary.friends.FriendsActivity
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.SessionManager
import com.google.android.material.button.MaterialButton


class ProfileActivity : AppCompatActivity(), AvatarFragment.OnAvatarFragmentListener, UsernameFragment.OnUsernameFragmentListener{
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: User
    private lateinit var profile: Profile

    override fun onAvatarUpdated() {
        getUserProfile()
    }
    override fun onUsernameUpdated() {
        getUserProfile()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!
        getUserProfile()

        val cameraBtn = findViewById<MaterialButton>(R.id.btn_camera)
        cameraBtn.bringToFront()
        cameraBtn.setOnClickListener {
            val avatarFragment = AvatarFragment()
            avatarFragment.show(supportFragmentManager, AvatarFragment.TAG)
        }

        findViewById<android.widget.LinearLayout>(R.id.log_out_profile_item).setOnClickListener {
            signOut()
        }

        val usernameFragment = UsernameFragment()
        findViewById<android.widget.LinearLayout>(R.id.username_profile_item).setOnClickListener {
            usernameFragment.show(supportFragmentManager, UsernameFragment.TAG)
        }

        val passwordFragment = PasswordFragment()
        findViewById<android.widget.LinearLayout>(R.id.password_profile_item).setOnClickListener {
            passwordFragment.show(supportFragmentManager, PasswordFragment.TAG)
        }

        findViewById<android.widget.LinearLayout>(R.id.friends_profile_item).setOnClickListener {
            val intent = Intent(this, FriendsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getUserProfile() {
        val httpMethod = HttpMethod()
        httpMethod.doGet<Profile>("/users/${user.id}", object : ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                profile = data as Profile
                renderData()
                Log.d("ProfileActivity", "Successfully fetched user data ${profile.username}")
            }
            override fun onError(error: Throwable) {
                Log.e("ProfileActivity", "Failed to fetch user data: $error")
            }
        })
    }
    private fun renderData() {
        findViewById<TextView>(R.id.usernameProfile).text = profile.username
        if (profile.avatar !== null) {
            findViewById<ImageView>(R.id.avatarProfile).apply {
                Glide.with(this@ProfileActivity)
                    .load(profile.avatar)
                    .into(this)
            }
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

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}