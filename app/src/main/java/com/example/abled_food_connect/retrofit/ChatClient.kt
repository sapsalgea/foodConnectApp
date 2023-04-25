package com.example.abled_food_connect.retrofit

import android.content.res.Resources
import com.example.abled_food_connect.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatClient {

    companion object {
        private var instance: ChatClient? = null
        lateinit var roomAPI: RoomAPI
        var baseUrl: String = "ServerIP"


        fun getInstance(): ChatClient {
            if (instance == null) {
                instance = ChatClient()
            }
            return instance as ChatClient
        }

        fun getApiService(): RoomAPI {
            return roomAPI
        }

    }
    fun ChatClient() {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        roomAPI = retrofit.create(RoomAPI::class.java)
    }


}