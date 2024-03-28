package com.example.polary

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText

class SignInActivity : AppCompatActivity() {
    private lateinit var usernameEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
