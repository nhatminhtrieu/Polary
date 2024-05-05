package com.example.polary.`object`

import android.util.Log
import com.example.polary.Class.HttpMethod
import com.example.polary.dataClass.Friend
import com.example.polary.utils.ApiCallBack
import java.time.LocalDateTime

object FriendsData {
    var revalidate: Boolean = false
        set(value) {
            field = value
        }

    var updatedAt: LocalDateTime? = null
    private lateinit var friends: List<Friend>
    fun getFriends(userId: Int, TAG: String? = "FetchData", onSuccess: (List<Friend>?) -> Unit) {
        val now = LocalDateTime.now()
//        if (revalidate || updatedAt == null || now.minusMinutes(5).isAfter(updatedAt)) {
            // Get the friends from the database
            val httpMethod = HttpMethod()
            var response: List<Friend>? = null
            httpMethod.doGet<Friend>("users/$userId/friends", object : ApiCallBack<Any> {
                override fun onSuccess(data: Any) {
                    Log.d(TAG, "Successfully fetched friends: $data")
                    response = data as List<Friend>
                    friends = response as List<Friend>
                    updatedAt = now
                    onSuccess(friends)
                }

                override fun onError(error: Throwable) {
                    Log.e(TAG, "Failed to fetch friends: $error")
                }
            })
        }
    }