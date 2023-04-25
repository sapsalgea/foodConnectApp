package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class DirectMessageNodeServerSendDataItem(
    @SerializedName("dm_log_tb_id")
    val dm_log_tb_id: Int,
    @SerializedName("room_name")
    val room_name: String,
    @SerializedName("from_user_tb_id")
    val from_user_tb_id: Int,
    @SerializedName("to_user_tb_id")
    val to_user_tb_id: Int,
    @SerializedName("content")
    val content: String,
    @SerializedName("text_or_image_or_dateline")
    val text_or_image_or_dateline: String,
    @SerializedName("send_time")
    val send_time: String,
    @SerializedName("message_check")
    val message_check: String
)
