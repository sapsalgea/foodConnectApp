package com.example.abled_food_connect.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abled_food_connect.DirectMessageActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.ChattingFragmentDmRvDataItem
import java.text.SimpleDateFormat
import java.util.*

class ChattingFragmentDmRvAdapter (val DMArrayList: ArrayList<ChattingFragmentDmRvDataItem>) : RecyclerView.Adapter<ChattingFragmentDmRvAdapter.CustromViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingFragmentDmRvAdapter.CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.chating_dm_fragments_rv_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return DMArrayList.size
    }

    override fun onBindViewHolder(holder: ChattingFragmentDmRvAdapter.CustromViewHolder, position: Int) {

        Glide.with(holder.dmListProfileIv.context)
            .load(holder.dmListProfileIv.context.getString(R.string.http_request_base_url)+DMArrayList.get(position).your_thumbnail_image)
            .circleCrop()
            .into(holder.dmListProfileIv)


        holder.dmListUserNicNameTv.text = DMArrayList.get(position).your_nick_name




        holder.dmListSendTimeTv.text = formatTimeAgo(DMArrayList.get(position).send_time,DMArrayList.get(position).now_server_time)




        if(DMArrayList.get(position).text_or_image_or_dateline == "Image"){
            holder.dmListMessageTv.text = "사진을 보냈습니다."
        }else{
            holder.dmListMessageTv.text = DMArrayList.get(position).content
        }



        if(DMArrayList.get(position).not_read_message_count_row == 0){
            holder.dmListMessageCountTv.visibility =View.GONE
        }else {
            holder.dmListMessageCountTv.text = DMArrayList.get(position).not_read_message_count_row.toString()
        }


        holder.DMListClickBtn.setOnClickListener {

            var toDirectMessageActivity : Intent = Intent(holder.DMListClickBtn.context, DirectMessageActivity::class.java)
            toDirectMessageActivity.putExtra("writer_user_tb_id", DMArrayList.get(position).your_table_id)
            toDirectMessageActivity.putExtra("clicked_user_NicName", DMArrayList.get(position).your_nick_name)
            toDirectMessageActivity.putExtra("clicked_user_ProfileImage", DMArrayList.get(position).your_thumbnail_image)
            toDirectMessageActivity.putExtra("is_account_delete", DMArrayList.get(position).is_account_delete)
            startActivity(holder.DMListClickBtn.context, toDirectMessageActivity, null)
        }
    }

    fun addItem(prof:ChattingFragmentDmRvDataItem){

        DMArrayList.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        DMArrayList.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dmListProfileIv = itemView.findViewById<ImageView>(R.id.dmListProfileIv)
        val dmListUserNicNameTv = itemView.findViewById<TextView>(R.id.dmListUserNicNameTv)
        val dmListSendTimeTv = itemView.findViewById<TextView>(R.id.dmListSendTimeTv)
        val dmListMessageTv = itemView.findViewById<TextView>(R.id.dmListMessageTv)
        val dmListMessageCountTv = itemView.findViewById<TextView>(R.id.dmListMessageCountTv)

        val DMListClickBtn = itemView.findViewById<LinearLayout>(R.id.DMListClickBtn)



    }


    fun formatTimeAgo(sendTime: String, ServerTime:String): String {  // Note : date1 must be in   "yyyy-MM-dd hh:mm:ss"   format
        var conversionTime =""

        val sendTime_str = sendTime
        val format = SimpleDateFormat("yyyy-MM-dd")
        val date: Date = format.parse(sendTime_str)
        val sendDate = format.format(date)

        var sendTimeToken = sendTime.split(' ')
        var hourAndMinute = sendTimeToken[1].split(":")


        val time_sendDate = hourAndMinute[0]+":"+hourAndMinute[1]

        try{
            val format = "yyyy-MM-dd hh:mm:ss"

            val sdf = SimpleDateFormat(format)

           //val datetime= Calendar.getInstance()
            //var date2= sdf.format(datetime.time).toString()

            val dateObj1 = sdf.parse(sendTime)
            val dateObj2 = sdf.parse(ServerTime)
            val diff = dateObj2.time - dateObj1.time

            val diffDays = diff / (24 * 60 * 60 * 1000)
            val diffhours = diff / (60 * 60 * 1000)
            val diffmin = diff / (60 * 1000)
            val diffsec = diff  / 1000
            if(diffDays>1){
                conversionTime+=sendDate
            }else if(diffhours>1){
                conversionTime+=sendDate
            }else if(diffmin>1){
                conversionTime+=time_sendDate
            }else if(diffsec>1){
                conversionTime+=time_sendDate
            }else{
                conversionTime+=time_sendDate
            }
        }catch (ex:java.lang.Exception){
            Log.e("formatTimeAgo",ex.toString())
        }
        if(conversionTime!=""){

        }
        return conversionTime
    }




}