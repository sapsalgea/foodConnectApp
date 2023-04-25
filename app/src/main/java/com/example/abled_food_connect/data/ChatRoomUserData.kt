package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

class ChatRoomUserData(
    @SerializedName("subscriptionId") var subscriptionId: String?=null,
    @SerializedName("userIndexId") var userIndexId: String,
    @SerializedName("nickName") var nickName: String,
    @SerializedName("profileImage") var profileImage: String,
    @SerializedName("thumbnailImage") var thumbnailImage: String,
    @SerializedName("status") var status:Int? = null,
    @SerializedName("roomId") var roomId:String

)