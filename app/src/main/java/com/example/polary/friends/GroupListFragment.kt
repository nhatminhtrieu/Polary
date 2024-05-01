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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.Class.CustomDividerItemDecoration
import com.example.polary.Class.HttpMethod
import com.example.polary.PostView.GroupsAdapter
import com.example.polary.R
import com.example.polary.dataClass.Friend
import com.example.polary.dataClass.FriendRequest
import com.example.polary.dataClass.User
import com.example.polary.`object`.GroupsData
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.SessionManager
import com.google.android.material.appbar.MaterialToolbar

class GroupListFragment:
    Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: User
    private lateinit var groupsAdapter: GroupsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvGroup = view.findViewById<RecyclerView>(R.id.groups_recycler_view)
        rvGroup.layoutManager = LinearLayoutManager(requireContext())
        configTopAppBar("Groups")
        GroupsData.getGroups(user.id, "GroupsActivity") { groups ->
            groupsAdapter = groups?.let { GroupsAdapter(it) }!!
            rvGroup.adapter = groupsAdapter
            val dividerItemDecoration = CustomDividerItemDecoration(rvGroup.context, LinearLayoutManager(requireContext()).orientation)
            rvGroup.addItemDecoration(dividerItemDecoration)
        }
    }

    private fun configTopAppBar(name: String) {
        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.app_top_app_bar)
        val menuItem = appBar?.menu?.findItem(R.id.edit)
        menuItem?.isEnabled = true
        menuItem?.isVisible = true
        menuItem?.title = "New"
        menuItem?.setOnMenuItemClickListener {
            val newGroupFragment = NewGroupFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newGroupFragment)
                .addToBackStack("New group").commit()
            true
        }
        appBar?.title = name
        appBar?.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_back)
        appBar?.setNavigationOnClickListener {
            // Go back to the parent activity
            requireActivity().finish()
        }
    }
}