package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class AccountSocketData(@SerializedName("roomId")val roomId:String,@SerializedName("members")val members:String,@SerializedName("hostName")val hostName:String)