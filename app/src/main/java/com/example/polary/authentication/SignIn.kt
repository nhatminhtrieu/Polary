package com.example.polary.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.polary.R
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

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
        // Handle sign in success
        if (account != null) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        val user = FirebaseAuth.getInstance().currentUser
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SignIn", "signInWithCredential:failure", task.exception)
                    }
                }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
