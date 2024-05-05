package com.example.polary.friends

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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
    FriendRequestsAdapter.OnFriendRequestListener,
    FriendsAdapter.OnFriendListener{
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var friends: MutableList<Friend>
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var rvFriend : RecyclerView
    private lateinit var friendRequests: MutableList<FriendRequest>
    private lateinit var friendRequestsAdapter: FriendRequestsAdapter
    private lateinit var rvFriendRequests: RecyclerView
    private lateinit var user: User
    private val TAG = "FriendsActivity"

    @SuppressLint("NotifyDataSetChanged")
    override fun onAcceptRequest() {
        Log.i(TAG, "Accept friend request")
        FriendsData.revalidate = true
        FriendsData.getFriends(user.id, TAG) {
            friends.clear()
            friends.addAll(it as MutableList<Friend>)
            friendsAdapter.notifyDataSetChanged()
            if (friends.size == 0) {
                configTopAppBar("Friends")
                requireActivity().findViewById<TextView>(R.id.no_friends_yet).visibility = View.VISIBLE
            } else {
                configTopAppBar("${friends.size} friend${if (friends.size > 1) "s" else ""}")
                requireActivity().findViewById<TextView>(R.id.no_friends_yet).visibility = View.GONE
            }
        }
    }

    override fun onChange() {
        if (friendRequests.size == 0) {
            requireActivity().findViewById<LinearLayout>(R.id.friend_requests_title).visibility = View.INVISIBLE
        }
        if (friends.size == 0) {
            configTopAppBar("Friends")
            requireActivity().findViewById<TextView>(R.id.no_friends_yet).visibility = View.VISIBLE
        } else {
            configTopAppBar("${friends.size} friend${if (friends.size > 1) "s" else ""}")
            requireActivity().findViewById<TextView>(R.id.no_friends_yet).visibility = View.GONE
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
            friendsAdapter = FriendsAdapter(friends, 0, user.id, false, this)
            rvFriend.adapter = friendsAdapter
            val loadMoreButton = view.findViewById<View>(R.id.btn_more_friends)
            val layoutMore = view.findViewById<View>(R.id.more_friends)
            val noFriendsYet = view.findViewById<TextView>(R.id.no_friends_yet)
            if (friends.size == 0) {
                noFriendsYet.visibility = View.VISIBLE
            }
            if (friends.size < 6) {
                layoutMore?.visibility = View.GONE
            }
            else {
                layoutMore?.visibility = View.VISIBLE
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
            friendRequestsAdapter = FriendRequestsAdapter(friendRequests, user.id, this)
            rvFriendRequests.adapter = friendRequestsAdapter
            if (friendRequests.size > 0)
                requireActivity().findViewById<LinearLayout>(R.id.friend_requests_title).visibility = View.VISIBLE
        }
    }

    private fun configTopAppBar(name: String) {
        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.app_top_app_bar)
        appBar.title = name
    }
}