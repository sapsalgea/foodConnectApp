package com.example.abled_food_connect.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Message{
    @SerializedName("result")
    @Expose
    private val result: Int? = null

    @SerializedName("imageUri")
    @Expose
    private val imageUri: String? = null
}