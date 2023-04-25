package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class RoomTbDbInfoDataItem(
    @SerializedName("id")
    val room_id : Int,
    @SerializedName("room_title")
    val room_title : String,
    @SerializedName("room_introduce")
    val room_introduce : String,
    @SerializedName("now_member_count")
    val now_member_count : Int,
    @SerializedName("member_count")
    val member_count : Int,
    @SerializedName("join_users")
    val join_users : String,
    @SerializedName("restaurant_address")
    val restaurant_address : String,
    @SerializedName("restaurant_roadaddress")
    val restaurant_roadaddress : String,
    @SerializedName("restaurant_placename")
    val restaurant_placename : String,
    @SerializedName("restaurant_name")
    val restaurant_name : String,
    @SerializedName("gender_selection")
    val gender_selection : String,
    @SerializedName("reporting_date")
    val reporting_date : String,
    @SerializedName("minimum_age")
    val minimum_age : Int,
    @SerializedName("maximum_age")
    val maximum_age : Int,
    @SerializedName("appointment_day")
    val appointment_day : String,
    @SerializedName("appointment_time")
    val appointment_time : String,
    @SerializedName("name_host")
    val name_host : String,
    @SerializedName("room_status")
    val room_status : Double,
    @SerializedName("search_keyword")
    val search_keyword : String,
    @SerializedName("map_x")
    val map_x : String,
    @SerializedName("map_y")
    val map_y : String
)
