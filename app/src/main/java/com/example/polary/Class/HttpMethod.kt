package com.example.polary.Class

import android.util.Log
import com.example.polary.dataClass.ResponseBody
import com.example.polary.dataClass.User
import com.example.polary.`object`.RetrofitInstance
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.IApi
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class HttpMethod {
    var retrofitBuilder: Retrofit = RetrofitInstance.getRetrofitInstance()

    inline fun <reified T : Any> doGet(endpoint: String, callback: ApiCallBack<Any>) {
        val api = retrofitBuilder.create(IApi::class.java)
        val call = api.getData(endpoint)

        call.enqueue(object : Callback<ResponseBody<JsonElement>> {
            override fun onResponse(
                call: Call<ResponseBody<JsonElement>>,
                response: Response<ResponseBody<JsonElement>>
            ) {
                if (response.isSuccessful) {
                    val res = response.body()
                    val gson = Gson()
                    val data: Any = if (res?.data?.isJsonArray == true) {
                        gson.fromJson<List<T>>(res.data, object : TypeToken<List<T>>() {}.type) ?: emptyList<T>()
                    } else {
                        gson.fromJson<T>(res?.data, object : TypeToken<T>() {}.type) ?: Any()
                    }
                    callback.onSuccess(data)
                } else {
                    callback.onError(Throwable(response.message()))
                }
            }

            override fun onFailure(call: Call<ResponseBody<JsonElement>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    inline fun <reified T : Any> doGetWithQuery(endpoint: String, params: Map<String, String>, callback: ApiCallBack<Any>) {
        val api = retrofitBuilder.create(IApi::class.java)
        val call = api.getDataWithQuery(endpoint, params)

        call.enqueue(object : Callback<ResponseBody<JsonElement>> {
            override fun onResponse(
                call: Call<ResponseBody<JsonElement>>,
                response: Response<ResponseBody<JsonElement>>
            ) {
                if (response.isSuccessful) {
                    val res = response.body()
                    val gson = Gson()
                    val data: Any = if (res?.data?.isJsonArray == true) {
                        gson.fromJson<List<T>>(res.data, object : TypeToken<List<T>>() {}.type) ?: emptyList<T>()
                    } else {
                        gson.fromJson<T>(res?.data, object : TypeToken<T>() {}.type) ?: Any()
                    }
                    callback.onSuccess(data)
                } else {
                    callback.onError(Throwable(response.message()))
                }
            }

            override fun onFailure(call: Call<ResponseBody<JsonElement>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun doPost(url: String, requestBody: Any, callback: ApiCallBack<Any>) {
        Log.d("HttpMethod", "doPost: $requestBody")
        val api = retrofitBuilder.create(IApi::class.java)
        val call = if (requestBody is User && requestBody.password?.isEmpty() == true) {
            val modifiedRequestBody = requestBody.copy(password = null)
            api.postData(url, modifiedRequestBody)
        } else {
            api.postData(url, requestBody)
        }

        call.enqueue(object : Callback<ResponseBody<JsonElement>> {
            override fun onResponse(
                call: Call<ResponseBody<JsonElement>>,
                response: Response<ResponseBody<JsonElement>>
            ) { if (response.isSuccessful) {
                    response.body()?.data?.let { callback.onSuccess(it) }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    callback.onError(Throwable("Unsuccessful response. Status code: ${response.code()}, Error body: $errorBody"))
                }
            }

            override fun onFailure(call: Call<ResponseBody<JsonElement>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun doPostMultiPart(
        url: String,
        file: MultipartBody.Part,
        authorId: MultipartBody.Part,
        caption: MultipartBody.Part,
        frame: MultipartBody.Part,
        font: MultipartBody.Part,
        visibleToIds: List<MultipartBody.Part>,
        callback: ApiCallBack<Any>) {
        Log.d("HttpMethod", "doPostMultiPart: $authorId")
        val api = retrofitBuilder.create(IApi::class.java)
        val call = api.postDataMultipart(url, file, authorId, caption, frame, font, visibleToIds)
        call.enqueue(object : Callback<ResponseBody<JsonElement>> {
            override fun onResponse(
                call: Call<ResponseBody<JsonElement>>,
                response: Response<ResponseBody<JsonElement>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { callback.onSuccess(it) }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    callback.onError(Throwable("Unsuccessful response. Status code: ${response.code()}, Error body: $errorBody"))
                }
            }

            override fun onFailure(call: Call<ResponseBody<JsonElement>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun doPutMultipart(
        url: String,
        file: MultipartBody.Part,
        callback: ApiCallBack<Any>) {
        val api = retrofitBuilder.create(IApi::class.java)
        val call = api.putDataMultipart(url, file)
        call.enqueue(object : Callback<ResponseBody<JsonElement>> {
            override fun onResponse(
                call: Call<ResponseBody<JsonElement>>,
                response: Response<ResponseBody<JsonElement>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { callback.onSuccess(it) }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    callback.onError(Throwable("Unsuccessful response. Status code: ${response.code()}, Error body: $errorBody"))
                }
            }

            override fun onFailure(call: Call<ResponseBody<JsonElement>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun doPatch(url: String, requestBody: Any, callback: ApiCallBack<Any>) {
        Log.d("HttpMethod", "doPatch: $requestBody")
        val api = retrofitBuilder.create(IApi::class.java)
        val call = api.patchData(url, requestBody)

        call.enqueue(object : Callback<ResponseBody<JsonElement>> {
            override fun onResponse(
                call: Call<ResponseBody<JsonElement>>,
                response: Response<ResponseBody<JsonElement>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { callback.onSuccess(it) }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    callback.onError(Throwable("Unsuccessful response. Status code: ${response.code()}, Error body: $errorBody"))
                }
            }

            override fun onFailure(call: Call<ResponseBody<JsonElement>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun doPut(url: String, requestBody: Any, callback: ApiCallBack<Any>) {
        val api = retrofitBuilder.create(IApi::class.java)
        val call = api.putData(url, requestBody)

        call.enqueue(object : Callback<ResponseBody<JsonElement>> {
            override fun onResponse(
                call: Call<ResponseBody<JsonElement>>,
                response: Response<ResponseBody<JsonElement>>
            ) { if (response.isSuccessful) {
                    response.body()?.data?.let { callback.onSuccess(it) }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    callback.onError(Throwable("Unsuccessful response. Status code: ${response.code()}, Error body: $errorBody"))
                }
            }

            override fun onFailure(call: Call<ResponseBody<JsonElement>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }
    fun doDelete(url: String, params: Map<String, String>, callback: ApiCallBack<Any>) {
        val api = retrofitBuilder.create(IApi::class.java)
        val call = api.deleteData(url, params)

        call.enqueue(object : Callback<ResponseBody<JsonElement>> {
            override fun onResponse(
                call: Call<ResponseBody<JsonElement>>,
                response: Response<ResponseBody<JsonElement>>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.data != null) {
                        callback.onSuccess(response.body()?.data!!)
                    } else {
                        callback.onSuccess("Deleted")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    callback.onError(Throwable("Unsuccessful response. Status code: ${response.code()}, Error body: $errorBody"))
                }
            }

            override fun onFailure(call: Call<ResponseBody<JsonElement>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }
}