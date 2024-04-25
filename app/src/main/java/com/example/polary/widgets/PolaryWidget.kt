package com.example.polary.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.text.TextPaint
import android.text.TextUtils
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.AppWidgetTarget
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.dataClass.Post
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.SessionManager

class PolaryWidget : AppWidgetProvider() {
    companion object {
        const val ACTION_UPDATE_WIDGET = "com.example.polary.widgets.ACTION_UPDATE_WIDGET"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_UPDATE_WIDGET) {
            updateWidgets(context)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val intent = Intent(context, PolaryWidget::class.java).apply {
            action = ACTION_UPDATE_WIDGET
        }
        context.sendBroadcast(intent)
    }

    fun updateWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds =
            appWidgetManager.getAppWidgetIds(ComponentName(context, PolaryWidget::class.java))
        appWidgetIds.forEach { getLatestPost(context, it) }
    }

    private fun getLatestPost(context: Context, appWidgetId: Int) {
        val httpMethod = HttpMethod()
        val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        val user = sessionManager.getUserFromSharedPreferences()
        val appWidgetManager = AppWidgetManager.getInstance(context)
        var views = RemoteViews(context.packageName, R.layout.polary_empty_widget)

        if (user == null) {
            updateViewWithText(context, appWidgetManager, appWidgetId, views, "Tap to set up", R.drawable.white_background)
        } else {
            val endpoint = "users/${user.id}/viewable-posts"
            val queryParam = mapOf("authorId" to "0")
            httpMethod.doGetWithQuery<Post>(endpoint, queryParam, object : ApiCallBack<Any> {
                override fun onSuccess(data: Any) {
                    val posts = data as List<Post>
                    val latestPost = posts.maxByOrNull { it.id.toInt() }
                    if (latestPost != null) {
                        views = RemoteViews(context.packageName, R.layout.polary_widget)
                        updateAppWidget(context, appWidgetManager, appWidgetId, latestPost)
                    } else {
                        updateViewWithText(context, appWidgetManager, appWidgetId, views, "No pics, yet")
                    }
                }

                override fun onError(error: Throwable) {
                    updateViewWithText(context, appWidgetManager, appWidgetId, views, "No pics, yet")
                }
            })
        }
    }

    private fun updateViewWithText(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, views: RemoteViews, text: String, image: Int = R.drawable.white_background) {
        views.setTextViewText(R.id.textView, text)
        val backgroundIVTarget = AppWidgetTarget(
            context.applicationContext,
            R.id.backgroundIV,
            views,
            appWidgetId
        )

        Glide.with(context.applicationContext)
            .asBitmap()
            .load(image)
            .transform(
                CenterCrop(),
                RoundedCorners(32)
            )
            .override(300, 300)
            .into(backgroundIVTarget)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        latestPost: Post
    ) {
        val views = RemoteViews(context.packageName, R.layout.polary_widget)
        val postImageTarget =
            AppWidgetTarget(context.applicationContext, R.id.post_image, views, appWidgetId)
        val avatarTarget = AppWidgetTarget(context.applicationContext, R.id.avatar, views, appWidgetId)

        views.setTextViewText(
            R.id.post_caption,
            TextUtils.ellipsize(latestPost.caption, TextPaint(), 100f, TextUtils.TruncateAt.END)
        )

        latestPost.imageUrl =
            "https://firebasestorage.googleapis.com/v0/b/polary-8862a.appspot.com/o/1713290324647_IMG_8141.jpeg?alt=media&token=7c27954d-7d22-4493-897b-375721a059b0"

        loadImage(context, postImageTarget, latestPost.imageUrl)
        loadImage(context, avatarTarget, latestPost.author.avatar!!)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun loadImage(context: Context, target: AppWidgetTarget, url: String) {
        Glide.with(context.applicationContext)
            .asBitmap()
            .load(url)
            .transform(CenterCrop(), RoundedCorners(32))
            .override(300, 300)
            .into(target)
    }
}