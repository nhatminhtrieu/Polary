package com.example.polary.dataClass

import java.io.Serializable

data class Author(
    val id: Number,
    val username: String,
    val avatar: String?
): Serializable
