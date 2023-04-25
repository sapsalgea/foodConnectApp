package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class LoadingRoom(
    @SerializedName("success")
    var success :Boolean
    ,
    @SerializedName("work")
    var work:String,
    @SerializedName("id")
    var id:String,
    @SerializedName("roomList")
    var roomList:ArrayList<MainFragmentItemData>


)