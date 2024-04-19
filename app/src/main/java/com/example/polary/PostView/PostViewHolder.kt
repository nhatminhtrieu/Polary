package com.example.polary.PostView

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.polary.R

interface ViewHolderListener {
    fun onLoadCompleted(view: ImageView, adapterPosition: Int)
    fun onItemClicked(view: View, position: Int)
}

class PostViewHolder(
    itemView: View,
    private val requestManager: RequestManager,
    private val viewHolderListener: ViewHolderListener
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val imageView: ImageView = itemView.findViewById(R.id.post_grid_image)

    init {
        itemView.findViewById<View>(R.id.post_grid_card).setOnClickListener(this)
    }

    fun onBind(imageUrl: String) {
        val adapterPosition = getAdapterPosition()
        imageView.transitionName = imageUrl
        setImage(imageUrl, adapterPosition)
    }

    private fun setImage(imageUrl: String, adapterPosition: Int) {
        requestManager
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    viewHolderListener.onLoadCompleted(imageView, adapterPosition)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    viewHolderListener.onLoadCompleted(imageView, adapterPosition)
                    return false
                }
            })
            .into(imageView)
    }

    override fun onClick(view: View) {
        viewHolderListener.onItemClicked(view, adapterPosition)
    }
}