package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class MyPageUserScheduleData(
    @SerializedName("success")
    var success : Boolean,
    @SerializedName("scheduleList")
    var scheduleList : List<MyPageUserScheduleDataItem>
)
