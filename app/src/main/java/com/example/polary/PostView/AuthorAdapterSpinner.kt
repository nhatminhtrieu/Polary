package com.example.polary.PostView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.polary.R
import com.example.polary.dataClass.Author

class AuthorAdapterSpinner(context: Context, private val layoutId: Int, users: List<Author>) : ArrayAdapter<Author>(context, layoutId, users) {
    // Override methods here to customize the adapter
    inner class ViewHolder(view: View) {
        val username: TextView = view.findViewById(R.id.author_username)
        val avatar: ImageView = view.findViewById(R.id.user_avatar_view)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initGetView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initGetView(position, convertView, parent)
    }

    private fun initGetView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if(convertView == null) {
            view = View.inflate(context, layoutId, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val author = getItem(position)
        viewHolder.username.text = if(author?.id.toString() == "2"){
            "You"
        } else {
            author?.username
        }
        val image = if (author?.avatar.isNullOrEmpty()) {
            if (position == 0) R.drawable.ic_people else R.drawable.ic_launcher_foreground
        } else {
            author?.avatar
        }
        Glide.with(context).load(image).into(viewHolder.avatar)
        return view
    }
}