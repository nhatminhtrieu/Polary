package com.example.polary.PostView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

class CustomBottomSheetBehavior<V: View> (context: Context, attrs: AttributeSet) : BottomSheetBehavior<V>(context, attrs) {
    private var allowDragging = true

    fun setAllowDragging(allowDragging: Boolean) {
        this.allowDragging = allowDragging
    }

    override fun onInterceptTouchEvent(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V, event: android.view.MotionEvent): Boolean {
        return if (!allowDragging) {
            false
        } else super.onInterceptTouchEvent(parent, child, event)
    }
}