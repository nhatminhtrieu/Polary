package com.example.polary.friends

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.Class.CustomDividerItemDecoration
import com.example.polary.PostView.GroupMembersAdapter
import com.example.polary.R
import com.example.polary.dataClass.User
import com.example.polary.`object`.FriendsData
import com.example.polary.`object`.GroupsData
import com.example.polary.utils.SessionManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlin.properties.Delegates

class GroupEditFragment:
    Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: User
    private lateinit var selected : MutableList<Int>
    private var groupId by Delegates.notNull<Int>()
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configTopAppBar()
        groupId = arguments?.getInt("id")!!
        val groupName = arguments?.getString("name")
        view.findViewById<TextInputEditText>(R.id.group_name).setText(groupName)
        selected = arguments?.getIntegerArrayList("selected") as MutableList<Int>
        val rvMembers = view.findViewById<RecyclerView>(R.id.group_members_recycler_view)
        rvMembers.layoutManager = LinearLayoutManager(requireContext())
        FriendsData.getFriends(user.id, "GroupFragment") { friends ->
            val groupMembersAdapter = friends?.let { GroupMembersAdapter(it, selected, true) }!!
            rvMembers.adapter = groupMembersAdapter
            val dividerItemDecoration = CustomDividerItemDecoration(rvMembers.context, LinearLayoutManager(requireContext()).orientation)
            rvMembers.addItemDecoration(dividerItemDecoration)
        }
        view.findViewById<MaterialButton>(R.id.btn_delete_group).setOnClickListener {
            val builder = MaterialAlertDialogBuilder(it.context)
            builder.setTitle("Delete group")
            builder.setMessage("Are you sure you want to delete $groupName?")
            builder.setPositiveButton("Delete") { dialog, _ ->
                GroupsData.deleteGroup(groupId) {
                    Toast.makeText(requireContext(), "Group deleted", Toast.LENGTH_SHORT).show()
                    GroupsData.revalidate = true
                    requireActivity().supportFragmentManager.popBackStackImmediate("GroupDetailsFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
            builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            dialog = builder.create()
            dialog.show()
        }
    }

    private fun configTopAppBar() {
        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.app_top_app_bar)
        val menuItem = appBar?.menu?.findItem(R.id.edit)
        menuItem?.isEnabled = true
        menuItem?.isVisible = true
        menuItem?.title = "Save"
        menuItem?.setOnMenuItemClickListener {
            val name = view?.findViewById<TextInputEditText>(R.id.group_name)?.text.toString()
            GroupsData.editGroup(groupId, name, selected) {
                Toast.makeText(requireContext(), "Group edited", Toast.LENGTH_SHORT).show()
                GroupsData.revalidate = true
                requireActivity().supportFragmentManager.popBackStack()
            }
            true
        }
        appBar?.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_back)
        appBar?.setNavigationOnClickListener {
            // Go back to the parent activity
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}