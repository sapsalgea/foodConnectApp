package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class MeetingUserEvaluationWritingData(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("get_season_point")
    val get_season_point: Int,
    @SerializedName("now_season_total_rangking_point")
    val now_season_total_rangking_point: Int
)
