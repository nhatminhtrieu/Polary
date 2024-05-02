package com.example.polary.`object`

import android.util.Log
import com.example.polary.Class.HttpMethod
import com.example.polary.dataClass.FriendRequest
import com.example.polary.utils.ApiCallBack
import java.time.LocalDateTime

object FriendRequestsData {
    var revalidate: Boolean = false
        set(value) {
            field = value
        }

    var updatedAt: LocalDateTime? = null
    private var friendRequestsOfSender: List<FriendRequest> = listOf()
    private var friendRequestsOfReceiver: List<FriendRequest> = listOf()
    fun getFriendRequestsOfReceiver(userId: Int, TAG: String? = "FetchData", onSuccess: (List<FriendRequest>?) -> Unit) {
        val now = LocalDateTime.now()
        if (revalidate || updatedAt == null || now.minusMinutes(5).isAfter(updatedAt)) {
            revalidate = false
            // Get the friends from the database
            val httpMethod = HttpMethod()
            httpMethod.doGetWithQuery<FriendRequest>("friend-requests/$userId", mapOf("type" to "receiver"), object:
                ApiCallBack<Any> {
                override fun onSuccess(data: Any) {
                    friendRequestsOfReceiver = data as List<FriendRequest>
                    onSuccess(friendRequestsOfReceiver)
                    Log.d(TAG, "Successfully fetched friend requests data")
                }
                override fun onError(error: Throwable) {
                    Log.e(TAG, "Failed to fetch friend requests data: $error")
                }
            })
        }
        else {
            onSuccess(friendRequestsOfSender)
        }
    }

    fun getFriendRequestsOfSender(userId: Int, TAG: String? = "FetchData", onSuccess: (List<FriendRequest>?) -> Unit) {
        val now = LocalDateTime.now()
        if (revalidate || updatedAt == null || now.minusMinutes(5).isAfter(updatedAt)) {
            revalidate = false
            // Get the friends from the database
            val httpMethod = HttpMethod()
            httpMethod.doGetWithQuery<FriendRequest>("friend-requests/$userId", mapOf("type" to "sender"), object:
                ApiCallBack<Any> {
                override fun onSuccess(data: Any) {
                    friendRequestsOfSender = data as List<FriendRequest>
                    onSuccess(friendRequestsOfSender)
                    Log.d(TAG, "Successfully fetched friend requests data")
                }
                override fun onError(error: Throwable) {
                    Log.e(TAG, "Failed to fetch friend requests data: $error")
                }
            })
        }
        else {
            onSuccess(friendRequestsOfSender)
        }
    }
    fun cntFriendRequestsOfReceiver(): Int {
        return friendRequestsOfReceiver.size
    }

}