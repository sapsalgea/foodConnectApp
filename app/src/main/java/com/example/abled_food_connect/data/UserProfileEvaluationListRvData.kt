package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

class UserProfileEvaluationListRvData (
    @SerializedName("type_name")
    val type_name : String,
    @SerializedName("type_count")
    val type_count : Int
)

