package com.example.polary.utils

import com.example.polary.dataClass.ResponseBody
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IApi {
    @GET("{endpoint}")
    fun getData(@Path("endpoint", encoded = true) endpoint: String): Call<ResponseBody<JsonElement>>
    @POST("{endpoint}")
    fun postData(@Path("endpoint", encoded = true) endpoint: String, @Body body: Any): Call<ResponseBody<JsonElement>>
}