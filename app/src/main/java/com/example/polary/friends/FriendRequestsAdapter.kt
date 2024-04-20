package com.example.polary.friends

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.dataClass.FriendRequest
import com.example.polary.utils.ApiCallBack
import com.google.android.material.button.MaterialButton
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

data class FriendRequestRequestBody(val senderId: Number, val receiverId: Number)
class FriendRequestsAdapter(
    private var friendRequests: MutableList<FriendRequest>,
    private val userId: Number,
    private val viewType: Int,
    private val listener: OnFriendRequestListener?
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface OnFriendRequestListener {
        fun onDeleteSentRequest()
        fun onAcceptRequest()
    }
    companion object {
        private const val VIEW_RECEIVED = 0
        private const val VIEW_SENT = 1
    }

    inner class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.friend_name)
        val avatar: ImageView = itemView.findViewById(R.id.friend_avatar)
        val deleteBtn: MaterialButton = itemView.findViewById(R.id.btn_delete_request)
        val acceptBtn: MaterialButton = itemView.findViewById(R.id.btn_accept_request)
        val time: TextView = itemView.findViewById(R.id.friend_time)
    }

    inner class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.friend_name)
        val avatar: ImageView = itemView.findViewById(R.id.friend_avatar)
        val cancelBtn: MaterialButton = itemView.findViewById(R.id.btn_cancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_RECEIVED -> {
                val view = inflater.inflate(R.layout.friend_request_item, parent, false)
                ReceivedViewHolder(view)
            }
            VIEW_SENT -> {
                val view = inflater.inflate(R.layout.sent_friend_request_item, parent, false)
                SentViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    fun removeAt(position: Int) {
        friendRequests.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val request = friendRequests[position]
        when (holder) {
            is ReceivedViewHolder -> {
                holder.name.text = request.sender?.username
                holder.time.text = getDuration(request.createdAt)
                Glide.with(holder.itemView).load(request.sender?.avatar).into(holder.avatar)
                holder.deleteBtn.setOnClickListener {
                    val pos = holder.adapterPosition
                    Log.i("FriendRequestsAdapter", "Delete button clicked at position $pos")
                    deleteReceivedRequest(pos)
                }
                holder.acceptBtn.setOnClickListener {
                    val pos = holder.adapterPosition
                    Log.i("FriendRequestsAdapter", "Accept button clicked at position $pos")
                    acceptRequest(pos)
                }
            }
            is SentViewHolder -> {
                holder.name.text = request.receiver?.username
                Glide.with(holder.itemView).load(request.receiver?.avatar).into(holder.avatar)
                holder.cancelBtn.setOnClickListener {
                    val pos = holder.adapterPosition
                    Log.i("FriendRequestsAdapter", "Cancel button clicked at position $pos")
                    deleteSentRequest(pos)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return friendRequests.size
    }

    private fun acceptRequest(position: Int) {
        val httpMethod = HttpMethod()
        val requestBody = FriendRequestRequestBody(friendRequests[position].sender?.id!!, userId)
        httpMethod.doPost("friend-requests/acceptance", requestBody, object:
            ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                removeAt(position)
                listener?.onAcceptRequest()
            }
            override fun onError(error: Throwable) {
                Log.e("FriendRequestsAdapter", "Failed to accept friend request: $error")
            }
        })
    }

    private fun deleteSentRequest(position: Int) {
        val httpMethod = HttpMethod()
        val params : Map<String, String> =
            mapOf("senderId" to userId.toString(), "receiverId" to friendRequests[position].receiver?.id.toString())
        httpMethod.doDelete("friend-requests", params, object:
            ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                removeAt(position)
                listener?.onDeleteSentRequest()
            }
            override fun onError(error: Throwable) {
                Log.e("FriendRequestsAdapter", "Failed to delete friend request: $error")
            }
        })
    }

    private fun deleteReceivedRequest(position: Int) {
        val httpMethod = HttpMethod()
        val params : Map<String, String> =
            mapOf("senderId" to friendRequests[position].sender?.id.toString(), "receiverId" to userId.toString())
        httpMethod.doDelete("friend-requests", params, object:
            ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                removeAt(position)
            }
            override fun onError(error: Throwable) {
                Log.e("FriendRequestsAdapter", "Failed to delete friend request: $error")
            }
        })
    }

    private fun getDuration(createdAt: String): String {
        val createdAtInstant = Instant.parse(createdAt).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val now = LocalDateTime.now()

        val minutesBetween = ChronoUnit.MINUTES.between(createdAtInstant, now)
        val hoursBetween = ChronoUnit.HOURS.between(createdAtInstant, now)
        val daysBetween = ChronoUnit.DAYS.between(createdAtInstant, now)
        val weeksBetween = ChronoUnit.WEEKS.between(createdAtInstant, now)

        return when {
            minutesBetween < 60 -> "$minutesBetween min"
            hoursBetween < 24 -> "${hoursBetween}h"
            daysBetween < 30 -> "${daysBetween}d"
            else -> "${weeksBetween + 1}w"
        }
    }
    
}