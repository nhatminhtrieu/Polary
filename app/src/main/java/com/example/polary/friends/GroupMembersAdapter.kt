package com.example.polary.PostView

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.polary.dataClass.Comment
import com.example.polary.R
import com.example.polary.dataClass.Friend
import com.example.polary.dataClass.Group
import com.example.polary.dataClass.User
import com.example.polary.friends.FriendsAdapter
import com.example.polary.friends.GroupDetailsFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class GroupMembersAdapter(
    private var members: List<Friend>,
    private var selected: MutableList<Int> = mutableListOf(),
    private var isEditable: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class SelectableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.friend_name)
        val avatar: ImageView = itemView.findViewById(R.id.friend_avatar)
        init {
            if (isEditable) {
                itemView.setOnClickListener {
                    val friend = members[adapterPosition]
                    if (friend.id in selected) {
                        selected.remove(friend.id)
                    } else {
                        selected.add(friend.id)
                    }
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }
    inner class UnSelectableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.friend_name)
        val avatar: ImageView = itemView.findViewById(R.id.friend_avatar)
        val deleteBtn: MaterialButton = itemView.findViewById(R.id.btn_delete_friend)
        init {
            if (isEditable) {
                itemView.setOnClickListener {
                    val friend = members[adapterPosition]
                    if (friend.id in selected) {
                        selected.remove(friend.id)
                    } else {
                        selected.add(friend.id)
                    }
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val member = members[position]
        return if (member.id in selected) {
            SELECTED
        } else {
            UNSELECTED
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            UNSELECTED -> {
                val view = inflater.inflate(R.layout.friend_item, parent, false)
                UnSelectableViewHolder(view)
            }
            SELECTED -> {
                val view = inflater.inflate(R.layout.select_friend_item, parent, false)
                SelectableViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val member: Friend = members[position]
        when (holder) {
            is SelectableViewHolder -> {
                holder.username.text = member.username
                Glide.with(holder.itemView.context)
                    .load(member.avatar)
                    .circleCrop()
                    .into(holder.avatar)
            }
            is UnSelectableViewHolder -> {
                holder.username.text = member.username
                holder.deleteBtn.alpha = 0f
                Glide.with(holder.itemView.context)
                    .load(member.avatar)
                    .circleCrop()
                    .into(holder.avatar)
            }
        }
    }

    override fun getItemCount(): Int {
        return members.size
    }

    companion object {
        private const val UNSELECTED = 0
        private const val SELECTED = 1
    }
}