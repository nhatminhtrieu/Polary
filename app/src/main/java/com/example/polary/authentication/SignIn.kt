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
import com.example.polary.utils.SessionManager
import com.example.polary.utils.applyClickableSpan
import com.example.polary.widgets.PolaryWidget
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
import com.google.gson.Gson
import com.google.gson.JsonObject

@Suppress("DEPRECATION")
class SignIn : AppCompatActivity() {
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sessionManager: SessionManager
    private val httpMethod = HttpMethod()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        usernameEditText = findViewById(R.id.username)

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        sessionManager = SessionManager(sharedPreferences)

        val signInMatBtn: MaterialButton = findViewById(R.id.login)
        signInMatBtn.setOnClickListener { signInWithUsernameAndPassword() }

        val googleSignInButton: MaterialButton = findViewById(R.id.GoogleLogin)
        googleSignInButton.setOnClickListener { signInWithGoogle() }

        val forgotPassword: MaterialButton = findViewById(R.id.forgot_password)
        forgotPassword.setOnClickListener { startActivity(Intent(this, ResetPassword::class.java)) }

        val signUpText: MaterialTextView = findViewById(R.id.signup_text)
        applyClickableSpan(signUpText, getString(R.string.sign_up), this, SignUpMain::class.java)

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

        val user =
            User(id = 0, username = username, password = password, email = "", firebaseUID = "")
        httpMethod.doPost("auth/sign-in", user, object : ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                val gson = Gson()
                val jsonObject = gson.fromJson(data.toString(), JsonObject::class.java)
                val userObject = jsonObject.getAsJsonObject("user")
                user.id = userObject.get("id").asInt
                sessionManager.saveUserToSharedPreferences(user)

                // Update widget
                val context = applicationContext
                val intent = Intent(applicationContext, PolaryWidget::class.java).apply {
                    action = PolaryWidget.ACTION_UPDATE_WIDGET
                }
                context.sendBroadcast(intent)

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
                            id = 0,
                            firebaseUID = firebaseUser.uid,
                            username = usernameEditText.text?.toString() ?: "",
                            email = firebaseUser.email ?: "",
                            password = ""
                        )

                        httpMethod.doPost("auth/sign-in", user, object : ApiCallBack<Any> {
                            override fun onSuccess(data: Any) {
                                val gson = Gson()
                                val jsonObject =
                                    gson.fromJson(data.toString(), JsonObject::class.java)
                                val userObject = jsonObject.getAsJsonObject("user")
                                user.id = userObject.get("id").asInt
                                // Update widget
                                val context = applicationContext
                                val intent =
                                    Intent(applicationContext, PolaryWidget::class.java).apply {
                                        action = PolaryWidget.ACTION_UPDATE_WIDGET
                                    }
                                context.sendBroadcast(intent)
                                startActivity(Intent(this@SignIn, TakePhotoActivity::class.java))
                            }

                            override fun onError(error: Throwable) {
                                Log.e("SignIn", "Error signing in", error)
                            }
                        })
                        sessionManager.saveUserToSharedPreferences(user)
                    }
                }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}