package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ReviewChildPageCommentGetDataItem(
    @SerializedName("nick_name")
    val nick_name : String,
    @SerializedName("profile_image")
    val profile_image : String,
    @SerializedName("comment_id")
    val comment_id : Int,
    @SerializedName("review_id")
    val review_id : Int,
    @SerializedName("writing_user_id")
    val writing_user_id : Int,
    @SerializedName("comment_content")
    val comment_content : String,
    @SerializedName("comment_class")
    val comment_class : Int,
    @SerializedName("groupNum")
    val groupNum : Int,
    @SerializedName("comment_order")
    val comment_order : Int,
    @SerializedName("comment_Writing_DateTime")
    val comment_Writing_DateTime : String,
    @SerializedName("sendTargetUserTable_id")
    val sendTargetUserTable_id : Int,
    @SerializedName("sendTargetUserNicName")
    val sendTargetUserNicName : String,
    @SerializedName("deleteCheck")
    val deleteCheck : Int


)
