package com.example.polary.Profile

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

class PasswordFragment() : BottomSheetDialogFragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: User
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.update_password_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!
        view.findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener {
            dismiss()
        }
        view.findViewById<MaterialButton>(R.id.btn_save).setOnClickListener {
            updatePassword()
        }
    }

    private fun validate(currentPassword: String, newPassword: String, confirmPassword: String): Boolean {
        if (currentPassword.isEmpty()) {
            Toast.makeText(context, "Current password cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPassword.isEmpty()) {
            Toast.makeText(context, "New password cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (confirmPassword.isEmpty()) {
            Toast.makeText(context, "Confirm password cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPassword != confirmPassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun updatePassword() {
        // Update the username
        val httpMethod = HttpMethod()
        val currentPassword = view?.findViewById<TextInputEditText>(R.id.input_current_password)?.text.toString()
        val newPassword = view?.findViewById<TextInputEditText>(R.id.input_new_password)?.text.toString()
        val confirmPassword = view?.findViewById<TextInputEditText>(R.id.input_confirm_password)?.text.toString()
        if (!validate(currentPassword, newPassword, confirmPassword)) {
            Toast.makeText(context, "Please enter the right password", Toast.LENGTH_SHORT).show()
            return
        }
        val requestBody = mapOf(
            "userId" to user.id,
            "currentPassword" to currentPassword,
            "newPassword" to newPassword
        )
        httpMethod.doPut("/users/${user.id}/password", requestBody, object : ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                Log.i(TAG, "Username updated")
                Toast.makeText(context, "Username updated", Toast.LENGTH_SHORT).show()
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