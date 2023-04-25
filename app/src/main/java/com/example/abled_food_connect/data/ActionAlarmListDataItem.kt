package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ActionAlarmListDataItem(
    @SerializedName("action_alarm_tb_id")
    var action_alarm_tb_id : Int,
    @SerializedName("receiver_user_tb_id")
    var receiver_user_tb_id : Int,
    @SerializedName("action_type")
    var action_type : String,
    @SerializedName("sender_user_tb_id")
    var sender_user_tb_id : Int,
    @SerializedName("sender_user_tb_nicname")
    var sender_user_tb_nicname : String,
    @SerializedName("which_text_choose")
    var which_text_choose : String,
    @SerializedName("sender_comment_content")
    var sender_comment_content : String,
    @SerializedName("review_id")
    var review_id : Int,
    @SerializedName("groupNum")
    var groupNum : Int,
    @SerializedName("comment_writing_user_id")
    var comment_writing_user_id : Int,
    @SerializedName("comment_writing_user_nicname")
    var comment_writing_user_nicname : String,
    @SerializedName("reviewWritingUserId")
    var reviewWritingUserId : Int,
    @SerializedName("action_datetime")
    var action_datetime : String,
    @SerializedName("time_ago")
    var time_ago : String
)
