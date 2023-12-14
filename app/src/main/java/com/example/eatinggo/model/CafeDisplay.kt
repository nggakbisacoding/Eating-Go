package com.example.eatinggo.model

import com.google.gson.annotations.SerializedName

data class CafeResult(
    @SerializedName("results")
    val data: List<CafeDisplay>? = null
)
data class DetailsCafe(
    @SerializedName("result")
    val data: CafeInfo
)
data class CafeInfo(
    @SerializedName("current_opening_hours")
    val time: CafeTime,
    @SerializedName("formatted_phone_number")
    val phone: String,
    @SerializedName("user_ratings_total")
    val ratings: Int,
    @SerializedName("wheelchair_accessible_entrance")
    val cacat: Boolean
)

data class CafeTime(
    @SerializedName("open_now")
    val openNow: Boolean,
    @SerializedName("weekday_text")
    val weekday: List<String>
)
data class CafeDisplay(
    @SerializedName("name")
    val name: String?,
    @SerializedName("vicinity")
    val lokasi: String?,
    @SerializedName("price_level")
    val seat: Int? = 0,
    @SerializedName("geometry")
    val geo: CafeLocation,
    @SerializedName("types")
    val fasilitas: List<String>,
    @SerializedName("place_id")
    val placesId: String,
    @SerializedName("photos")
    val image: List<CafeImage>? = null
)

data class CafeLocation(
    @SerializedName("location")
    val latlng: LatLng
)

data class LatLng(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)

data class CafeImage(
    @SerializedName("height")
    val height: Int,
    @SerializedName("width")
    val width: Int,
    @SerializedName("photo_reference")
    val references: String?
)
