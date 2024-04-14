package com.example.polary.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.AppWidgetTarget
import com.example.polary.R

class PolaryWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.polary_widget)

    val awt = AppWidgetTarget(context.applicationContext, R.id.post_image, views, appWidgetId)

    Glide.with(context.applicationContext)
        .asBitmap()
        .load(R.drawable.ic_launcher_background)
        .transform(CenterCrop(), RoundedCorners(32))
        .into(awt)

    views.setImageViewResource(R.id.avatar, R.drawable.clown_emoji)
    views.setTextViewText(R.id.post_caption, "TODO")
    appWidgetManager.updateAppWidget(appWidgetId, views)
}