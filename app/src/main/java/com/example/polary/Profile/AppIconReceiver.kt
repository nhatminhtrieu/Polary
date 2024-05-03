package com.example.polary.Profile

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import com.example.polary.R

@Suppress("DEPRECATION")
class AppIconReceiver : BroadcastReceiver() {
    @SuppressLint("DiscouragedApi")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.example.app.ACTION_CHANGE_APP_ICON") {
            // Get the icon resource from the intent
            val iconResource: Intent.ShortcutIconResource? = intent.getParcelableExtra(
                "android.intent.extra.shortcut.ICON_RESOURCE"
            )

            // Create the icon from the resource
            val resourceId = context?.resources?.getIdentifier(
                iconResource?.resourceName ?: "",
                "drawable",
                context.packageName
            )
            val icon = resourceId?.let { Icon.createWithResource(context, it) }

            // Create the shortcut manager
            val shortcutManager = context?.getSystemService(ShortcutManager::class.java)

            // Create the shortcut
            val shortcut = ShortcutInfo.Builder(context, "app_icon")
                .setIcon(icon)
                .setShortLabel(context?.getString(R.string.app_name) ?: "")
                .build()

            // Update the app icon
            shortcutManager?.dynamicShortcuts = listOf(shortcut)
        }
    }
}