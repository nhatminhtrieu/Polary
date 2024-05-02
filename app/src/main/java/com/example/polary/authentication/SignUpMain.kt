package com.example.polary.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.polary.BaseActivity
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.Validate
import com.example.polary.utils.applyClickableSpan
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

data class EmailRequestBody(val email: String)

@Suppress("DEPRECATION")
class SignUpMain : BaseActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    private lateinit var emailLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var signUpButton: MaterialButton
    private lateinit var googleSignInButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_main)

        emailLayout = findViewById(R.id.email_layout)
        emailEditText = findViewById(R.id.email)
        signUpButton = findViewById(R.id.signUp)
        googleSignInButton = findViewById(R.id.GoogleLogin)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()

            if (email.isEmpty() || !Validate.validateEmail(email)) {
                emailLayout.apply {
                    isErrorEnabled = true
                    error = "Please enter a valid email address"
                }
                return@setOnClickListener
            }

            val requestBody = EmailRequestBody(email)
            HttpMethod().doPost("auth/sign-up", requestBody, object : ApiCallBack<Any> {
                override fun onSuccess(data: Any) {
                    Log.d("SignUpMain", "Successfully sent OTP to email")
                }

                override fun onError(error: Throwable) {
                    Log.d("SignUpMain", "Error occurred while sending OTP: ${error.message}")
                }
            })

            Intent(this, VerifyOTP::class.java).apply {
                putExtra("email", email)
                putExtra("message", "signUp")
                startActivity(this)
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInButton.setOnClickListener {
            startActivityForResult(mGoogleSignInClient.signInIntent, RC_SIGN_IN)
        }

        applyClickableSpan(findViewById(R.id.signIn_text), getString(R.string.log_in), this, SignIn::class.java)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            task.getResult(ApiException::class.java)?.let { account ->
                account.let {
                    Log.d("SignUpMain", "account: $it")
                    firebaseAuthWithGoogle(it.idToken!!)
                }
            } ?: run {
                Log.w("SignUpMain", "Google sign in failed", task.exception)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                task.result?.user?.also {
                    Log.d("SignUpMain", "signInWithCredential:success")
                } ?: run {
                    Log.w("SignUpMain", "signInWithCredential:failure", task.exception)
                }
            }
    }
}