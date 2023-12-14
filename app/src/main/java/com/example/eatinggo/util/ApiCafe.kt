package com.example.eatinggo.util

import com.example.eatinggo.model.CafeResult
import com.example.eatinggo.model.DetailsCafe
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/"
private const val PLACE_URL = "https://maps.googleapis.com/maps/api/place/details/"

private val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build()
private val alt = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(PLACE_URL).build()

interface ApiCafe{

    @GET("json?keyword=cafe&radius=1500&type=cafe")
    fun getAllData(@Query("location") latlng: String, @Query("key") key: String): Call<CafeResult>

    @GET("json?radius=1000&type=cafe")
    fun getnearby(@Query("keyword") keyword: String, @Query("location") latlng: String, @Query("key") key: String): Call<CafeResult>

    @GET("json?language=id&region=id")
    fun placeDetails(@Query("fields") field: String, @Query("place_id") placesId: String, @Query("key") key: String): Call<DetailsCafe>
}

object Api {
    val retrofitService: ApiCafe by lazy{retrofit.create(ApiCafe::class.java)}
    val placeService: ApiCafe by lazy{alt.create(ApiCafe::class.java)}
}
