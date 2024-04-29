package com.example.polary.Class

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.polary.R
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.SessionManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService() : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val user = SessionManager(getSharedPreferences("user", MODE_PRIVATE)).getUserFromSharedPreferences()!!
        val httpMethod = HttpMethod()
        httpMethod.doPost("users/${user.id}/fcm-token", mapOf("token" to token), object : ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                // Do nothing
                Log.d("NotificationService", "Token sent to server")
            }

            override fun onError(error: Throwable) {
                // Do nothing
                Log.e("NotificationService", "Failed to send token to server")
            }
        })
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotification(message.notification!!.title!!, message.notification!!.body!!)
    }

    fun sendRegistrationToServer(token: String, userId: String) {
        // Send token to server
        val httpMethod = HttpMethod()
        val endpoint = "users/${userId}/fcm-token"
        val params = mapOf("token" to token)
        httpMethod.doPost(endpoint, params, object : ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                // Do nothing
                Log.d("NotificationService", "Token sent to server")
            }

            override fun onError(error: Throwable) {
                // Do nothing
                Log.e("NotificationService", "Failed to send token to server")
            }
        })
    }

    private fun showNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
               Toast.makeText(applicationContext, "Permission not granted", Toast.LENGTH_SHORT).show()
                return@with
            }
            // notificationId is a unique int for each notification that you must define.
            notify(CHANNEL_ID.toInt(), notification)
        }
    }

    companion object {
        private const val CHANNEL_ID = "1"
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val name = "Channel name"
                val descriptionText = "Channel description"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("1", name, importance).apply {
                    description = descriptionText
                }
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}