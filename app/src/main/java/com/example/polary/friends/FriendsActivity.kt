package com.example.polary.friends

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.Class.CustomDividerItemDecoration
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.dataClass.Friend
import com.example.polary.dataClass.FriendRequest
import com.example.polary.utils.ApiCallBack

class FriendsActivity : AppCompatActivity() {
    private lateinit var friends: List<Friend>
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var rvFriend : RecyclerView
    private lateinit var friendRequests: MutableList<FriendRequest>
    private lateinit var friendRequestsAdapter: FriendRequestsAdapter
    private lateinit var rvFriendRequests: RecyclerView
    private val userId = 1
    private val TAG = "FriendsActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        rvFriend = findViewById(R.id.recyclerView_friends)
        rvFriend.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = CustomDividerItemDecoration(rvFriend.context, LinearLayoutManager(this).orientation)
        rvFriend.addItemDecoration(dividerItemDecoration)
        rvFriendRequests = findViewById(R.id.recyclerView_friend_requests)
        rvFriendRequests.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration2 = CustomDividerItemDecoration(rvFriendRequests.context, LinearLayoutManager(this).orientation)
        rvFriendRequests.addItemDecoration(dividerItemDecoration2)
        getFriends(userId)
        getFriendRequests(userId)
    }

    private fun getFriends(userId: Number) {
        // get friends from server
        val httpMethod = HttpMethod()
        httpMethod.doGet<Friend>("users/${userId}/friends", object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                friends = data as List<Friend>
                friendsAdapter = FriendsAdapter(friends)
                rvFriend.adapter = friendsAdapter
                Log.d(TAG, "Successfully fetched friends data")
            }
            override fun onError(error: Throwable) {
                Log.e(TAG, "Failed to fetch friends data: $error")
            }
        })
    }

    private fun getFriendRequests(userId: Number) {
        // get friend requests from server
        val httpMethod = HttpMethod()
        httpMethod.doGetDataWithParams<FriendRequest>("friend-requests/$userId", mapOf("type" to "receiver"), object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                friendRequests = data as MutableList<FriendRequest>
                friendRequestsAdapter = FriendRequestsAdapter(friendRequests, userId)
                rvFriendRequests.adapter = friendRequestsAdapter
                Log.d(TAG, "Successfully fetched friend requests data")
            }
            override fun onError(error: Throwable) {
                Log.e(TAG, "Failed to fetch friend requests data: $error")
            }
        })
    }
}