package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class DirectMessageRvData(
    @SerializedName("dm_log_tb_id")
    var dm_log_tb_id: Int,
    @SerializedName("roomName")
    var roomName: String,
    @SerializedName("user_tb_id")
    var user_tb_id: Int,
    @SerializedName("userNicName")
    var userNicName: String,
    @SerializedName("userProfileImage")
    var userProfileImage: String,
    @SerializedName("message")
    var message: String,
    @SerializedName("TextOrImageOrDateLine")
    var TextOrImageOrDateLine: String,
    @SerializedName("sendTime")
    var sendTime: String,
    @SerializedName("toShowTimeStr")
    var toShowTimeStr: String,
    @SerializedName("message_check")
    var message_check: String

)
