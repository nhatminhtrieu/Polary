package com.example.polary.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat

class BitMapFromDrawable {
    companion object {
        fun getBitmapFromVectorDrawable(context: Context, drawableId: Int, background: Int): Bitmap {
            val drawable = ContextCompat.getDrawable(context, drawableId)

            val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            canvas.drawColor(background)

            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }
    }
}