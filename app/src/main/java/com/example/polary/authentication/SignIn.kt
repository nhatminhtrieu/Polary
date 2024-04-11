package com.example.polary.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.dataClass.User
import com.example.polary.ultils.ApiCallBack
import com.example.polary.utils.applyClickableSpan
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignIn : AppCompatActivity() {
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var forgotPassword: MaterialButton
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInButton: MaterialButton

    private val httpMethod = HttpMethod()
    private val callback = object : ApiCallBack<Any> {
        override fun onSuccess(data: Any) {
            // Redirect to home page
            Log.d("SignIn", "User signed in")
        }

        override fun onError(error: Throwable) {
            // Handle error
            Log.e("SignIn", "Error signing in", error)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        usernameEditText = findViewById(R.id.username)


        val signUpText: MaterialTextView = findViewById(R.id.signup_text)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInButton = findViewById(R.id.GoogleLogin)
        googleSignInButton.setOnClickListener {
            signIn()
        }

        forgotPassword = findViewById(R.id.forgot_password)
        forgotPassword.setOnClickListener {
            val intent = Intent(this, ResetPassword::class.java)
            startActivity(intent)
        }

        applyClickableSpan(signUpText, "Sign up", this, SignUpMain::class.java)
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

    private fun signIn() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                handleSignInResult(account)
            } catch (e: ApiException) {
                // Handle sign in failure
            }
        }
    }

private fun handleSignInResult(account: GoogleSignInAccount?) {
    account?.let {
        val credential = GoogleAuthProvider.getCredential(it.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                task.result?.user?.let { firebaseUser ->
                    val user = User(
                        firebaseUID = firebaseUser.uid,
                        username = usernameEditText.text?.toString() ?: "",
                        email = firebaseUser.email ?: "",
                        password = ""
                    )
                    httpMethod.doPost("users/", user, callback)
                }
            }.addOnFailureListener { exception ->
                Log.w("SignIn", "signInWithCredential:failure", exception)
            }
    }
}
    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
