package com.example.abled_food_connect.broadcastReciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.abled_food_connect.RoomInformationActivity

class RoomInformationBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action == "RoomInfo") {
           if (context is RoomInformationActivity){
               context.loadRoomInfo()
           }
        }
    }
}