package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class UserProfileData(
    @SerializedName("success")
    var success: Boolean,
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val user_id: String,
    @SerializedName("nick_name")
    val nick_name: String,
    @SerializedName("social_login_type")
    val social_login_type: String,
    @SerializedName("profile_image")
    val profile_image: String,
    @SerializedName("thumbnail_image")
    val thumbnail_image: String,
    @SerializedName("birth_year")
    val birth_year: Int,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("phone_number")
    val phone_number: String,
    @SerializedName("introduction")
    val introduction: String,
    @SerializedName("review_count")
    val review_count: Int,
    //랭킹관련 변수
    @SerializedName("ranking_tb_id")
    val ranking_tb_id: Int,
    @SerializedName("tier")
    val tier: String,
    @SerializedName("now_season")
    val now_season: String,
    @SerializedName("rank")
    val rank: String,
    @SerializedName("tier_image")
    val tier_image: String,
    @SerializedName("rank_point")
    val rank_point: Int,
    @SerializedName("number")
    val number: Int,
    @SerializedName("account_delete")
    val account_delete: Int,
    @SerializedName("no_show_count")
    val no_show_count: Int
)
