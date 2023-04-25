package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

class paginationData(
    @SerializedName("success") var success: Boolean,
    @SerializedName("ChatLogList") var ChatLogList: ArrayList<MessageData>,
    @SerializedName("members") var members:String
)