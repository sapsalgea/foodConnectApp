package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class UserProfileEvaluationListDataItem(
    @SerializedName("record_id")
    val record_id : Int,
    @SerializedName("user_tb_id")
    val user_tb_id : Int,
    @SerializedName("delightful_type")
    val delightful_type : Int,
    @SerializedName("gourmet_type")
    val gourmet_type : Int,
    @SerializedName("funny_type")
    val funny_type : Int,
    @SerializedName("noisy_type")
    val noisy_type : Int,
    @SerializedName("curt_type")
    val curt_type : Int,
    @SerializedName("food_smart_type")
    val food_smart_type : Int,
    @SerializedName("sociability_type")
    val sociability_type : Int,
    @SerializedName("smile_type")
    val smile_type : Int,
    @SerializedName("uncomfortable_type")
    val uncomfortable_type : Int
)
