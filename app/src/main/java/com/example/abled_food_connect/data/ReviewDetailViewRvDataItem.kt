package com.example.abled_food_connect.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReviewDetailViewRvDataItem(
    @SerializedName("review_id")
    val review_id : Int,
    @SerializedName("room_tb_id")
    val room_tb_id : Int,
    @SerializedName("writer_user_tb_id")
    val writer_user_tb_id : Int,
    @SerializedName("writer_uid")
    val writer_uid : String,
    @SerializedName("writer_nicname")
    val writer_nicname : String,
    @SerializedName("restaurant_address")
    val restaurant_address : String,
    @SerializedName("restaurant_name")
    val restaurant_name : String,
    @SerializedName("reporting_date")
    val reporting_date : String,
    @SerializedName("appointment_day")
    val appointment_day : String,
    @SerializedName("appointment_time")
    val appointment_time : String,
    @SerializedName("review_description")
    val review_description : String,
    @SerializedName("rating_star_taste")
    val rating_star_taste : Int,
    @SerializedName("rating_star_service")
    val rating_star_service : Int,
    @SerializedName("rating_star_clean")
    val rating_star_clean : Int,
    @SerializedName("rating_star_interior")
    val rating_star_interior : Int,
    @SerializedName("review_picture_0")
    val review_picture_0 : String,
    @SerializedName("review_picture_1")
    val review_picture_1 : String,
    @SerializedName("review_picture_2")
    val review_picture_2 : String,
    @SerializedName("like_count")
    var like_count : String,
    @SerializedName("heart_making")
    var heart_making : Boolean,
    @SerializedName("comment_count")
    var comment_count : String,
    @SerializedName("nick_name")
    val nick_name : String,
    @SerializedName("profile_image")
    val profile_image : String
): Parcelable
