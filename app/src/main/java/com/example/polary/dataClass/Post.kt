package com.example.polary.dataClass

import java.io.Serializable

data class Post(
    val id: Number,
    val author: PostAuthor,
    val caption: String,
    val imageUrl: String,
    val reactions: List<PostReaction>,
    val countComments: Number
) : Serializable
