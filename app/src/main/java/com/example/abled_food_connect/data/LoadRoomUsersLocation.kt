package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

class LoadRoomUsersLocation(
    @SerializedName("userIndexId")val userIndexId:Int,
    @SerializedName("userThumbnail")val userThumbnail:String,
    @SerializedName("userNickname")val userNickname:String,
    @SerializedName("x")val x:Double,
    @SerializedName("y")val y:Double,

)