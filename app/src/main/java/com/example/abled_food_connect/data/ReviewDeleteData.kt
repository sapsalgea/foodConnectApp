package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ReviewDeleteData(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("minus_season_point")
    val minus_season_point: Int,
    @SerializedName("now_season_total_rangking_point")
    val now_season_total_rangking_point: Int
)
