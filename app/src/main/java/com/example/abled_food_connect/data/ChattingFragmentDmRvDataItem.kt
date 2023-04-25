package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName
import java.sql.Time

data class ChattingFragmentDmRvDataItem(
    @SerializedName("room_name")
    val room_name: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("text_or_image_or_dateline")
    val text_or_image_or_dateline: String,
    @SerializedName("send_time")
    val send_time: String,
    @SerializedName("your_table_id")
    val your_table_id: Int,
    @SerializedName("your_nick_name")
    val your_nick_name: String,
    @SerializedName("your_thumbnail_image")
    val your_thumbnail_image: String,
    @SerializedName("not_read_message_count_row")
    val not_read_message_count_row: Int,
    @SerializedName("now_server_time")
    val now_server_time: String,
    @SerializedName("is_account_delete")
    val is_account_delete: Int,
)
