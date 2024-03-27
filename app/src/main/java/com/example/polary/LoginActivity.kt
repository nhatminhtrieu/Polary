package com.example.polary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        usernameEditText = findViewById(R.id.username)

        // Set up any necessary listeners here. For example:
        usernameEditText.addTextChangedListener {
            // This block will be called whenever the text changes.
            // 'text' is the new text in the EditText.
        }
    }
}