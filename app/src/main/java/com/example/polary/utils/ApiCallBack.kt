package com.example.polary.utils
interface ApiCallBack<T> {
    fun onSuccess(data: T)
    fun onError(error: Throwable)
}