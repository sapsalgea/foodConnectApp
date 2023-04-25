package com.example.abled_food_connect.interfaces

import com.example.abled_food_connect.data.ReviewDetailViewRvData
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ReviewDetailRvInterface {

    @Multipart
    @POST("review/review_detail_rv_list_get.php")
    fun review_Detail_rv_using_interface(
        @Part("review_id") review_id: String,
        @Part("user_tb_id") user_tb_id: Int
    ): Call<ReviewDetailViewRvData>

}