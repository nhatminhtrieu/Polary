package com.example.polary.`object`

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val SimulatorURL = "10.0.2.2"
    private const val PandaURL = "192.168.1.104"
    private const val BASE_URL = "http://$PandaURL:3000/"

    fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}