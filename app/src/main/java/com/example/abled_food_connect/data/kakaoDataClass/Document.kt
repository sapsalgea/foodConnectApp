package com.example.abled_food_connect.data.kakaoDataClass


import com.google.gson.annotations.SerializedName

data class Document(
    @SerializedName("address_name")
    var addressName: String,
    @SerializedName("category_group_code")
    var categoryGroupCode: String,
    @SerializedName("category_group_name")
    var categoryGroupName: String,
    @SerializedName("category_name")
    var categoryName: String,
    @SerializedName("distance")
    var distance: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("phone")
    var phone: String,
    @SerializedName("place_name")
    var placeName: String,
    @SerializedName("place_url")
    var placeUrl: String,
    @SerializedName("road_address_name")
    var roadAddressName: String,
    @SerializedName("x")
    var x: String,
    @SerializedName("y")
    var y: String
) {
    fun Document(
        addressName: String,
        categoryGroupCode: String,
        categoryGroupName: String,
        categoryName: String,
        distance: String,
        id: String,
        phone: String,
        placeName: String,
        placeUrl: String,
        roadAddressName: String,
        x: String,
        y: String
    ) {
        this.addressName = addressName
        this.categoryGroupCode = categoryGroupCode
        this.categoryGroupName = categoryGroupName
        this.categoryName = categoryName
        this.distance = distance
        this.id = id
        this.phone = phone
        this.placeName = placeName
        this.placeUrl = placeUrl
        this.roadAddressName = roadAddressName
        this.x = x
        this.y = y

    }


}