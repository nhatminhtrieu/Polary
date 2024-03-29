package com.example.polary.ultils
interface ApiCallBack<T> {
    fun onSuccess(data: T)
    fun onError(error: Throwable)
}