package com.example.polary.dataClass

import java.io.Serializable

data class PostNotification(
    val id: Number,
    val emoji: String,
    val postId: String,
): Serializable
