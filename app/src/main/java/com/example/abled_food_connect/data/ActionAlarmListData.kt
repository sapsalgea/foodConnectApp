package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ActionAlarmListData(
    @SerializedName("success")
    var success : Boolean,
    @SerializedName("ActionAlarmList")
    var ActionAlarmList : List<ActionAlarmListDataItem>
)
