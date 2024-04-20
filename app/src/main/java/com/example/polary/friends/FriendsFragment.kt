package com.example.polary.friends

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.Class.CustomDividerItemDecoration
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.dataClass.Friend
import com.example.polary.dataClass.FriendRequest
import com.example.polary.utils.ApiCallBack

class FriendsFragment :
    Fragment(),
    FriendRequestsAdapter.OnFriendRequestListener {
    private lateinit var friends: MutableList<Friend>
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var rvFriend : RecyclerView
    private lateinit var friendRequests: MutableList<FriendRequest>
    private lateinit var friendRequestsAdapter: FriendRequestsAdapter
    private lateinit var rvFriendRequests: RecyclerView
    private val userId = 1
    private val TAG = "FriendsActivity"
    override fun onDeleteSentRequest() {
        // Do nothing
    }

    override fun onAcceptRequest() {
        Log.i(TAG, "Accept friend request")
        getFriends()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvFriend = view.findViewById(R.id.recyclerView_friends)
        rvFriend.layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = CustomDividerItemDecoration(rvFriend.context, LinearLayoutManager(requireContext()).orientation)
        rvFriend.addItemDecoration(dividerItemDecoration)
        rvFriendRequests = view.findViewById(R.id.recyclerView_friend_requests)
        rvFriendRequests.layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration2 = CustomDividerItemDecoration(rvFriendRequests.context, LinearLayoutManager(requireContext()).orientation)
        rvFriendRequests.addItemDecoration(dividerItemDecoration2)
        getFriends()
        getFriendRequests()
    }


    private fun getFriends() {
        // get friends from server
        val httpMethod = HttpMethod()
        httpMethod.doGet<Friend>("users/${userId}/friends", object: ApiCallBack<Any> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onSuccess(data: Any) {
                friends = data as MutableList<Friend>
                friendsAdapter = FriendsAdapter(friends, 0, userId, null, false)
                rvFriend.adapter = friendsAdapter

                val loadMoreButton = view?.findViewById<View>(R.id.btn_more_friends)
                val layoutMore = view?.findViewById<View>(R.id.more_friends)
                if (friends.size < 6) {
                    loadMoreButton?.visibility = View.GONE
                }
                loadMoreButton?.setOnClickListener {
                    // load more friends
                    friendsAdapter.setIsLoadedAll(true)
                    friendsAdapter.notifyDataSetChanged()
                    layoutMore?.visibility = View.GONE
                }
                Log.d(TAG, "Successfully fetched friends data")
            }
            override fun onError(error: Throwable) {
                Log.e(TAG, "Failed to fetch friends data: $error")
            }
        })
    }

    private fun getFriendRequests() {
        // get friend requests from server
        val httpMethod = HttpMethod()
        httpMethod.doGetDataWithParams<FriendRequest>("friend-requests/$userId", mapOf("type" to "receiver"), object:
            ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                friendRequests = data as MutableList<FriendRequest>
                friendRequestsAdapter = FriendRequestsAdapter(friendRequests, userId, 0, null)
                rvFriendRequests.adapter = friendRequestsAdapter
                Log.d(TAG, "Successfully fetched friend requests data")
            }
            override fun onError(error: Throwable) {
                Log.e(TAG, "Failed to fetch friend requests data: $error")
            }
        })
    }
}