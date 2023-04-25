package com.example.abled_food_connect.interfaces

import com.example.abled_food_connect.data.LoginDataClass
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CheckingRegisteredUser {

        @FormUrlEncoded
        @POST("user_info/checking_registered_user.php")
        fun post_checking_register_user(
            @Field("userId") userId : String ) : Call<LoginDataClass>

}