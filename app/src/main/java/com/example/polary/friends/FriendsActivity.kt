package com.example.polary.friends

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.example.polary.R
import com.google.android.material.textfield.TextInputEditText

class FriendsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
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
    }
}