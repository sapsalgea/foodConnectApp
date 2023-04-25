package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ReviewDetailViewRvData(
    @SerializedName("success")
    var success : String,
    @SerializedName("roomList")
    var roomList : List<ReviewDetailViewRvDataItem>
)
