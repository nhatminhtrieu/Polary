package com.example.polary.utils

import android.content.SharedPreferences
import android.util.Log
import com.example.polary.dataClass.User
import com.google.gson.Gson

class SessionManager(private val sharedPreferences: SharedPreferences) {
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun saveUserToSharedPreferences(user: User) {
        val gson = Gson()
        val userJson = gson.toJson(user)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", true)
            putString("user", userJson)
            apply()
        }
    }

    fun getUserFromSharedPreferences(): User? {
        val userJson = sharedPreferences.getString("user", null) ?: return null
        Log.d("SessionManager", "User JSON: $userJson")
        val gson = Gson()
        return gson.fromJson(userJson, User::class.java)
    }
}
