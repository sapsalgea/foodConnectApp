package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class MeetingEvaluationUserListRvDataItem(
    @SerializedName("room_id")
    val room_id : Int,
    @SerializedName("meeting_result")
    val meeting_result : Int,
    @SerializedName("review_result")
    val review_result : Int,
    @SerializedName("user_evaluation")
    val user_evaluation : Int,
    @SerializedName("user_nickname")
    val user_nickname : String,
    @SerializedName("id")
    val user_tb_id : Int,
    @SerializedName("profile_image")
    val profile_image : String,
    @SerializedName("thumbnail_image")
    val thumbnail_image : String,
    @SerializedName("ishost")
    val is_host : Boolean,
    @SerializedName("is_account_delete")
    val is_account_delete: Int,
    @SerializedName("user_evaluation_what_did_you_say")
    var user_evaluation_what_did_you_say : String
)
