package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ReviewWritingData(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("review_id")
    val review_id: Int,
    @SerializedName("get_season_point")
    val get_season_point: Int,
    @SerializedName("now_season_total_rangking_point")
    val now_season_total_rangking_point: Int
)
