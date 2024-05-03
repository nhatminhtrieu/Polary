package com.example.polary.utils


import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CompletableDeferred
import java.text.SimpleDateFormat
import java.util.Locale

class ImageDownloader {
    companion object {
        fun saveImage(view: View, context: Context) {
            val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
            val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis())
            val cardView = view.findViewById<MaterialCardView>(R.id.post_card)
            val bitmap = Bitmap.createBitmap(cardView.width, cardView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            cardView.draw(canvas)

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Polary")
            }

            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            context.contentResolver.openOutputStream(uri!!).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }
            Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show()
        }
    }
}