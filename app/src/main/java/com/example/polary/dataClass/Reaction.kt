package com.example.polary.dataClass

import java.io.Serializable

data class Reaction(
    val author: Author,
    val type: String,
    val createdAt: String
): Serializable
