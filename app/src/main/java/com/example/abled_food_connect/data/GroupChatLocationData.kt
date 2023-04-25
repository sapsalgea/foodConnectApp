package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

class GroupChatLocationData(
    @SerializedName("members")
    val members : ArrayList<LoadRoomUsersLocation>,
    @SerializedName("success")
    val success:Boolean,
    @SerializedName("roomInfo")
    val roomInfo: MainFragmentItemData
)