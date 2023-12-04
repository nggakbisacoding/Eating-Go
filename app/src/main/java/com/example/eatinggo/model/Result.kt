package com.example.eatinggo.model

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("results")
    val data: List<Location>
)
