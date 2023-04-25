package com.example.abled_food_connect.interfaces

import com.example.abled_food_connect.data.ReviewDetailViewRvData
import com.example.abled_food_connect.data.ReviewFragmentLoadingData
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ReviewFragRvUsingInterface {

    @Multipart
    @POST("review/review_list_get.php")
    fun review_frag_rv_using_interface(
        @Part("userId") userId: Int
    ): Call<ReviewFragmentLoadingData>


}
