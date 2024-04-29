package com.example.polary.Profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.polary.R
import com.example.polary.utils.SessionManager
import com.example.polary.Class.HttpMethod
import com.example.polary.dataClass.User
import com.example.polary.utils.ApiCallBack
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class UsernameFragment() : BottomSheetDialogFragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: User
    private var listener: OnUsernameFragmentListener? = null

    interface OnUsernameFragmentListener {
        fun onUsernameUpdated()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UsernameFragment.OnUsernameFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnAvatarUpdateListener")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.update_username_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!
        view.findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener {
            dismiss()
        }
        view.findViewById<MaterialButton>(R.id.btn_save).setOnClickListener {
            updateUsername()
        }
    }

    private fun updateUsername() {
        // Update the username
        val httpMethod = HttpMethod()
        val username = view?.findViewById<TextInputEditText>(R.id.input_username)?.text.toString()
        if (username.isEmpty()) {
            Toast.makeText(context, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        val requestBody = mapOf(
            "userId" to user.id,
            "username" to username
        )
        httpMethod.doPut("/users/${user.id}/username", requestBody, object : ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                Toast.makeText(context, "Username updated", Toast.LENGTH_SHORT).show()
                listener?.onUsernameUpdated()
                dismiss()
            }

            override fun onError(error: Throwable) {
                Log.e("Update username", "Failed to update username", error)
                Toast.makeText(context, "Failed to update username", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        })
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
}