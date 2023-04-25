package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class RoomTbDbInfoData(
    @SerializedName("success")
    var success : String,
    @SerializedName("roomInfoList")
    var roomInfoList : List<RoomTbDbInfoDataItem>
)
