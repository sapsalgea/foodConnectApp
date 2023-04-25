package com.example.abled_food_connect.data.naverDataClass


import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("address")
    val address: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("mapx")
    val mapx: String,
    @SerializedName("mapy")
    val mapy: String,
    @SerializedName("roadAddress")
    val roadAddress: String,
    @SerializedName("telephone")
    val telephone: String,
    @SerializedName("title")
    val title: String
)