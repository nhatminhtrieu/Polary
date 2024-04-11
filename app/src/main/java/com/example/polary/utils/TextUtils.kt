package com.example.polary.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.example.polary.R
import com.google.android.material.textview.MaterialTextView

fun applyClickableSpan(textView: MaterialTextView, clickableText: String, activity: Activity, targetActivity: Class<*>) {
    val fullText = textView.text.toString()
    val spannableString = SpannableString(fullText)

    val clickableSpan: ClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            val intent = Intent(activity, targetActivity)
            activity.startActivity(intent)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(activity, R.color.md_theme_onBackground)
        }
    }

    val startIndexOfLink = fullText.indexOf(clickableText)
    spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + clickableText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    spannableString.setSpan(StyleSpan(Typeface.BOLD), startIndexOfLink, startIndexOfLink + clickableText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    textView.text = spannableString
    textView.movementMethod = LinkMovementMethod.getInstance()
}