package com.example.polary.usersList

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.polary.Class.Post
import com.example.polary.R

class PostAdapter(
    context: Activity,
    posts: ArrayList<Post>
) : ArrayAdapter<Post>(context, R.layout.item_post, posts) {

    private class ViewHolder {
        lateinit var caption: TextView
        lateinit var imageUrl: TextView
        lateinit var countComments: TextView
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if(convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_post, parent, false)
            viewHolder = ViewHolder()
            viewHolder.caption = view.findViewById<TextView>(R.id.caption)
            viewHolder.imageUrl = view.findViewById(R.id.image_post)
            viewHolder.countComments = view.findViewById(R.id.total_comment)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val post = getItem(position)
        viewHolder.caption.text = post!!.getCaption()
        viewHolder.imageUrl.text = post.getImageUrl()
        viewHolder.countComments.text = post.getCountComments().toString()

        return view
    }
}