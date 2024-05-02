package com.example.polary.dataClass

import java.io.Serializable

data class User(
    var id: Int,
    val firebaseUID: String,
    val username: String,
    val email: String,
    val password: String?
): Serializable
