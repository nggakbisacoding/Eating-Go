package com.example.eatinggo.util

import com.example.eatinggo.model.Result
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("json?sensor=true&language=id")
    fun getLocation(@Query("latlng") latlng: String, @Query("key") key: String): Call<Result>
}