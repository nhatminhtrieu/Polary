package com.example.polary.dataClass

import java.io.Serializable

data class Post(
    val id: Number,
    val author: Author,
    val caption: String,
    var imageUrl: String,
    val reactions: List<PostReaction>,
    val countComments: Number,
    val frame: Int,
    val font: Int
) : Serializable
