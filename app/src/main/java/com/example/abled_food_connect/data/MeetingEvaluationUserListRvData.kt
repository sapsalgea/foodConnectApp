package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class MeetingEvaluationUserListRvData(

    @SerializedName("success")
    var success : Boolean,
    @SerializedName("userList")
    var userList : List<MeetingEvaluationUserListRvDataItem>
)
