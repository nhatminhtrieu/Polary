//package com.example.polary.authentication
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.polary.Class.HttpMethod
//import com.example.polary.R
//import com.example.polary.ultils.ApiCallBack
//import com.example.polary.utils.Validate
//import com.google.android.material.button.MaterialButton
//import com.google.android.material.textfield.TextInputEditText
//import com.google.android.material.textfield.TextInputLayout
//
//class ResetPassword : AppCompatActivity() {
//    private lateinit var httpMethod: HttpMethod
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_reset_password)
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        val email = findViewById<TextInputEditText>(R.id.email)
//        val emailLayout = findViewById<TextInputLayout>(R.id.email_layout)
//
//        val continueMatBtn = findViewById<MaterialButton>(R.id.continueMatBtn)
//        httpMethod = HttpMethod()
//
//        continueMatBtn.setOnClickListener {
//            val emailText = email.text.toString()
//            if (emailText.isEmpty() || !Validate.validateEmail(emailText)) {
//                emailLayout.isErrorEnabled = true
//                emailLayout.error = "Please enter a valid email address"
//                return@setOnClickListener
//            }
//
//            httpMethod.doPost(
//                "auth/reset-password",
//                EmailRequestBody(emailText),
//                object : ApiCallBack<Any> {
//                    override fun onSuccess(data: Any) {
//                        val intent = Intent(this@ResetPassword, VerifyOTP::class.java)
//                        intent.putExtra("email", emailText)
//                        intent.putExtra("message", "resetPassword")
//                        startActivity(intent)
//                    }
//
//                    override fun onError(error: Throwable) {
//                        emailLayout.isErrorEnabled = true
//                        emailLayout.error = error.message
//                    }
//                })
//        }
//    }
//}
package com.example.polary.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.ultils.ApiCallBack
import com.example.polary.utils.Validate
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class ResetPassword : AppCompatActivity() {
    private lateinit var email: TextInputEditText
    private lateinit var continueMatBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        email = findViewById(R.id.email)
        continueMatBtn = findViewById(R.id.continueMatBtn)

        continueMatBtn.setOnClickListener {
            val emailText = email.text.toString()
            if (Validate.validateEmail(emailText)) {
                resetPassword(emailText)
            } else {
                showError()
            }
        }
    }

    private fun resetPassword(emailText: String) {
        lifecycleScope.launch {
            try {
                val httpMethod = HttpMethod()
                httpMethod.doPost("auth/reset-password", EmailRequestBody(emailText), object :
                    ApiCallBack<Any> {
                    override fun onSuccess(data: Any) {
                        val intent = Intent(this@ResetPassword, VerifyOTP::class.java)
                        intent.putExtra("email", emailText)
                        intent.putExtra("message", "resetPassword")
                        startActivity(intent)
                    }

                    override fun onError(error: Throwable) {
                        showError()
                    }
                })
            } catch (error: Throwable) {
                // Handle error
            }
        }
    }

    private fun showError() {
        val emailLayout = findViewById<TextInputLayout>(R.id.email_layout)
        emailLayout.isErrorEnabled = true
        emailLayout.error = "Please enter a valid email address"

        // Remove error after 2 seconds
        emailLayout.postDelayed({
            emailLayout.isErrorEnabled = false
            emailLayout.error = null
        }, 2000) // 2000 milliseconds = 2 seconds
    }
}