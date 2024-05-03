package com.example.polary.friends

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.Class.CustomDividerItemDecoration
import com.example.polary.PostView.GroupMembersAdapter
import com.example.polary.R
import com.example.polary.dataClass.Group
import com.example.polary.dataClass.User
import com.example.polary.`object`.GroupsData
import com.example.polary.utils.SessionManager
import com.google.android.material.appbar.MaterialToolbar

class GroupDetailsFragment:
    Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: User
    private lateinit var group: Group

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val groupId = arguments?.getSerializable("groupId")
        val rvMembers = view.findViewById<RecyclerView>(R.id.group_members_recycler_view)
        rvMembers.layoutManager = LinearLayoutManager(requireContext())
        GroupsData.getGroupById(groupId.toString(), "GroupDetailsFragment") { group ->
            if (group != null) {
                this.group = group
                configTopAppBar(group.name)
                if (group.members.isNullOrEmpty()) {
                    val textView = TextView(requireContext())
                    textView.text = "This group has no member yet"
                    textView.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    textView.gravity = Gravity.CENTER
                    (rvMembers.parent as ViewGroup).addView(textView)
                    rvMembers.visibility = View.GONE
                } else {
                    val groupMembersAdapter = GroupMembersAdapter(group.members)
                    rvMembers.adapter = groupMembersAdapter
                    val dividerItemDecoration = CustomDividerItemDecoration(rvMembers.context, LinearLayoutManager(requireContext()).orientation)
                    rvMembers.addItemDecoration(dividerItemDecoration)
                }
            }
        }
    }

    private fun configTopAppBar(name: String) {
        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.app_top_app_bar)
        val menuItem = appBar?.menu?.findItem(R.id.edit)
        menuItem?.isEnabled = true
        menuItem?.isVisible = true
        menuItem?.title = "Edit"
        menuItem?.setOnMenuItemClickListener {
            val groupEditFragment = GroupEditFragment()
            val bundle = Bundle()
            bundle.putInt("id", group.id)
            bundle.putString("name", group.name)
            bundle.putIntegerArrayList("selected", group.members?.map { it.id } as ArrayList<Int>)
            groupEditFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, groupEditFragment)
                .addToBackStack("GroupEdit").commit()
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