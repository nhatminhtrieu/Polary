//package com.example.polary.authentication
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.MenuItem
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.NavUtils
//import com.example.polary.R
//import com.example.polary.utils.Validate
//import com.example.polary.utils.applyClickableSpan
//import com.google.android.material.button.MaterialButton
//import com.google.android.material.textfield.TextInputEditText
//import com.google.android.material.textfield.TextInputLayout
//import com.google.firebase.auth.FirebaseAuth
//
//class SignUpMain : AppCompatActivity() {
//    private lateinit var auth: FirebaseAuth
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_up_main)
//
//        auth = FirebaseAuth.getInstance()
//
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        val signUpButton: MaterialButton = findViewById(R.id.signUp)
//        signUpButton.setOnClickListener {
//            val emailLayout: TextInputLayout = findViewById(R.id.email_layout)
//            val emailEditText: TextInputEditText = findViewById(R.id.email)
//            val email = emailEditText.text.toString()
//
//            if (email.isEmpty() || !Validate.validateEmail(email)) {
//                emailLayout.isErrorEnabled = true
//                emailLayout.error = "Please enter a valid email address"
//                return@setOnClickListener
//            }
//
//            auth.createUserWithEmailAndPassword(email, "password")
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        val user = auth.currentUser
//                        user?.sendEmailVerification()
//                            ?.addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    val intent = Intent(this, VerifyOTP::class.java)
//                                    intent.putExtra("email", email)
//                                    intent.putExtra("message", "signUp")
//                                    startActivity(intent)
//                                }
//                            }
//                    } else {
//                        // If sign in fails, display a message to the user.
//                    }
//                }
//        }
//
//        applyClickableSpan(findViewById(R.id.signIn_text), "Sign in", this, SignIn::class.java)
//
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                NavUtils.navigateUpTo(this, intent)
//                true
//            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//}
package com.example.polary.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.ultils.ApiCallBack
import com.example.polary.utils.Validate
import com.example.polary.utils.applyClickableSpan
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson

data class EmailRequestBody(val email: String)
class SignUpMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val signUpButton: MaterialButton = findViewById(R.id.signUp)
        signUpButton.setOnClickListener {
            val emailLayout: TextInputLayout = findViewById(R.id.email_layout)
            val emailEditText: TextInputEditText = findViewById(R.id.email)
            val email = emailEditText.text.toString()

            if (email.isEmpty() || !Validate.validateEmail(email)) {
                emailLayout.isErrorEnabled = true
                emailLayout.error = "Please enter a valid email address"
                return@setOnClickListener
            }

            val gson = Gson()
            val emailJson = gson.toJson(EmailRequestBody(email))

            if (emailJson != null) {
                val httpMethod = HttpMethod()
                val requestBody = EmailRequestBody(email)
                httpMethod.doPost("auth/signup", requestBody, object : ApiCallBack<Any> {
                    override fun onSuccess(data: Any) {
                        Log.d("SignUpMain", "Successfully sent OTP to email")
                    }

                    override fun onError(error: Throwable) {
                        Log.d("SignUpMain", "Error occurred while sending OTP: ${error.message}")
                    }
                })
            } else {
                Log.d("SignUpMain", "Failed to convert email to JSON")
            }

            // jump to VerifyOTP, send email to that activity
            val intent = Intent(this, VerifyOTP::class.java)
            intent.putExtra("email", email)
            intent.putExtra("message", "signUp")

            startActivity(intent)

            applyClickableSpan(findViewById(R.id.signIn_text), "Sign in", this, SignIn::class.java)
        }

    }
}