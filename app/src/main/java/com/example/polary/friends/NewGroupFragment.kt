package com.example.polary.friends

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.Class.CustomDividerItemDecoration
import com.example.polary.PostView.GroupMembersAdapter
import com.example.polary.R
import com.example.polary.dataClass.Group
import com.example.polary.dataClass.User
import com.example.polary.`object`.FriendsData
import com.example.polary.`object`.GroupsData
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.SessionManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

class NewGroupFragment:
    Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: User
    private var selected: MutableList<Int> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvMembers = view.findViewById<RecyclerView>(R.id.group_members_recycler_view)
        rvMembers.layoutManager = LinearLayoutManager(requireContext())
        configTopAppBar("New group")
        FriendsData.getFriends(user.id, "NewGroupFragment") { friends ->
            val groupMembersAdapter = friends?.let { GroupMembersAdapter(it, selected, true) }!!
            rvMembers.adapter = groupMembersAdapter
            val dividerItemDecoration = CustomDividerItemDecoration(rvMembers.context, LinearLayoutManager(requireContext()).orientation)
            rvMembers.addItemDecoration(dividerItemDecoration)
        }
    }

    private fun configTopAppBar(name: String) {
        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.app_top_app_bar)
        val menuItem = appBar?.menu?.findItem(R.id.edit)
        menuItem?.isEnabled = true
        menuItem?.isVisible = true
        menuItem?.title = "Save"
        menuItem?.setOnMenuItemClickListener {
            val groupName = requireView().findViewById<TextInputEditText>(R.id.group_name).text.toString()
            if (groupName.isEmpty())
                Toast.makeText(requireContext(), "Group name cannot be empty", Toast.LENGTH_SHORT).show()
            else {
                GroupsData.createGroup(user.id, groupName, selected, "NewGroupFragment") {
                    Toast.makeText(requireContext(), "Group $groupName created", Toast.LENGTH_SHORT).show()
                    GroupsData.revalidate = true
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
            true
        }
        appBar?.title = name
        appBar?.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_back)
        appBar?.setNavigationOnClickListener {
            // Go back to the parent activity
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}