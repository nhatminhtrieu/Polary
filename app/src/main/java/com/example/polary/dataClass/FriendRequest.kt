package com.example.polary.dataClass

data class FriendRequest(
    val sender: Friend?,
    val receiver: Friend?,
    val createdAt: String,
)
