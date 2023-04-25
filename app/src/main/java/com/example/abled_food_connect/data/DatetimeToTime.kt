package com.example.abled_food_connect.data

import java.text.SimpleDateFormat

interface DatetimeToTime {

     fun isDatetimeToTime(data: ChatItem):String{
        val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
        val convertedCurrentDate = sdf.parse(data.sendTime)
        val date = SimpleDateFormat("a HH:mm").format(convertedCurrentDate)
        return date
    }
}