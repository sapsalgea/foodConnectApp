package com.example.abled_food_connect.data

data class ChatItem(val name:String, val ThumbnailImage:String, val content:String, val sendTime:String, val viewType:Int, var readMember:String, var messageStatus:Int?=0) {
}