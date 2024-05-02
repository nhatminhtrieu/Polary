package com.example.polary.friends

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.Class.CustomDividerItemDecoration
import com.example.polary.R
import com.example.polary.dataClass.Friend
import com.example.polary.dataClass.FriendRequest
import com.example.polary.dataClass.User
import com.example.polary.`object`.FriendRequestsData
import com.example.polary.`object`.FriendsData
import com.example.polary.utils.SessionManager
import com.google.android.material.appbar.MaterialToolbar

class FriendsFragment:
    Fragment(),
    FriendRequestsAdapter.OnFriendRequestListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var friends: MutableList<Friend>
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var rvFriend : RecyclerView
    private lateinit var friendRequests: MutableList<FriendRequest>
    private lateinit var friendRequestsAdapter: FriendRequestsAdapter
    private lateinit var rvFriendRequests: RecyclerView
    private lateinit var user: User
    private val TAG = "FriendsActivity"
    override fun onDeleteSentRequest() {
        // Do nothing
    }

    override fun onAcceptRequest() {
        Log.i(TAG, "Accept friend request")
        FriendsData.revalidate = true
        FriendsData.getFriends(user.id, TAG) {
            friends = it as MutableList<Friend>
            friendsAdapter = FriendsAdapter(friends, 0, user.id, null, false)
            rvFriend.adapter = friendsAdapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
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

        FriendsData.getFriends(user.id, TAG) {
            friends = it as MutableList<Friend>
            friendsAdapter = FriendsAdapter(friends, 0, user.id, null, false)
            rvFriend.adapter = friendsAdapter
            val loadMoreButton = view.findViewById<View>(R.id.btn_more_friends)
            val layoutMore = view.findViewById<View>(R.id.more_friends)
            if (friends.size < 6) {
                loadMoreButton?.visibility = View.GONE
            }
            loadMoreButton?.setOnClickListener {
                // load more friends
                friendsAdapter.setIsLoadedAll(true)
                friendsAdapter.notifyDataSetChanged()
                layoutMore?.visibility = View.GONE
            }
            if (friends.size > 0)
                configTopAppBar("${friends.size} friend${if (friends.size > 1) "s" else ""}")
        }

        FriendRequestsData.getFriendRequestsOfReceiver(user.id, TAG) {
            friendRequests = it as MutableList<FriendRequest>
            friendRequestsAdapter = FriendRequestsAdapter(friendRequests, user.id, 0, this)
            rvFriendRequests.adapter = friendRequestsAdapter
        }
    }

    private fun configTopAppBar(name: String) {
        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.app_top_app_bar)
        appBar.title = name
    }
}