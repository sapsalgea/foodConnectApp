package com.example.abled_food_connect.data


import com.google.gson.annotations.SerializedName

data class JoinRoomCheck(
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("hostName")
    val hostName: String,
    @SerializedName("isRoom")
    val isRoom:Boolean,
)