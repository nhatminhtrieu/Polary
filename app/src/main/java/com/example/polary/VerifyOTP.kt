package com.example.polary

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.polary.utils.applyClickableSpan
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class VerifyOTP : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        applyClickableSpan(findViewById(R.id.resendOTP), "Resend OTP", this, VerifyOTP::class.java)

        val expectedOTP = "123456" // change later
        val email = intent.getStringExtra("email")

        val announceText: MaterialTextView = findViewById(R.id.announcement)
        announceText.text = announceText.text.toString().replace("your email address", email.toString())

        val otpFields = arrayOf<TextInputEditText>(
            findViewById(R.id.otp1),
            findViewById(R.id.otp2),
            findViewById(R.id.otp3),
            findViewById(R.id.otp4),
            findViewById(R.id.otp5),
            findViewById(R.id.otp6)
        )

        for (i in otpFields.indices) {
            otpFields[i].addTextChangedListener(createTextWatcher(otpFields[(i + 1) % otpFields.size]))
        }

        val submitButton: MaterialButton = findViewById(R.id.submit)
        submitButton.setOnClickListener {
            val enteredOTP = otpFields.joinToString("") { it.text.toString() }
            if (email != null) {
                validateOTP(enteredOTP, expectedOTP, email)
            }
        }
    }

    private fun createTextWatcher(nextEditText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    nextEditText.requestFocus()
                }
            }
        }
    }

    private fun validateOTP(enteredOTP: String, expectedOTP: String, email: String) {
        if (enteredOTP == expectedOTP) {
            val intent = Intent(this, SignUpFull::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
        }
    }
}