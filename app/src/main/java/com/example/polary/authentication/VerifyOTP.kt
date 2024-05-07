package com.example.polary.authentication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.polary.BaseActivity
import com.example.polary.R
import com.example.polary.utils.Validate
import com.example.polary.utils.applyClickableSpan
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch

class VerifyOTP : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)
        configTopAppBar()

        applyClickableSpan(findViewById(R.id.resendOTP), getString(R.string.resent_otp), this, ResetPassword::class.java)

        val email = intent.getStringExtra("email")
        val message = intent.getStringExtra("message")

        val announceText: MaterialTextView = findViewById(R.id.announcement)
        announceText.text =
            announceText.text.toString().replace("your email address", email.toString())

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
            lifecycleScope.launch {
                val isValid = Validate.validateOTP(email!!, enteredOTP)
                if (isValid) {
                    when (message) {
                        "signUp" -> startActivity(Intent(this@VerifyOTP, SignUpFull::class.java).apply {
                            putExtra("email", email)
                        })

                        "resetPassword" -> startActivity(
                            Intent(
                                this@VerifyOTP,
                                EnterNewPassword::class.java
                            ).apply {
                            putExtra("email", email)
                        })
                    }
                }
            }
        }
    }
    private fun configTopAppBar() {
        val appBar = findViewById<MaterialToolbar>(R.id.app_top_app_bar)
        appBar?.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_back)
        appBar?.setNavigationOnClickListener {
            // Go back to the parent activity
            finish()
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
}
