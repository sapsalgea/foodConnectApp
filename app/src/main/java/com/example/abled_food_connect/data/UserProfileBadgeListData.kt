package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class UserProfileBadgeListData(
    @SerializedName("success")
    var success : Boolean,
    @SerializedName("badgeList")
    var badgeList : List<UserProfileBadgeListDataItem>
)
