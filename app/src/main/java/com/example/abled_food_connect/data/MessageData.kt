package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

class MessageData(
    @SerializedName("type") val type: String,
    @SerializedName("from") val from: String,
    @SerializedName("to") val to: String,
    @SerializedName("content") val content: String,
    @SerializedName("thumbnailImage") val thumbnailImage: String,
    @SerializedName("sendTime") val sendTime: String? = null,
    @SerializedName("members") val members:String,
    @SerializedName("userIndex") val userIndex:Int,
    @SerializedName("hostName")val hostName:String,

) {

}