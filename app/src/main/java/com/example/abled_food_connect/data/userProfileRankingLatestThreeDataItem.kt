package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class userProfileRankingLatestThreeDataItem(
    @SerializedName("before_season_tier_tb_id")
    val before_season_tier_tb_id : String,
    @SerializedName("season_name")
    val season_name : Int,
    @SerializedName("user_tb_id")
    val user_tb_id : String,
    @SerializedName("user_nicname")
    val user_nicname : String,
    @SerializedName("season_rank")
    val season_rank : String,
    @SerializedName("season_count_number")
    val season_count_number : String,
    @SerializedName("ranking_point")
    val ranking_point : String,
    @SerializedName("user_tier")
    val user_tier : String,
    @SerializedName("tier_image")
    val tier_image : String
)
