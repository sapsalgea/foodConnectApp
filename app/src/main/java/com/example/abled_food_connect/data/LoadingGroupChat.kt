package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

class LoadingGroupChat(
    @SerializedName("success")
    var success :Boolean,
    @SerializedName("roomList")
    var roomList:ArrayList<GroupChatListData>
)