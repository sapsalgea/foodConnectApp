package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class MainFragmentItemData(
    @SerializedName("roomId")
    var roomId: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("info")
    var info: String? = null,
    @SerializedName("nowNumOfPeople")
    var nowNumOfPeople: String? = null,
    @SerializedName("numOfPeople")
    var numOfPeople: String? = null,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("time")
    var time: String? = null,
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("roadAddress")
    var roadAddress: String? = null,
    @SerializedName("shopName")
    var shopName: String? = null,
    @SerializedName("placeName")
    var placeName: String? = null,
    @SerializedName("keyWords")
    var keyWords: String? = null,
    @SerializedName("gender")
    var gender: String? = null,
    @SerializedName("minimumAge")
    var minimumAge: Int? = null,
    @SerializedName("maximumAge")
    var maximumAge: Int? = null,
    @SerializedName("hostName")
    var hostName: String? = null,
    @SerializedName("hostIndex")
    var hostIndex: String? = null,
    @SerializedName("roomStatus")
    var roomStatus: Double? = null,
    @SerializedName("map_x")
    var mapX:Double? = null,
    @SerializedName("map_y")
    var mapY:Double? = null,
    @SerializedName("joinMember")
    var joinMember:ArrayList<String>? = null,
    @SerializedName("finish")
    var finish:String? = null

) {
    private fun CreateRoom() {

    }
}