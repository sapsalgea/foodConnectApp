package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class LoadRoomUsers(
    @SerializedName("userIndexId")val userIndexId:Int,
    @SerializedName("userThumbnail")val userThumbnail:String,
    @SerializedName("userNickname")val userNickname:String,

)