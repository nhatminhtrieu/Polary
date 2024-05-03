package com.example.polary.PostView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.polary.R
import com.example.polary.constant.EmojiDrawable
import com.example.polary.dataClass.Reaction

class ReactionAdapter(private val reactions: List<Reaction>) : RecyclerView.Adapter<ReactionAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val author: TextView = itemView.findViewById(R.id.reaction_author)
        val type: ImageView = itemView.findViewById(R.id.reaction_type)
        val avatar: ImageView = itemView.findViewById(R.id.user_avatar_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val reactionView = inflater.inflate(R.layout.reaction_item, parent, false)
        return ViewHolder(reactionView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reaction: Reaction = reactions[position]
        val emojiDrawableId = EmojiDrawable.map[reaction.type]
        holder.author.text = reaction.author.username
        Glide.with(holder.itemView).load(emojiDrawableId).into(holder.type)
        Glide.with(holder.itemView).load(reaction.author.avatar).into(holder.avatar)
    }

    override fun getItemCount(): Int {
        return reactions.size
    }

}