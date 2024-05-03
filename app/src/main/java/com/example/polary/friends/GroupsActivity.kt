package com.example.polary.friends

import android.content.SharedPreferences
import android.os.Bundle
import com.example.polary.BaseActivity
import com.example.polary.R
import com.example.polary.dataClass.User
import com.example.polary.utils.SessionManager

class GroupsActivity : BaseActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the shared preferences and get the user from the shared preferences
        setContentView(R.layout.activity_groups)
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!
        val transaction = supportFragmentManager.beginTransaction()
        val groupListFragment = GroupListFragment()
        transaction.replace(R.id.fragment_container, groupListFragment)
        transaction.addToBackStack("GroupListFragment")
        transaction.commit()
    }
}