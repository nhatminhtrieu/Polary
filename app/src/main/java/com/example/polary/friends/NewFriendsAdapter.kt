package com.example.polary.friends

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextWatcher
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
import com.example.polary.dataClass.FriendRequest
import com.example.polary.utils.ApiCallBack
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale
import kotlin.math.min


class NewFriendsAdapter(
    private var searchNonFriends: MutableList<Friend>,
    private var searchSentRequests: MutableList<FriendRequest>,
    private val userId: Number,
    private val listener: FriendRequestsFragment?
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_REQUEST = 0
        private const val VIEW_NEW_FRIEND = 1
    }
    interface onChangeListener {
        fun onChange()
    }
    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.friend_name)
        val avatar: ImageView = itemView.findViewById(R.id.friend_avatar)
        val cancelBtn: MaterialButton = itemView.findViewById(R.id.btn_cancel)
    }
    inner class NewFriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.friend_name)
        val avatar: ImageView = itemView.findViewById(R.id.friend_avatar)
        val addBtn: MaterialButton = itemView.findViewById(R.id.btn_add_friend)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_REQUEST -> {
                val view = inflater.inflate(R.layout.sent_friend_request_item, parent, false)
                RequestViewHolder(view)
            }
            VIEW_NEW_FRIEND -> {
                val view = inflater.inflate(R.layout.new_friend_item, parent, false)
                NewFriendViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < searchSentRequests.size) {
            VIEW_REQUEST
        } else {
            VIEW_NEW_FRIEND
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RequestViewHolder -> {
                val request = searchSentRequests[position]
                holder.name.text = request.receiver?.username
                Glide.with(holder.itemView).load(request.receiver?.avatar).into(holder.avatar)
                holder.cancelBtn.setOnClickListener {
                    val pos = holder.adapterPosition
                    Log.i("FriendRequestsAdapter", "Cancel button clicked at position $pos")
                    deleteSentRequest(pos)
                }
            }
            is NewFriendViewHolder -> {
                val friend = searchNonFriends[position - searchSentRequests.size]
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
        if (position < searchSentRequests.size) {
            searchSentRequests.removeAt(position)
        } else searchNonFriends.removeAt(position - searchSentRequests.size)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return searchSentRequests.size + searchNonFriends.size
    }

    private fun createRequest(position: Int, context: Context) {
        // create friend request
        val friendId = searchNonFriends[position - searchSentRequests.size].id
        val httpMethod = HttpMethod()
        val requestBody = mapOf("senderId" to userId, "receiverId" to friendId)
        httpMethod.doPost("friend-requests", requestBody, object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                // show success message
                removeAt(position)
                listener?.onChange()
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

    private fun deleteSentRequest(position: Int) {
        val httpMethod = HttpMethod()
        val params : Map<String, String> =
            mapOf("senderId" to userId.toString(), "receiverId" to searchSentRequests[position].receiver?.id.toString())
        httpMethod.doDelete("friend-requests", params, object:
            ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                removeAt(position)
                listener?.onChange()
            }
            override fun onError(error: Throwable) {
                Log.e("FriendRequestsAdapter", "Failed to delete friend request: $error")
            }
        })
    }

}