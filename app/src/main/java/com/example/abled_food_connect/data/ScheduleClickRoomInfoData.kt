package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ScheduleClickRoomInfoData(
    @SerializedName("success")
    var success :Boolean,
    @SerializedName("work")
    var work:String,
    @SerializedName("hostImage")
    var hostImage:String,
    @SerializedName("roomList")
    var roomList:ArrayList<MainFragmentItemData>
)
