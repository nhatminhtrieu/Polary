package com.example.polary.friends

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.Class.CustomDividerItemDecoration
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.dataClass.Friend
import com.example.polary.dataClass.FriendRequest
import com.example.polary.dataClass.User
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.SessionManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale

class FriendRequestsFragment:
    Fragment(),
    NewFriendsAdapter.onChangeListener
{
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var nonFriends: MutableList<Friend>
    private lateinit var sentRequests: MutableList<FriendRequest>

    private lateinit var dataAdapter: NewFriendsAdapter
    private lateinit var rvNonFriends: RecyclerView

    private val searchNewFriends: MutableList<Friend> = mutableListOf()
    private val searchSentRequests: MutableList<FriendRequest> = mutableListOf()

    private lateinit var user: User
    private lateinit var inputSearch: TextInputEditText
    private lateinit var emptyText: LinearLayout
    private val TAG = "Sent and new friend requests Fragment"

    override fun onChange() {
        getUsers()
        getSentFriendRequests()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!
        return inflater.inflate(R.layout.fragment_sent_friend_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configTopAppBar()
        rvNonFriends = view.findViewById(R.id.recyclerView_new_friend_requests)
        rvNonFriends.layoutManager = LinearLayoutManager(requireContext())
        dataAdapter = NewFriendsAdapter(searchNewFriends, searchSentRequests, user.id, this@FriendRequestsFragment)
        rvNonFriends.adapter = dataAdapter
        val dividerItemDecoration = CustomDividerItemDecoration(rvNonFriends.context, LinearLayoutManager(requireContext()).orientation)
        rvNonFriends.addItemDecoration(dividerItemDecoration)

        emptyText = requireActivity().findViewById(R.id.empty_text)
        inputSearch = requireActivity().findViewById(R.id.input_search)
        getSentFriendRequests()
        getUsers()
        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Code here will be triggered before the text is changed
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val searchText = s.toString().lowercase(Locale.ROOT)
                val newFriendsToAdd = nonFriends.filter { friend ->
                    friend.username.lowercase(Locale.ROOT).contains(searchText)
                }
                searchNewFriends.clear()
                searchNewFriends.addAll(newFriendsToAdd)

                // Filter and add sent requests
                val sentRequestsToAdd = sentRequests.filter { request ->
                    request.receiver?.username?.lowercase(Locale.ROOT)?.contains(searchText) == true
                }
                searchSentRequests.clear()
                searchSentRequests.addAll(sentRequestsToAdd)
                dataAdapter.notifyDataSetChanged()
                viewEmptyText()
            }

            override fun afterTextChanged(s: Editable) {
                // Code here will be triggered after the text has been changed
            }
        })
    }

    private fun getSentFriendRequests() {
        val httpMethod = HttpMethod()
        httpMethod.doGetWithQuery<FriendRequest>("friend-requests/${user.id}", mapOf("type" to "sender"), object:
            ApiCallBack<Any> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onSuccess(data: Any) {
                sentRequests = data as MutableList<FriendRequest>
                val searchText = inputSearch.text.toString().lowercase(Locale.ROOT)
                val sentRequestsToAdd = sentRequests.filter { request ->
                    request.receiver?.username?.lowercase(Locale.ROOT)?.contains(searchText) == true
                }
                searchSentRequests.clear()
                searchSentRequests.addAll(sentRequestsToAdd)
                dataAdapter.notifyDataSetChanged()
                viewEmptyText()
                Log.d(TAG, "Successfully fetched friend requests data")
            }
            override fun onError(error: Throwable) {
                Log.e(TAG, "Failed to fetch friend requests data: $error")
            }
        })
    }

    private fun getUsers() {
        val httpMethod = HttpMethod()
        httpMethod.doGet<Friend>("users/${user.id}/non-friends", object: ApiCallBack<Any> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onSuccess(data: Any) {
                nonFriends = data as MutableList<Friend>
                val searchText = inputSearch.text.toString().lowercase(Locale.ROOT)
                val newFriendsToAdd = nonFriends.filter { friend ->
                    friend.username.lowercase(Locale.ROOT).contains(searchText)
                }
                searchNewFriends.clear()
                searchNewFriends.addAll(newFriendsToAdd)
                dataAdapter.notifyDataSetChanged()
                viewEmptyText()
                Log.d(TAG, "Successfully fetched friend requests data")
            }
            override fun onError(error: Throwable) {
                Log.e(TAG, "Failed to fetch friend requests data: $error")
            }
        })
    }

    private fun configTopAppBar() {
        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.app_top_app_bar)
        appBar.title = "New friends"
    }

    private fun viewEmptyText() {
        if (searchSentRequests.size + searchNewFriends.size == 0) {
            emptyText.visibility = View.VISIBLE
        } else {
            emptyText.visibility = View.INVISIBLE
        }
    }
}