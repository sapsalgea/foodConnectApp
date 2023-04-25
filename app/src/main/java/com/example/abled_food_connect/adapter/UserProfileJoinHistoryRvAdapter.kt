package com.example.abled_food_connect.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.MeetingUserEvaluationActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.UserProfileJoinHistoryRvDataItem
import java.util.*
import kotlin.collections.ArrayList

class UserProfileJoinHistoryRvAdapter (val ScheduleDataList: ArrayList<UserProfileJoinHistoryRvDataItem>,val UserNicName : String) : RecyclerView.Adapter<UserProfileJoinHistoryRvAdapter.CustromViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_profile_join_history_rv_item ,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return ScheduleDataList.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {



        if(ScheduleDataList.get(position).name_host == UserNicName){
            holder.hostOrGuestIv.setImageResource(R.drawable.ic_bell_ring_alarm_green)
            holder.hostorGusetTv.setText("호스트")
        }else{
            holder.hostOrGuestIv.setImageResource(R.drawable.ic_multiple_users_silhouette_orange)
            holder.hostorGusetTv.setText("게스트")
        }




        var daySplit = ScheduleDataList.get(position).appointment_day .split("-")


        val cal = Calendar.getInstance()
        cal.set(daySplit[0].toInt(),daySplit[1].toInt()-1,daySplit[2].toInt())




        var dayOfWeekStr :String = "어떤요일"

        when (cal.get(Calendar.DAY_OF_WEEK)) {
            1 -> dayOfWeekStr = "일요일"
            2 -> dayOfWeekStr = "월요일"
            3 -> dayOfWeekStr = "화요일"
            4 -> dayOfWeekStr = "수요일"
            5 -> dayOfWeekStr = "목요일"
            6 -> dayOfWeekStr = "금요일"
            7 -> dayOfWeekStr = "토요일"

        }






        var timeSplit  = ScheduleDataList.get(position).appointment_time.split(":")

        var scheduleHour = timeSplit[0].toInt()
        var scheduleMinute = timeSplit[1].toInt()

        var amOrPmStr = "AM"
        if( scheduleHour > 11){
            amOrPmStr = "PM"
            if(scheduleHour>12){
                scheduleHour = scheduleHour -12
            }

        }



        holder.meetingDateTv.text = daySplit[1]+"월 "+daySplit[2]+"일 "+dayOfWeekStr+" "+amOrPmStr+" "+scheduleHour.toString()+" : "+scheduleMinute.toString()
        holder.meetingTitleTv.text = ScheduleDataList.get(position).room_title




        var addressSplit = ScheduleDataList.get(position).restaurant_address.split(" ")
        var addressStr = addressSplit[0]+">"+addressSplit[1]
        holder.restaurantAddressTv.text = addressStr+">"+ScheduleDataList.get(position).restaurant_name





    }



    fun addItem(prof: UserProfileJoinHistoryRvDataItem){

        ScheduleDataList.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        ScheduleDataList.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hostOrGuestIv = itemView.findViewById<ImageView>(R.id.hostOrGuestIv)
        val hostorGusetTv = itemView.findViewById<TextView>(R.id.hostorGusetTv)
        val meetingDateTv = itemView.findViewById<TextView>(R.id.meetingDateTv)
        val meetingTitleTv = itemView.findViewById<TextView>(R.id.meetingTitleTv)
        val restaurantAddressTv = itemView.findViewById<TextView>(R.id.restaurantAddressTv)
        //val restaurantNameTv = itemView.findViewById<TextView>(R.id.restaurantNameTv)





    }

}