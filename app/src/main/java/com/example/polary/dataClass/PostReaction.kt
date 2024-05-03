package com.example.polary.dataClass

import java.io.Serializable

data class PostReaction(
    val type: String,
    val author: User
): Serializable
