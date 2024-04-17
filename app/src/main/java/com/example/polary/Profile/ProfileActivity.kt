package com.example.polary.Profile

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.polary.Class.HttpMethod
import com.bumptech.glide.Glide
import com.example.polary.R
import com.example.polary.dataClass.Profile
import com.example.polary.utils.ApiCallBack

class ProfileActivity : AppCompatActivity() {
    private val userId = "1"
    private lateinit var  user: Profile
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
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}