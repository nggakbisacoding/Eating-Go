package com.example.eatinggo.util

import retrofit2.http.Query
import com.example.eatinggo.model.CafeResult
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/"

private val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build()

interface ApiCafe{

    @GET("json?keyword=cafe&radius=1500&type=restaurant")
    fun getAllData(@Query("location") latlng: String, @Query("key") key: String): Call<CafeResult>

}

object Api {
    val retrofitService: ApiCafe by lazy{retrofit.create(ApiCafe::class.java)}
}
