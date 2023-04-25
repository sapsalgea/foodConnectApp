package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class NewActionAlarmCheckData(
    @SerializedName("success")
    var success : Boolean,
    @SerializedName("alarm_count")
    var alarm_count : Int
)
