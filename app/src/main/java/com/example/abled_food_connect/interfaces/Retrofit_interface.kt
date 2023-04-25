package com.example.abled_food_connect.interfaces

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface retrofit_interface {


    // 프로필 이미지 보내기
    @Multipart
    @POST("user_info/user_register.php")
    fun post_Porfile_Request(
        @Part("user_id") user_id: String,
        @Part("social_login_type") social_login_type: String,
        @Part("nick_name") nick_name: String,
        @Part("birth_year") birth_year: String,
        @Part("user_gender") user_gender: String,
        @Part("phone_number") phone_number: String,
        @Part imageFile : MultipartBody.Part,
        @Part thumbnail_imageFile : MultipartBody.Part
    ): Call<String>

//    //string값 보내는 예제입니다.
//    // 프로필 닉 상태메세지 보내기
//    @FormUrlEncoded
//    @POST("upload_test.php/")
//    fun post_setNick_Or_StatusMassage(
//        @Field("mode") mode : String,
//        @Field("userId") userId : String,
//        @Field("data") data: String ) : Call<String>

}