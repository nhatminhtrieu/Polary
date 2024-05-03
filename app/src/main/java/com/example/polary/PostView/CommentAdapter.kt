package com.example.polary.PostView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.polary.dataClass.Comment
import com.example.polary.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class CommentAdapter(private var comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val author: TextView = itemView.findViewById(R.id.comment_author)
        val content: TextView = itemView.findViewById(R.id.comment_content)
        val date: TextView = itemView.findViewById(R.id.comment_date)
        val avatar: ImageView = itemView.findViewById(R.id.user_avatar_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val commentView = inflater.inflate(R.layout.comment_item, parent, false)
        return ViewHolder(commentView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment: Comment = comments[position]
        holder.author.text = comment.author.username
        holder.content.text = comment.content
        holder.date.text = getDuration(comment.createdAt)
        Glide.with(holder.itemView).load(comment.author.avatar).into(holder.avatar)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    private fun getDuration(createdAt: String): String {
        val createdAtDate = Instant.parse(createdAt).atZone(ZoneId.systemDefault()).toLocalDate()

        val today = LocalDate.now()

        return when (val daysBetween = ChronoUnit.DAYS.between(createdAtDate, today)) {
            0L -> "Today"
            1L -> "Yesterday"
            else -> "$daysBetween days ago"
        }
    }
}