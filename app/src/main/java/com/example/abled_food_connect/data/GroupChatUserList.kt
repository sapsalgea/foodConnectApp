package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class GroupChatUserList(@SerializedName("members") val members: String , @SerializedName("list")val list :ArrayList<LoadRoomUsers>)