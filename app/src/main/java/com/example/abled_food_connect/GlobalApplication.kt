package com.example.abled_food_connect

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, getString(R.string.kakao_login_api_key))


    }
}