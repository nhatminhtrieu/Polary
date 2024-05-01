package com.example.polary.dataClass

data class Group(
    val id: Int,
    val name: String,
    val memberCount: Int?,
    val members: List<Friend>?
)
