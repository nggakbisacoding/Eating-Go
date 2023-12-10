package com.example.eatinggo.model

import com.google.gson.annotations.SerializedName

data class CafeResult(
    @SerializedName("results")
    val data: List<CafeDisplay>
)
data class CafeDisplay(
    @SerializedName("name")
    val name: String?,
    @SerializedName("vicinity")
    val lokasi: String?,
    @SerializedName("price_level")
    val seat: Int? = 0,
    @SerializedName("types")
    val fasilitas: List<String>,
    @SerializedName("icon")
    val image: String?
)

/*
data class CafeImage(
    @SerializedName("height")
    val height: Int,
    @SerializedName("width")
    val width: Int,
    @SerializedName("photo_reference")
    val references: String?
)
*/