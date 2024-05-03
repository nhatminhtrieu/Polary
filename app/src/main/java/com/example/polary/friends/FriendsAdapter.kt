package com.example.polary.friends

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.dataClass.Friend
import com.example.polary.utils.ApiCallBack
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.min


class FriendsAdapter(
    private var friends: MutableList<Friend>,
    private val viewType: Int,
    private val userId: Number,
    private val listener: FriendRequestsFragment?,
    private var isLoadedAll: Boolean = true,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var dialog: AlertDialog
    interface OnFriendListener {
        fun onFriendRequestSuccess()
    }
    companion object {
        private const val VIEW_FRIEND = 0
        private const val VIEW_NEW_FRIEND = 1
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.friend_name)
        val avatar: ImageView = itemView.findViewById(R.id.friend_avatar)
        val deleteBtn: MaterialButton = itemView.findViewById(R.id.btn_delete_friend)
    }

    inner class NewFriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.friend_name)
        val avatar: ImageView = itemView.findViewById(R.id.friend_avatar)
        val addBtn: MaterialButton = itemView.findViewById(R.id.btn_add_friend)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_FRIEND -> {
                val view = inflater.inflate(R.layout.friend_item, parent, false)
                FriendViewHolder(view)
            }
            VIEW_NEW_FRIEND -> {
                val view = inflater.inflate(R.layout.new_friend_item, parent, false)
                NewFriendViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val friend = friends[position]
        when (holder) {
            is FriendViewHolder -> {
                holder.name.text = friend.username
                Glide.with(holder.itemView).load(friend.avatar).into(holder.avatar)

                holder.deleteBtn.setOnClickListener {
                    // Create an AlertDialog
                    val pos = holder.adapterPosition
                    val builder = MaterialAlertDialogBuilder(it.context)
                    builder.setTitle("Unfriend")
                    builder.setMessage("Are you sure you want to unfriend ${friend.username}?")
                    builder.setPositiveButton("Unfriend") { dialog, _ ->
                        // delete friend from server
                        deleteFriend(pos)
                    }
                    builder.setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    dialog = builder.create()
                    dialog.show()
                }
            }
            is NewFriendViewHolder -> {
                holder.name.text = friend.username
                Glide.with(holder.itemView).load(friend.avatar).into(holder.avatar)
                holder.addBtn.setOnClickListener {
                    val pos = holder.adapterPosition
                    createRequest(pos, holder.itemView.context)
                }
            }
        }

    }
    fun removeAt(position: Int) {
        friends.removeAt(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setIsLoadedAll(value: Boolean) {
        isLoadedAll = value
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (isLoadedAll) friends.size
        else min(5, friends.size)
    }

    private fun createRequest(position: Int, context: Context) {
        // create friend request
        val friendId = friends[position].id
        val httpMethod = HttpMethod()
        val requestBody = mapOf("senderId" to userId, "receiverId" to friendId)
        httpMethod.doPost("friend-requests", requestBody, object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                // show success message
                removeAt(position)
                listener?.onFriendRequestSuccess()
            }

            override fun onError(error: Throwable) {
                Log.e("FriendsAdapter", "Failed to send friend request: $error")
                // show error message
                Toast.makeText(
                    context,
                    "Failed to send friend request. Please try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun deleteFriend(position: Int) {
        val httpMethod = HttpMethod()
        val friendId = friends[position].id
        httpMethod.doDelete("users/$userId/friend/$friendId", mapOf(), object:
            ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                removeAt(position)
                dialog.dismiss()
            }
            override fun onError(error: Throwable) {
                Log.e("FriendsAdapter", "Failed to delete friend: $error")
            }
        })
    }
}