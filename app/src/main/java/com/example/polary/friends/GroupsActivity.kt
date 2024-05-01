package com.example.polary.friends

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.Class.CustomDividerItemDecoration
import com.example.polary.PostView.GroupsAdapter
import com.example.polary.R
import com.example.polary.dataClass.User
import com.example.polary.`object`.GroupsData
import com.example.polary.utils.SessionManager
import com.google.android.material.appbar.MaterialToolbar

class GroupsActivity : AppCompatActivity() {
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