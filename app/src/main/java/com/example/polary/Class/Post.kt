package com.example.polary.Class

import com.google.gson.annotations.SerializedName
import java.io.Serial

class Post{
    @SerializedName("id")
    private lateinit var id: Number

    @SerializedName("caption")
    private lateinit var caption: String

    @SerializedName("imageUrl")
    private lateinit var imageUrl: String
    private lateinit var reactions: ArrayList<Reaction>

    @SerializedName("countComments")
    private lateinit var countComments: Number

    //create getter
    fun getId(): Number {
        return id
    }

    fun getCaption(): String {
        return caption
    }

    fun getImageUrl(): String {
        return imageUrl
    }

    fun getReactions(): ArrayList<Reaction> {
        return reactions
    }

    fun getCountComments(): Number {
        return countComments
    }

    //create setter
    fun setId(id: Number) {
        this.id = id
    }

    fun setCaption(caption: String) {
        this.caption = caption
    }

    fun setImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    fun setReactions(reactions: ArrayList<Reaction>) {
        this.reactions = reactions
    }

    fun setCountComments(countComments: Number) {
        this.countComments = countComments
    }
}