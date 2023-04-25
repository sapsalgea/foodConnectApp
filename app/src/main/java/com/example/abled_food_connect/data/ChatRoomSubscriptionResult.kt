package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

class ChatRoomSubscriptionResult(
    @SerializedName("success")var success : Boolean,
    @SerializedName("userList")var userList:ArrayList<ChatRoomUserData>
)