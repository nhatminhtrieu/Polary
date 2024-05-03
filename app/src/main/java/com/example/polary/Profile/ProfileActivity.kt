package com.example.polary.Profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.polary.BaseActivity
import com.example.polary.Class.HttpMethod
import com.example.polary.PostView.PostActivity
import com.example.polary.R
import com.example.polary.authentication.SignIn
import com.example.polary.constant.IconDrawable
import com.example.polary.dataClass.Profile
import com.example.polary.dataClass.User
import com.example.polary.friends.FriendsActivity
import com.example.polary.friends.GroupsActivity
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.SessionManager
import com.example.polary.widgets.PolaryWidget
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : BaseActivity(), AvatarFragment.OnAvatarFragmentListener, UsernameFragment.OnUsernameFragmentListener{
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appIconCardView: CardView
    private lateinit var addWidgetCardView: CardView
    private lateinit var changeThemeCardView: CardView
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var user: User
    private lateinit var profile: Profile

    @SuppressLint("CutPasteId")
    override fun onAvatarUpdated() {
        getUserProfile()
    }
    override fun onUsernameUpdated() {
        getUserProfile()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configTopAppBar()
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val currentTheme = sharedPreferences.getString("theme", "Dark")
        val currentThemeResId = when (currentTheme) {
            "Light" -> R.style.AppTheme_Light
            "Dark" -> R.style.AppTheme_Dark
            else -> R.style.AppTheme_Dark
        }
        setTheme(currentThemeResId)
        setContentView(R.layout.activity_profile)
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // You can request the permission here or handle the situation as per your requirement
        }

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!
        getUserProfile()
        // Force hide the action bar
        supportActionBar?.hide()

        appIconCardView = findViewById(R.id.app_icon_profile_item)
        addWidgetCardView = findViewById(R.id.add_widget_profile_item)
        changeThemeCardView = findViewById(R.id.change_theme_profile_item)

        appIconCardView.setOnClickListener {
            showIconBottomSheet()
        }

        addWidgetCardView.setOnClickListener {
            showAddWidgetInstructions()
        }

        changeThemeCardView.setOnClickListener {
            showThemeDialog()
        }

        val cameraBtn = findViewById<MaterialButton>(R.id.btn_camera)
        cameraBtn.bringToFront()
        cameraBtn.setOnClickListener {
            val avatarFragment = AvatarFragment()
            avatarFragment.show(supportFragmentManager, AvatarFragment.TAG)
        }

        findViewById<android.widget.LinearLayout>(R.id.log_out_profile_item).setOnClickListener {
            signOut()
        }

        findViewById<android.widget.LinearLayout>(R.id.posts_archive_profile_item).setOnClickListener {
            val intent = Intent(this, PostActivity::class.java).apply {
                putExtra("myPost", true)
            }
            startActivity(intent)
        }


        val usernameFragment = UsernameFragment()
        findViewById<android.widget.LinearLayout>(R.id.username_profile_item).setOnClickListener {
            usernameFragment.show(supportFragmentManager, UsernameFragment.TAG)
        }

        val passwordFragment = PasswordFragment()
        findViewById<android.widget.LinearLayout>(R.id.password_profile_item).setOnClickListener {
            passwordFragment.show(supportFragmentManager, PasswordFragment.TAG)
        }

        findViewById<android.widget.LinearLayout>(R.id.friends_profile_item).setOnClickListener {
            val intent = Intent(this, FriendsActivity::class.java)
            startActivity(intent)
        }

        findViewById<android.widget.LinearLayout>(R.id.groups_profile_item).setOnClickListener {
            val intent = Intent(this, GroupsActivity::class.java)
            startActivity(intent)
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

    private fun getUserProfile() {
        val httpMethod = HttpMethod()
        httpMethod.doGet<Profile>("/users/${user.id}", object : ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                profile = data as Profile
                renderData()
                Log.d("ProfileActivity", "Successfully fetched user data ${profile.username}")
            }
            override fun onError(error: Throwable) {
                Log.e("ProfileActivity", "Failed to fetch user data: $error")
            }
        })
    }
    private fun renderData() {
        findViewById<TextView>(R.id.usernameProfile).text = profile.username
        if (profile.avatar !== null) {
            findViewById<ImageView>(R.id.avatarProfile).apply {
                Glide.with(this@ProfileActivity)
                    .load(profile.avatar)
                    .into(this)
            }
        }
    }

    private fun showThemeDialog() {
        // Array of themes
        val themes = arrayOf("Light", "Dark")

        // Get the current theme
        val currentThemeResId = sharedPreferences.getInt("themeResId", R.style.AppTheme_Dark)

        // Find the index of the current theme in the themes array
        val checkedItem = if (currentThemeResId == R.style.AppTheme_Light) 0 else 1

        // Create an AlertDialog Builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose a theme")

        // Set the single choice items on the dialog
        builder.setSingleChoiceItems(themes, checkedItem) { dialog, which ->
            // Save the selected theme in SharedPreferences
            val selectedThemeResId =
                if (which == 0) R.style.AppTheme_Light else R.style.AppTheme_Dark
            switchTheme(selectedThemeResId)

            // Dismiss the dialog
            dialog.dismiss()
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }
    private fun signOut() {
        FirebaseAuth.getInstance().signOut()

        // Update widget
        applicationContext.sendBroadcast(
            Intent(applicationContext, PolaryWidget::class.java).apply {
                action = PolaryWidget.ACTION_UPDATE_WIDGET
            }
        )

        startActivity(Intent(this, SignIn::class.java))

        // Reset to default app icon
        SessionManager(sharedPreferences).getAppIcon()?.let { currentIcon ->
            // Disable the current icon
            packageManager.setComponentEnabledSetting(
                ComponentName(this, "${packageName}.Profile.ProfileActivity.$currentIcon"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        // Enable the default icon
        packageManager.setComponentEnabledSetting(
            ComponentName(this, "${packageName}.OnBoarding"),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        // Save the default icon name in session manager
        SessionManager(sharedPreferences).saveAppIcon(
            resources.getResourceEntryName(
                packageManager.getApplicationInfo(
                    this.packageName,
                    PackageManager.GET_META_DATA
                ).icon
            )
        )

        with(sharedPreferences.edit()) {
            clear()
            putBoolean("isLoggedIn", false)
            apply()
        }

        FirebaseAuth.getInstance().signOut()

        // Update widget
        val context = applicationContext
        val intent = Intent(applicationContext, PolaryWidget::class.java).apply {
            action = PolaryWidget.ACTION_UPDATE_WIDGET
        }
        context.sendBroadcast(intent)

        val intentSignIn = Intent(this, SignIn::class.java)
        startActivity(intentSignIn)
        finish()
    }

    private fun showAddWidgetInstructions() {
        val appWidgetManager = getSystemService(AppWidgetManager::class.java)
        val myProvider = ComponentName(this, PolaryWidget::class.java)

        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            val successCallback = Intent(this, PolaryWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            appWidgetManager.requestPinAppWidget(
                myProvider,
                null,
                PendingIntent.getBroadcast(
                    this, 0, successCallback,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun changeIcon(context: Context, newIcon: String) {
        val sessionManager = SessionManager(sharedPreferences)
        val oldIcon = sessionManager.getAppIcon()

        // If there's an old icon, disable it
        oldIcon?.let {
            val oldIconComponentName =
                ComponentName(context, "${context.packageName}.Profile.ProfileActivity.$it")
            context.packageManager.setComponentEnabledSetting(
                oldIconComponentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        // Disable the default icon
        val defaultIconComponentName = ComponentName(context, "${context.packageName}.OnBoarding")
        context.packageManager.setComponentEnabledSetting(
            defaultIconComponentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )

        // Enable the new icon
        val newIconComponentName =
            ComponentName(context, "${context.packageName}.Profile.ProfileActivity.$newIcon")
        context.packageManager.setComponentEnabledSetting(
            newIconComponentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        // Save the new icon
        sessionManager.saveAppIcon(newIcon)
    }

    @SuppressLint("InflateParams")
    private fun showIconBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.fragment_app_icon, null)
        bottomSheetDialog.setContentView(view)

        // Set up the RecyclerView in the BottomSheetDialog
        val recyclerView = view.findViewById<RecyclerView>(R.id.appIconRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val icons = IconDrawable.map.keys.map { AppIcon(it) }
        recyclerView.adapter = AppIconAdapter(icons) { icon ->
            changeIcon(this, icon.aliasName)
        }
        bottomSheetDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::bottomSheetDialog.isInitialized && bottomSheetDialog.isShowing) {
            bottomSheetDialog.dismiss()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}
