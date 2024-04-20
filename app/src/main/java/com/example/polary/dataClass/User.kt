package com.example.polary.dataClass

data class User(
    var id: Int,
    val firebaseUID: String,
    val username: String,
    val email: String,
    val password: String?
)
