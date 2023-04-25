package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class MeetingUserEvaluationWritingBeforeNoShowCheckData(
    @SerializedName("success")
    var success : Boolean,
    @SerializedName("isNoShow")
    var isNoShow : Int
)
