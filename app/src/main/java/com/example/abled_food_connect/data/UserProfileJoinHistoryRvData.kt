package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class UserProfileJoinHistoryRvData(
    @SerializedName("success")
    var success : Boolean,
    @SerializedName("host_count")
    var host_count : Int,
    @SerializedName("guest_count")
    var guest_count : Int,
    @SerializedName("scheduleList")
    var scheduleList : List<UserProfileJoinHistoryRvDataItem>
)
