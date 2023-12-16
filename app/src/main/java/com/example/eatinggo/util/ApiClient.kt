package com.example.eatinggo.util

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    fun getInstance(): ApiService {
        val mOkHttpClient = OkHttpClient
            .Builder()
            .build()
        val builder = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(mOkHttpClient)
            .build()
        return builder.create(ApiService::class.java)
    }
}