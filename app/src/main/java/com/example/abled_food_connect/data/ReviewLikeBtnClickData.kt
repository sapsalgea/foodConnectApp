package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ReviewLikeBtnClickData(
    @SerializedName("heart_making")
    val heart_making: Boolean,
    @SerializedName("how_many_like_count")
    val how_many_like_count: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("review_deleted")
    val review_deleted: Int,
    @SerializedName("isDoubleLikeButtonClicked")
    val isDoubleLikeButtonClicked: Boolean
)