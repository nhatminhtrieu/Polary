package com.example.polary.friends

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.polary.R
import com.example.polary.dataClass.User
import com.example.polary.utils.SessionManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

class FriendsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the shared preferences and get the user from the shared preferences
        setContentView(R.layout.activity_friends)
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!

        val inputSearch = findViewById<TextInputEditText>(R.id.input_search)
        // Remove focus when pressing "Done"
        inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputSearch.clearFocus()
            }
            false
        }

        val friendsFragment = FriendsFragment()
        val friendRequestsFragment = FriendRequestsFragment()

        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Code here will be triggered before the text is changed
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val transaction = supportFragmentManager.beginTransaction()
                // Code here will be triggered when the text is being changed
                if (s.isNotEmpty()) {
                    // When the search input has text, add the FriendRequestsFragment
                    transaction.replace(R.id.friendsFragment, friendRequestsFragment)
                } else {
                    // When the search input is empty, add the FriendsFragment
                    transaction.replace(R.id.friendsFragment, friendsFragment)
                }
                transaction.commit()
            }

            override fun afterTextChanged(s: Editable) {
                // Code here will be triggered after the text has been changed
            }
        })
        inputSearch.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                inputSearch.clearFocus()
                inputSearch.dispatchWindowFocusChanged(false)
                return@setOnKeyListener true
            }
            false
        }
        configTopAppBar()
    }

    private fun configTopAppBar() {
        val appBar = findViewById<MaterialToolbar>(R.id.app_top_app_bar)
        appBar?.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_back)
        appBar?.setNavigationOnClickListener {
            // Go back to the parent activity
            finish()
        }
    }
}