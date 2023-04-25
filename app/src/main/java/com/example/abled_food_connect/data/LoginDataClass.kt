package com.example.abled_food_connect.data


import com.google.gson.annotations.SerializedName

data class LoginDataClass(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("id")
    val id: Int,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("userNickname")
    val userNickname: String,
    @SerializedName("userThumbnailImage")
    val userThumbnailImage: String,
    @SerializedName("userGender")
    val userGender: String,
    @SerializedName("userAge")
    val userAge: Int,
    @SerializedName("ranking_explanation_check")
    val ranking_explanation_check: Int



)