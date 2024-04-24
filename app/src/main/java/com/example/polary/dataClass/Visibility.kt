package com.example.polary.dataClass

import java.io.Serializable

data class Visibility(
    val id: Int,
    val displayName: String,
    val avatar: String?
) : Serializable
