package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

class NodeSendData(
    @SerializedName("type") val type: String,
    @SerializedName("from") val from: String,
    @SerializedName("to") val to: String,
    @SerializedName("content") val content: String,
    @SerializedName("thumbnailImage") val thumbnailImage: String,
    @SerializedName("sendTime") val sendTime: String? = null

) {

}