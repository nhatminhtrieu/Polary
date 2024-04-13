package com.example.polary.dataClass

data class User(
    val firebaseUID: String,
    val username: String,
    val email: String,
    val password: String?
)
