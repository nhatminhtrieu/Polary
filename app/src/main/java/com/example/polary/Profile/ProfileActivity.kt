package com.example.polary.Profile

import AppIcon
import AppIconAdapter
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.R
import com.example.polary.authentication.SignIn
import com.example.polary.constant.IconDrawable
import com.example.polary.utils.SessionManager
import com.example.polary.widgets.PolaryWidget
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appIconCardView: CardView
    private lateinit var addWidgetCardView: CardView
    private lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)

        appIconCardView = findViewById(R.id.app_icon_profile_item)
        addWidgetCardView = findViewById(R.id.add_widget_profile_item)
        appIconCardView.findViewById<ImageView>(R.id.app_icon_profile_item_image).setImageResource(
            IconDrawable.map[SessionManager(sharedPreferences).getAppIcon()] ?: R.mipmap.ic_launcher
        )

        appIconCardView.setOnClickListener {
            showIconBottomSheet()
        }

        addWidgetCardView.setOnClickListener {
            try {
                // Open the widget section in the application list
                val pickIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    addCategory(Intent.CATEGORY_DEFAULT)
                }
                val chooser = Intent.createChooser(pickIntent, "Select Widget")
                startActivity(chooser)
            } catch (e: Exception) {
                // Handle exception
            }
        }

        val signOutBtn = findViewById<TextView>(R.id.signOutBtn)
        signOutBtn.setOnClickListener {
            signOut()
        }
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

        finish()
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
}