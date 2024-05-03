package com.example.polary.Class

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View

class FocusCircleView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var focusX: Float = 0f
    private var focusY: Float = 0f
    private val handler = Handler(Looper.getMainLooper())
    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5f
        alpha = 0
    }

    fun focusAt(x: Float, y: Float) {
        focusX = x
        focusY = y
        paint.alpha = 200
        invalidate()
        // Remove any existing callbacks to ensure that only one circle is drawn at a time
        handler.removeCallbacksAndMessages(null)

        // Post a delayed runnable that will erase the circle after 2 seconds
        handler.postDelayed({
            focusX = 0f
            focusY = 0f
            paint.alpha = 0
            invalidate()
        }, 2000) // 2000 milliseconds = 2 seconds
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(focusX, focusY, 50f, paint)
    }
}