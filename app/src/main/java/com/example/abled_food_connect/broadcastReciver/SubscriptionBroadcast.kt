package com.example.abled_food_connect.broadcastReciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.abled_food_connect.JoinRoomSubscriptionActivity

class SubscriptionBroadcast:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {

        if (intent.action == "subscription") {
            if (context is JoinRoomSubscriptionActivity){
                val roomId = intent.getStringExtra("roomId")!!
                context.hostSubscriptionCheck(roomId)
            }
        }
    }


}