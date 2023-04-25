package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ChatImageSendingData(
    @SerializedName("success")
    var success: Boolean,
    @SerializedName("ImageName")
    val ImageName: String
)
