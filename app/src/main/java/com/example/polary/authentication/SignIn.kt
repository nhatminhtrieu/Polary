package com.example.polary.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.polary.BaseActivity
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
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.google.gson.JsonObject

@Suppress("DEPRECATION")
class SignIn : BaseActivity() {
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sessionManager: SessionManager
    private val httpMethod = HttpMethod()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        configTopAppBar()
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

    private fun configTopAppBar() {
        val appBar = findViewById<MaterialToolbar>(R.id.app_top_app_bar)
        appBar?.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_back)
        appBar?.setNavigationOnClickListener {
            // Go back to the parent activity
            finish()
        }
    }
    private fun signInWithUsernameAndPassword() {
        val username = findViewById<TextInputEditText>(R.id.username).text.toString()
        val password = findViewById<TextInputEditText>(R.id.password).text.toString()

        if (username.isBlank() || password.isBlank()) {
            if (username.isBlank()) {
                findViewById<TextInputLayout>(R.id.username_layout).apply {
                    isErrorEnabled = true
                    error = "Username cannot be empty"
                }
            }
            if (password.isBlank()) {
                findViewById<TextInputLayout>(R.id.password_layout).apply {
                    isErrorEnabled = true
                    error = "Password cannot be empty"
                }
            }
            return
        }

        httpMethod.doPost("auth/sign-in", requestBody = User(id = 0, username = username, password = password, email = "", firebaseUID = ""), object : ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                val gson = Gson()
                val jsonObject = gson.fromJson(data.toString(), JsonObject::class.java)
                val userObject = jsonObject.getAsJsonObject("user")
                val user =
                    User(id = userObject.get("id").asInt, username = username, password = password, email = "", firebaseUID = "")
                sessionManager.saveUserToSharedPreferences(user)

                // Update widget
                val context = applicationContext
                val intent = Intent(applicationContext, PolaryWidget::class.java).apply {
                    action = PolaryWidget.ACTION_UPDATE_WIDGET
                }
                context.sendBroadcast(intent)

                startActivity(Intent(this@SignIn, TakePhotoActivity::class.java))
            }

            override fun onError(errorMsg: Throwable) {
                Log.e("SignIn", "Error signing in", errorMsg)
                Toast.makeText(this@SignIn, "Invalid username or password", Toast.LENGTH_SHORT)
                    .show()
                findViewById<TextInputLayout>(R.id.username_layout).apply {
                    isErrorEnabled = true
                    error = "Invalid username or password"
                }
                findViewById<TextInputLayout>(R.id.password_layout).apply {
                    isErrorEnabled = true
                    error = "Invalid username or password"
                }
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
                        httpMethod.doPost("auth/sign-in", requestBody = User(
                            id = 0,
                            firebaseUID = firebaseUser.uid,
                            username = usernameEditText.text?.toString() ?: "",
                            email = firebaseUser.email ?: "",
                            password = ""
                        ), object : ApiCallBack<Any> {
                            override fun onSuccess(data: Any) {
                                val gson = Gson()
                                val jsonObject =
                                    gson.fromJson(data.toString(), JsonObject::class.java)
                                val userObject = jsonObject.getAsJsonObject("user")
                                val user = User(
                                    id = userObject.get("id").asInt,
                                    firebaseUID = firebaseUser.uid,
                                    username = userObject.get("username").asString,
                                    email = firebaseUser.email ?: "",
                                    password = String()
                                )
                                sessionManager.saveUserToSharedPreferences(user)
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
                    }
                }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}