package com.example.polary.friends

import android.app.AlertDialog
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.polary.R
import com.example.polary.dataClass.Friend
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class FriendsAdapter(private var friends: List<Friend>): RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.friend_name)
        val avatar: ImageView = itemView.findViewById(R.id.friend_avatar)
        val deleteBtn: MaterialButton = itemView.findViewById(R.id.btn_delete_friend)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.friend_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]
        holder.name.text = friend.username
        Glide.with(holder.itemView).load(friend.avatar).into(holder.avatar)

        holder.deleteBtn.setOnClickListener {
            // Create an AlertDialog
            val builder = MaterialAlertDialogBuilder(it.context)
            builder.setTitle("Unfriend")
            builder.setMessage("Are you sure you want to unfriend ${friend.username}?")
            builder.setPositiveButton("Unfriend") { dialog, _ ->
                // delete friend from server
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }
}