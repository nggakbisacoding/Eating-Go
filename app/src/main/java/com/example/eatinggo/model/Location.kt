package com.example.eatinggo.model

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("formatted_address")
    val location: String
)
