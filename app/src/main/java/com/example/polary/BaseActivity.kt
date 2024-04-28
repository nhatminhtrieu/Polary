package com.example.polary

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)

        // Get current theme of activity
        val currentThemeResId = sharedPreferences.getInt("themeResId", R.style.AppTheme_Dark)

        setTheme(currentThemeResId)

        super.onCreate(savedInstanceState)
    }

    fun switchTheme(theme: Int) {
        sharedPreferences.edit().putInt("themeResId", theme).apply()
        recreate()
    }
}