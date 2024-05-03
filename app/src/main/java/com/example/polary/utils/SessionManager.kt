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
        val gson = Gson()
        return gson.fromJson(userJson, User::class.java)
    }

    fun saveAppIcon(aliasName: String?) {
        if (aliasName != null) {
            with(sharedPreferences.edit()) {
                putString("appIcon", aliasName)
                apply()
            }
        }
        else {
            with(sharedPreferences.edit()) {
                remove("appIcon")
                apply()
            }
        }
    }

    fun getAppIcon(): String {
        val appIcon = sharedPreferences.getString("appIcon", null) ?: return "default"
        Log.d("SessionManager", "App icon: $appIcon")
        return appIcon
    }
}
