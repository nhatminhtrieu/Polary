package com.example.polary.dataClass

import java.io.Serializable

data class Comment(
    val author: Author,
    val content: String,
    val createdAt: String
): Serializable
