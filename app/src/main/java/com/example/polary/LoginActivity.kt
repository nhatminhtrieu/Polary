package com.example.polary

import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : BaseActivity() {
    private lateinit var usernameEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post)

//        usernameEditText = findViewById(R.id.username)
//
//        // Set up any necessary listeners here. For example:
//        usernameEditText.addTextChangedListener {
//            // This block will be called whenever the text changes.
//            // 'text' is the new text in the EditText.
//        }
    }
}