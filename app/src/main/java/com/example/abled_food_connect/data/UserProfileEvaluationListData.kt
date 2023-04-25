package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class UserProfileEvaluationListData(
    @SerializedName("success")
    var success : Boolean,
    @SerializedName("evaluationList")
    var evaluationList : List<UserProfileEvaluationListDataItem>
)
