package com.example.polary.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.text.TextPaint
import android.text.TextUtils
import android.util.DisplayMetrics
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.AppWidgetTarget
import com.example.polary.Class.HttpMethod
import com.example.polary.OnBoarding
import com.example.polary.Photo.TakePhotoActivity
import com.example.polary.R
import com.example.polary.dataClass.Author
import com.example.polary.dataClass.Post
import com.example.polary.dataClass.User
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.SessionManager
import kotlin.math.max
import kotlin.math.roundToInt

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
        context.sendBroadcast(Intent(context, PolaryWidget::class.java).apply {
            action = ACTION_UPDATE_WIDGET
        })
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds.forEach { getLatestPost(context, it) }
    }

    fun updateWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds =
            appWidgetManager.getAppWidgetIds(ComponentName(context, PolaryWidget::class.java))
        appWidgetIds.forEach { getLatestPost(context, it) }
    }

    private fun getLatestPost(context: Context, appWidgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val user = getUserFromSession(context)
        var views = RemoteViews(context.packageName, R.layout.polary_empty_widget)

        if (user == null) {
            updateViewWithText(context, appWidgetManager, appWidgetId, views, "Tap to set up", R.drawable.white_background)
        } else {
            // TODO: Fix query post later
            HttpMethod().doGet<Post>("users/${user.id}/viewable-posts", object : ApiCallBack<Any> {
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
//                    updateViewWithText(context, appWidgetManager, appWidgetId, views, "No pics, yet")
                    updateAppWidget(context, appWidgetManager, appWidgetId, generateFakePost())
                }
            })
        }
    }

    private fun updateViewWithText(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, views: RemoteViews, text: String, image: Int = R.drawable.white_background) {
        views.setTextViewText(R.id.textView, text)
        loadImage(context, views, appWidgetId, image)
        setOnClickPendingIntent(context, views)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun loadImage(context: Context, views: RemoteViews, appWidgetId: Int, image: Int) {
        val backgroundIVTarget =
            AppWidgetTarget(context.applicationContext, R.id.backgroundIV, views, appWidgetId)
        Glide.with(context.applicationContext)
            .asBitmap()
            .load(image)
            .transform(CenterCrop(), RoundedCorners(32))
            .override(300, 300)
            .into(backgroundIVTarget)
    }

    private fun setOnClickPendingIntent(context: Context, views: RemoteViews) {
        val user = getUserFromSession(context)
        val intent = if (user != null) Intent(context, TakePhotoActivity::class.java) else Intent(
            context,
            OnBoarding::class.java
        )
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        latestPost: Post
    ) {
        val views = RemoteViews(context.packageName, R.layout.polary_widget)
        setTextViewText(views, latestPost)
        loadImage(context, views, appWidgetId, latestPost)

        // Set onClickPendingIntent for post_image
        val intent = Intent(context, TakePhotoActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.post_image, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun setTextViewText(views: RemoteViews, latestPost: Post) {
        views.setTextViewText(
            R.id.post_caption,
            TextUtils.ellipsize(latestPost.caption, TextPaint(), 100f, TextUtils.TruncateAt.END)
        )
    }

    private fun calculateWidgetSizeInDp(context: Context): Pair<Int, Int> {
        // Size in dp (width x height)
        // Portrait mode: (73n - 16) x (118m - 16)
        // Landscape mode: (142n - 15) x (66m - 15)
        // https://developer.android.com/develop/ui/views/appwidgets/layouts#anatomy_determining_size
        val widthInCells = 3
        val heightInCells = 2

        // check if the device is in portrait or landscape mode
        val displayMetrics = context.resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        return if (width < height) {
            Pair((73 * widthInCells) - 16, (118 * heightInCells) - 16)
        } else {
            Pair((142 * widthInCells) - 15, (66 * heightInCells) - 15)
        }
    }

    private fun loadImage(
        context: Context,
        views: RemoteViews,
        appWidgetId: Int,
        latestPost: Post
    ) {
        val postImageTarget =
            AppWidgetTarget(context.applicationContext, R.id.post_image, views, appWidgetId)
        val avatarTarget =
            AppWidgetTarget(context.applicationContext, R.id.avatar, views, appWidgetId)

        // Widget size in dp
        val widgetSize = calculateWidgetSizeInDp(context)

        // Convert to pixels to load the image with the correct size
        val targetWidth = dpToPx(context, max(widgetSize.first, widgetSize.second))

        latestPost.imageUrl.let {
            Glide.with(context.applicationContext)
                .asBitmap()
                .load(it)
                .transform(CenterCrop(), RoundedCorners(32))
                .override(targetWidth, targetWidth)
                .into(postImageTarget)
        }

        latestPost.author.avatar.let {
            Glide.with(context.applicationContext)
                .asBitmap()
                .load(it)
                .transform(CircleCrop())
                .override(300, 300)
                .into(avatarTarget)
        }
    }
    private fun dpToPx(context: Context, dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }
    private fun getUserFromSession(context: Context): User? {
        val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        return sessionManager.getUserFromSharedPreferences()
    }

    fun generateFakePost(): Post {
        return Post(
            id = 1,
            caption = "This is a caption",
            imageUrl = "https://picsum.photos/1600/1200",
            author = Author(
                id = 1,
                username = "username",
                avatar = "https://picsum.photos/1600/1200"
            ),
            countComments = 0,
            reactions = emptyList()
        )
    }
}