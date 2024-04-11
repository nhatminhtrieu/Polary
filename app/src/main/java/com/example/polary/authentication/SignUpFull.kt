package com.example.polary.authentication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.dataClass.User
import com.example.polary.ultils.ApiCallBack
import com.example.polary.utils.Validate
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpFull : AppCompatActivity() {
    private val httpMethod = HttpMethod()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_full)

        val email = intent.getStringExtra("email")
        val username: TextInputEditText = findViewById(R.id.username)
        val password: TextInputEditText = findViewById(R.id.password)
        val confirmPassword: TextInputEditText = findViewById(R.id.confirm_password)
        val signUpButton: MaterialButton = findViewById(R.id.signUp)

        findViewById<TextInputEditText>(R.id.email).apply {
            setText(email)
            isEnabled = false
        }

        signUpButton.setOnClickListener {
            val passwordText = password.text.toString()
            val confirmPasswordText = confirmPassword.text.toString()

            if (!Validate.validatePassword(passwordText, confirmPasswordText)) {
                findViewById<TextInputLayout>(R.id.password_layout).apply {
                    isErrorEnabled = true
                    error = "Passwords do not match"

                    Handler(Looper.getMainLooper()).postDelayed({
                        isErrorEnabled = false
                        error = null
                    }, 2000)
                }

                return@setOnClickListener
            }

            // how to save user sign in with google to my database
            if (Validate.validateSignUp(email, passwordText) {}) {
                val auth = Firebase.auth
                auth.createUserWithEmailAndPassword(email!!, passwordText)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = auth.currentUser
                            val user = User(
                                firebaseUID = firebaseUser?.uid ?: "",
                                username = username.text.toString(),
                                email = email,
                                password = passwordText
                            )

                            val callback = object : ApiCallBack<Any> {
                                override fun onSuccess(data: Any) {
                                    Intent(
                                        this@SignUpFull,
                                        SignIn::class.java
                                    ).also { startActivity(it) }
                                }

                                override fun onError(error: Throwable) {
                                }
                            }

                            httpMethod.doPost("users/", user, callback)
                        }
                    }
            }
        }
    }
}