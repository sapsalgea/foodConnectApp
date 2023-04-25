package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class UserProfileBadgeListDataItem(
    @SerializedName("badge_name")
    val badge_name : String,
    @SerializedName("badge_achieve_check")
    val badge_achieve_check : Int,
    @SerializedName("badge_achieve_goal_image")
    val badge_achieve_goal_image : String,
    @SerializedName("badge_fail_goal_image")
    val badge_fail_goal_image : String,
    @SerializedName("how_to_achieve_goal")
    val how_to_achieve_goal : String,
    @SerializedName("congratulations_message")
    val congratulations_message : String
)
