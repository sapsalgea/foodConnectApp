package com.example.abled_food_connect.data.kakaoDataClass


import com.google.gson.annotations.SerializedName

data class KakaoLocalSearch(
    @SerializedName("documents")
    val documents: ArrayList<Document>,
    @SerializedName("meta")
    val meta: Meta
)