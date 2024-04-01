package com.example.polary.dataClass

data class ResponseBody<T>(
    val success: Boolean,
    val message: String,
    val data: T
)
