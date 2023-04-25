package com.example.abled_food_connect.retrofit

import android.graphics.Bitmap
import com.example.abled_food_connect.data.kakaoDataClass.KakaoLocalSearch
import com.example.abled_food_connect.data.naverDataClass.NaverSearchLocal
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.io.InputStream

interface MapSearch {

        @GET("v1/search/local.json")
    fun naverMapSearch(
                @Header("X-Naver-Client-Id") clientId: String,
                @Header("X-Naver-Client-Secret") clientSecret: String,
                @Query("query") query: String,
                @Query("display") display: Int? = null,
                @Query("start") start: Int? = null
            ): Call<NaverSearchLocal>

    @GET("v2/local/search/keyword.json")
    fun kakaoMapSearch(
        @Header("Authorization") header :String,
        @Query("query")query:String
    ) : Call<KakaoLocalSearch>

    @GET("map-static/v2/raster")
    @Streaming
    fun getStaticMap( @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
                      @Header("X-NCP-APIGW-API-KEY") clientSecret: String,
                      @Query("w") w: Int,
                      @Query("h") h: Int,
                      @Query("level") level: Int? = null,
                      @Query("maptype") maptype: String? = null,
                      @Query("markers",encoded = true) markers: String? = null,
                      @Query("center") center: String? = null,
                      @Query("scale") scale: Int? = null
    ):Call<ResponseBody>
    @FormUrlEncoded
    @POST("/gpstest.php")
    fun update(
        @Field("userIndex")userIndex:Int,
        @Field("x")x:String,
        @Field("y")y:String
    ): Call<String>
}