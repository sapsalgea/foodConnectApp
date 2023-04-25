package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ChattingFragmentDmRvData(
    @SerializedName("success")
    var success : Boolean,
    @SerializedName("chattingList")
    var chattingList : List<ChattingFragmentDmRvDataItem>
)
