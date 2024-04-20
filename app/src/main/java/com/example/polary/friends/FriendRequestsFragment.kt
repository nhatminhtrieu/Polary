package com.example.polary.friends

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.Class.CustomDividerItemDecoration
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.dataClass.Friend
import com.example.polary.dataClass.FriendRequest
import com.example.polary.utils.ApiCallBack

class FriendRequestsFragment :
    Fragment(),
    FriendsAdapter.OnFriendListener,
    FriendRequestsAdapter.OnFriendRequestListener
{
    private lateinit var sentFriendRequests: MutableList<FriendRequest>
    private lateinit var sentFriendRequestsAdapter: FriendRequestsAdapter
    private lateinit var rvSentFriendRequests : RecyclerView
    private lateinit var newFriendRequests: MutableList<Friend>
    private lateinit var newFriendRequestsAdapter: FriendsAdapter
    private lateinit var rvNewFriendRequests: RecyclerView
    private val userId = 1
    private val TAG = "Sent and new friend requests Fragment"

    override fun onDeleteSentRequest() {
        getUsers()
    }

    override fun onAcceptRequest() {
        // Do nothing
    }

    override fun onFriendRequestSuccess() {
        getSentFriendRequests()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sent_friend_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvSentFriendRequests = view.findViewById(R.id.recyclerView_sent_friend_requests)
        rvSentFriendRequests.layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(rvSentFriendRequests.context, LinearLayoutManager(requireContext()).orientation)
        rvSentFriendRequests.addItemDecoration(dividerItemDecoration)

        rvNewFriendRequests = view.findViewById(R.id.recyclerView_new_friend_requests)
        rvNewFriendRequests.layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration2 = CustomDividerItemDecoration(rvNewFriendRequests.context, LinearLayoutManager(requireContext()).orientation)
        rvNewFriendRequests.addItemDecoration(dividerItemDecoration2)
        getSentFriendRequests()
        getUsers()
    }

    private fun getSentFriendRequests() {
        val httpMethod = HttpMethod()
        httpMethod.doGetDataWithParams<FriendRequest>("friend-requests/$userId", mapOf("type" to "sender"), object:
            ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                sentFriendRequests = data as MutableList<FriendRequest>
                sentFriendRequestsAdapter = FriendRequestsAdapter(sentFriendRequests, userId, 1, this@FriendRequestsFragment)
                rvSentFriendRequests.adapter = sentFriendRequestsAdapter
                Log.d(TAG, "Successfully fetched friend requests data")
            }
            override fun onError(error: Throwable) {
                Log.e(TAG, "Failed to fetch friend requests data: $error")
            }
        })
    }

    private fun getUsers() {
        val httpMethod = HttpMethod()
        httpMethod.doGet<Friend>("users/$userId/non-friends", object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                newFriendRequests = data as MutableList<Friend>
                newFriendRequestsAdapter = FriendsAdapter(newFriendRequests, 1, userId, this@FriendRequestsFragment)
                rvNewFriendRequests.adapter = newFriendRequestsAdapter
                Log.d(TAG, "Successfully fetched friend requests data")
            }
            override fun onError(error: Throwable) {
                Log.e(TAG, "Failed to fetch friend requests data: $error")
            }
        })
    }
}