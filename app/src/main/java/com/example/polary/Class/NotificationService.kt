package com.example.polary.Class

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.polary.PostView.PostActivity
import com.example.polary.R
import com.example.polary.constant.EmojiText
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.BitMapFromDrawable
import com.example.polary.utils.SessionManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.concurrent.ExecutionException

class NotificationService() : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val user = SessionManager(getSharedPreferences("user", MODE_PRIVATE)).getUserFromSharedPreferences()
        if(user == null) return
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
        showNotification(message.notification!!.title!!, message.notification!!.body!!, message.data)
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

    private fun showNotification(title: String, message: String, data: Map<String, String>? = null) {
        val avatarUrl = data?.get("avatar")
        val emojiText = EmojiText.map[data?.get("emoji")] ?: ""
        val displayContent = message + emojiText
        Log.d("NotificationData", data?.get("postId").toString())
        val resultIntent = Intent(this, PostActivity::class.java).apply {
            putExtra("postId", data?.get("postId"))
        }
        val resultPendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            resultIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(displayContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)

        if (avatarUrl != null) {
            Glide.with(this)
                .asBitmap()
                .load(avatarUrl)
                .error(R.drawable.ic_launcher_foreground)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        Log.d("NotificationService", "Avatar loaded")
                        notificationBuilder.setLargeIcon(resource)
                        notifyNotification(notificationBuilder.build())
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        try {
                            notificationBuilder.setLargeIcon(
                                BitMapFromDrawable.getBitmapFromVectorDrawable(
                                    applicationContext,
                                    R.drawable.ic_launcher_foreground,
                                    R.drawable.ic_launcher_background
                                ))
                            Log.d("hello", "hi")
                            notifyNotification(notificationBuilder.build())
                        } catch (e: ExecutionException) {
                            e.printStackTrace()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }

                    }
                })
        } else {
            notifyNotification(notificationBuilder.build())
        }
    }

    private fun notifyNotification(notification: Notification) {
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