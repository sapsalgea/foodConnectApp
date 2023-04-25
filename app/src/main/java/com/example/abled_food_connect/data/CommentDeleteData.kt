package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class CommentDeleteData(
    @SerializedName("success")
    var success : String,
    @SerializedName("review_deleted")
    var review_deleted : Int
)
