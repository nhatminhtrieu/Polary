//package com.example.polary.authentication
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.MenuItem
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.NavUtils
//import com.example.polary.Class.HttpMethod
//import com.example.polary.Photo.TakePhotoActivity
//import com.example.polary.R
//import com.example.polary.dataClass.User
//import com.example.polary.utils.ApiCallBack
//import com.example.polary.utils.applyClickableSpan
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.common.api.ApiException
//import com.google.android.material.button.MaterialButton
//import com.google.android.material.textfield.TextInputEditText
//import com.google.android.material.textfield.TextInputLayout
//import com.google.android.material.textview.MaterialTextView
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.GoogleAuthProvider
//
//@Suppress("DEPRECATION")
//class SignIn : AppCompatActivity() {
//    private lateinit var usernameEditText: TextInputEditText
//    private lateinit var forgotPassword: MaterialButton
//    private lateinit var googleSignInClient: GoogleSignInClient
//    private lateinit var googleSignInButton: MaterialButton
//    private lateinit var signInMatBtn: MaterialButton
//    private val httpMethod = HttpMethod()
//
//    private val callback = object : ApiCallBack<Any> {
//        override fun onSuccess(data: Any) {
//            // Redirect to home page
//            Log.d("SignIn", "User signed in")
//            val intent = Intent(this@SignIn, TakePhotoActivity::class.java)
//            startActivity(intent)
//        }
//
//        override fun onError(error: Throwable) {
//            // Handle error
//            Log.e("SignIn", "Error signing in", error)
//        }
//    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_in)
//        usernameEditText = findViewById(R.id.username)
//        signInMatBtn = findViewById(R.id.login)
//
//
//        val signUpText: MaterialTextView = findViewById(R.id.signup_text)
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//
//        signInMatBtn.setOnClickListener {
//            val username = usernameEditText.text.toString()
//            val password: String = findViewById<TextInputEditText>(R.id.password).text.toString()
//
//            if (username.isBlank() || password.isBlank()) {
//                val usernameLayout = findViewById<TextInputLayout>(R.id.username_layout)
//                val passwordLayout = findViewById<TextInputLayout>(R.id.password_layout)
//
//                if (username.isBlank()) {
//                    usernameLayout.isErrorEnabled = true
//                    usernameLayout.error = "Please enter a username"
//                }
//
//                if (password.isBlank()) {
//                    passwordLayout.isErrorEnabled = true
//                    passwordLayout.error = "Please enter a password"
//                }
//            }
//
//            val user = User(username = username, password = password, email = "", firebaseUID = "")
//            httpMethod.doPost("auth/sign-in", user, object : ApiCallBack<Any> {
//                override fun onSuccess(data: Any) {
//                    val intent = Intent(this@SignIn, TakePhotoActivity::class.java)
//                    startActivity(intent)
//                }
//
//                override fun onError(error: Throwable) {
//                    // Handle sign in error
//                    Log.e("SignIn", "Error signing in", error)
//                }
//            })
//        }
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//
//        googleSignInButton = findViewById(R.id.GoogleLogin)
//        googleSignInButton.setOnClickListener {
//            signIn()
//        }
//
//        forgotPassword = findViewById(R.id.forgot_password)
//        forgotPassword.setOnClickListener {
//            val intent = Intent(this, ResetPassword::class.java)
//            startActivity(intent)
//        }
//
//        applyClickableSpan(signUpText, "Sign up", this, SignUpMain::class.java)
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
//    private fun signIn() {
//        val intent = googleSignInClient.signInIntent
//        startActivityForResult(intent, RC_SIGN_IN)
//    }
//
//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)
//                handleSignInResult(account)
//            } catch (e: ApiException) {
//                // Handle sign in failure
//            }
//        }
//    }
//
//    private fun handleSignInResult(account: GoogleSignInAccount?) {
//        account?.let {
//            val credential = GoogleAuthProvider.getCredential(it.idToken, null)
//            FirebaseAuth.getInstance().signInWithCredential(credential)
//                .addOnCompleteListener(this) { task ->
//                    task.result?.user?.let { firebaseUser ->
//                        val user = User(
//                            firebaseUID = firebaseUser.uid,
//                            username = usernameEditText.text?.toString() ?: "",
//                            email = firebaseUser.email ?: "",
//                            password = ""
//                        )
//                        httpMethod.doPost("users/", user, callback)
//                        val intent = Intent(this, TakePhotoActivity::class.java)
//                        startActivity(intent)
//                    }
//                }.addOnFailureListener { exception ->
//                    Log.w("SignIn", "signInWithCredential:failure", exception)
//                }
//        }
//    }
//    companion object {
//        private const val RC_SIGN_IN = 9001
//    }
//}
package com.example.polary.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.polary.Class.HttpMethod
import com.example.polary.Photo.TakePhotoActivity
import com.example.polary.R
import com.example.polary.dataClass.User
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.applyClickableSpan
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Suppress("DEPRECATION")
class SignIn : AppCompatActivity() {
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var googleSignInClient: GoogleSignInClient
    private val httpMethod = HttpMethod()
    private lateinit var sharedPreferences: SharedPreferences

    private fun saveUserToSharedPreferences(user: User) {
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", true)
            putString("username", user.username)
            putString("email", user.email)
            putString("firebaseUID", user.firebaseUID)
            apply()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        usernameEditText = findViewById(R.id.username)

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)

        val signInMatBtn: MaterialButton = findViewById(R.id.login)
        signInMatBtn.setOnClickListener { signInWithUsernameAndPassword() }

        val googleSignInButton: MaterialButton = findViewById(R.id.GoogleLogin)
        googleSignInButton.setOnClickListener { signInWithGoogle() }

        val forgotPassword: MaterialButton = findViewById(R.id.forgot_password)
        forgotPassword.setOnClickListener { startActivity(Intent(this, ResetPassword::class.java)) }

        val signUpText: MaterialTextView = findViewById(R.id.signup_text)
        applyClickableSpan(signUpText, "Sign up", this, SignUpMain::class.java)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithUsernameAndPassword() {
        val username = findViewById<TextInputEditText>(R.id.username).text.toString()
        val password = findViewById<TextInputEditText>(R.id.password).text.toString()

        if (username.isBlank() || password.isBlank()) {
            if (username.isBlank()) {
                findViewById<TextInputLayout>(R.id.username_layout).apply {
                    isErrorEnabled = true
                    error = "Please enter a username"
                }
            }
            if (password.isBlank()) {
                findViewById<TextInputLayout>(R.id.password_layout).apply {
                    isErrorEnabled = true
                    error = "Please enter a password"
                }
            }
            return
        }

        val user = User(username = username, password = password, email = "", firebaseUID = "")
        httpMethod.doPost("auth/sign-in", user, object : ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                saveUserToSharedPreferences(user)
                startActivity(Intent(this@SignIn, TakePhotoActivity::class.java))
            }

            override fun onError(error: Throwable) {
                Log.e("SignIn", "Error signing in", error)
            }
        })
    }

    private fun signInWithGoogle() {
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
            handleSignInResult(account)
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

                        saveUserToSharedPreferences(user)

                        httpMethod.doPost("auth/sign-in", user, object : ApiCallBack<Any> {
                            override fun onSuccess(data: Any) {
                                startActivity(Intent(this@SignIn, TakePhotoActivity::class.java))
                            }

                            override fun onError(error: Throwable) {
                                Log.e("SignIn", "Error signing in", error)
                            }
                        })
                    }
                }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}