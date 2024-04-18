package com.example.polary.utils

import com.example.polary.dataClass.ResponseBody
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface IApi {
    @GET("{endpoint}")
    fun getData(@Path("endpoint", encoded = true) endpoint: String): Call<ResponseBody<JsonElement>>

    @GET("{endpoint}")
    fun getDataWithParams(@Path("endpoint", encoded = true) endpoint: String, @QueryMap params: Map<String, String>): Call<ResponseBody<JsonElement>>

    @POST("{endpoint}")
    fun postData(@Path("endpoint", encoded = true) endpoint: String, @Body body: Any): Call<ResponseBody<JsonElement>>

    @Multipart
    @POST("{endpoint}")
    fun postDataMultipart(
        @Path("endpoint", encoded = true) endpoint: String,
        @Part file: MultipartBody.Part,
        @Part authorId: MultipartBody.Part,
        @Part caption: MultipartBody.Part,
        @Part visibleToIds: List<MultipartBody.Part>
    ): Call<ResponseBody<JsonElement>>

    @Multipart
    @PUT("{endpoint}")
    fun putDataMultipart(@Path("endpoint", encoded = true) endpoint: String, @Part file: MultipartBody.Part): Call<ResponseBody<JsonElement>>

    @DELETE("{endpoint}")
    fun deleteData(@Path("endpoint", encoded = true) endpoint: String, @QueryMap queryMap: Map<String, String>): Call<ResponseBody<JsonElement>>
}