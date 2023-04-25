package com.example.abled_food_connect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.ChatRoomActivity
import com.example.abled_food_connect.data.GroupChatListData
import com.example.abled_food_connect.databinding.ChatFragmentListItemBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GroupChatListFragmentAdapter(
    val context: Context,

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var chatList= ArrayList<GroupChatListData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return customholder(
            ChatFragmentListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var mainFragmentItemData: GroupChatListData = chatList[position]
        val holder = holder as customholder
        holder.bind(mainFragmentItemData)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatRoomActivity::class.java)
            intent.putExtra("roomId", mainFragmentItemData.roomId)
//            intent.putExtra("title", mainFragmentItemData.title)
//            intent.putExtra("info", mainFragmentItemData.info)
            intent.putExtra("hostName", mainFragmentItemData.hostName)
//            intent.putExtra("address", mainFragmentItemData.address)
//            intent.putExtra("date", mainFragmentItemData.date)
//            intent.putExtra("shopName", mainFragmentItemData.shopName)
//            intent.putExtra("roomStatus", mainFragmentItemData.roomStatus)
//            intent.putExtra("numOfPeople", mainFragmentItemData.numOfPeople.toString())
//            intent.putExtra("keyWords", mainFragmentItemData.keyWords)
//            intent.putExtra("nowNumOfPeople", mainFragmentItemData.nowNumOfPeople.toString())
//            intent.putExtra("mapX", mainFragmentItemData.mapX)
//            intent.putExtra("mapY", mainFragmentItemData.mapY)
//            intent.putExtra("finish",mainFragmentItemData.finish)
//            intent.putExtra("imageUrl", "imageUrl")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }


    class customholder(private val binding: ChatFragmentListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        val dateform = SimpleDateFormat("MM-dd HH:mm")
        fun bind(data: GroupChatListData) {
            binding.groupChatListTile.text = data.title
            binding.groupChatListShopName.text = data.placeName
            binding.groupChatListNumfoPeople.text = data.nowNumOfPeople.toString()
            binding.groupChatListMessage.text = data.content
            binding.groupChatListDate.text = isDateSet(data.sendTime!!)

            when (true) {
                data.nonReadCount!! > 300 -> {
                    binding.nonRead.visibility = View.VISIBLE
                    binding.nonRead.text = "300+"
                }
                data.nonReadCount == 0 -> {
                    binding.nonRead.visibility = View.INVISIBLE
                }
                else -> {
                    binding.nonRead.visibility = View.VISIBLE
                    binding.nonRead.text = data.nonReadCount.toString()
                }
            }

        }

        @SuppressLint("SimpleDateFormat")
        fun isDateSet(data:String):String{
            val sdf = SimpleDateFormat("MM-dd")
            val sdf2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            val sdf3 = SimpleDateFormat("HH:mm")
            val calendar: Calendar = Calendar.getInstance()
            val date: Date = calendar.time
            sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val messageTime = sdf2.parse(data)
            val mMessageTime = sdf.format(messageTime)
            val dateResult = sdf.format(date)
//            Log.e("시간","시간은? ${mMessageTime},${dateResult}")
            if (mMessageTime.equals(dateResult)){
                return sdf3.format(messageTime).toString()
            }else{
                return mMessageTime
            }
        }
    }

}