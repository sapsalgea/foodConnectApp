package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class userProfileRankingLatestThreeData(
    @SerializedName("success")
    var success : String,
    @SerializedName("RankingLatestThreeList")
    var RankingLatestThreeList : List<userProfileRankingLatestThreeDataItem>
)
