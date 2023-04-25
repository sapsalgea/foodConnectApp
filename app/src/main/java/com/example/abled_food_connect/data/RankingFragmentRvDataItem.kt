package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class RankingFragmentRvDataItem(
    @SerializedName("ranking_tb_id")
    val ranking_tb_id : Int,
    @SerializedName("season_name")
    val season_name : String,
    @SerializedName("user_tb_id")
    val user_tb_id : Int,
    @SerializedName("user_tb_nicname")
    val user_tb_nicname : String,
    @SerializedName("profile_image")
    val profile_image : String,
    @SerializedName("rank_point")
    val rank_point : String,
    @SerializedName("rank")
    val rank : String,
    @SerializedName("number")
    val number : Int,
    @SerializedName("tier")
    val tier : String,
    @SerializedName("tier_image")
    val tier_image : String,
    @SerializedName("isTopMenu")
    val isTopMenu : Int


)
