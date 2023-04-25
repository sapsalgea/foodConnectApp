package com.example.abled_food_connect.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abled_food_connect.ChatImageZoom
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.UserProfileActivity
import com.example.abled_food_connect.data.DirectMessageRvData

class DirectMessageRvAdapter (private val mydata: MutableList<DirectMessageRvData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var datas = mydata

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val view : View?
        return when(viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.direct_message_chat_my_message,
                    parent,
                    false
                )
                MyMessageViewHolder(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.direct_message_chat_others_message,
                    parent,
                    false
                )
                YourMessageViewHolder(view)
            }
            2 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.direct_message_chat_my_image,
                    parent,
                    false
                )
                MyImageViewHolder(view)
            }

            3 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.direct_message_chat_others_image,
                    parent,
                    false
                )
                YourImageViewHolder(view)
            }


            4 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.direct_message_chat_date_line,
                    parent,
                    false
                )
                DateLineHolder(view)
            }

            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.direct_message_chat_my_message,
                    parent,
                    false
                )
                MyMessageViewHolder(view)
            }
        }
    }
    override fun getItemCount(): Int = datas.size

    override fun getItemViewType(position: Int): Int {
        var type : Int = 0

        if(datas[position].TextOrImageOrDateLine =="Text" && datas[position].user_tb_id == MainActivity.user_table_id){
            type = 0
        } else if(datas[position].TextOrImageOrDateLine =="Text" && datas[position].user_tb_id != MainActivity.user_table_id){
            type = 1
        } else if(datas[position].TextOrImageOrDateLine =="Image" && datas[position].user_tb_id == MainActivity.user_table_id){
            type = 2
        } else if(datas[position].TextOrImageOrDateLine =="Image" && datas[position].user_tb_id != MainActivity.user_table_id){
            type = 3
        } else if(datas[position].TextOrImageOrDateLine =="DateLine"){
            type = 4
        }
        return type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {



        when(getItemViewType(position)) {
            0 -> {
                (holder as MyMessageViewHolder).bind(datas[position])

            }
            1 -> {
                (holder as YourMessageViewHolder).bind(datas[position])

            }
            2 -> {
                (holder as MyImageViewHolder).bind(datas[position])

            }
            3 -> {
                (holder as YourImageViewHolder).bind(datas[position])

            }

            4 -> {
                (holder as DateLineHolder).bind(datas[position])

            }

            else -> {
                (holder as MyMessageViewHolder).bind(datas[position])
            }
        }
    }

    inner class MyMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val MyMessage: TextView = view.findViewById(R.id.ImageChatMyMessageText)
        private val chatMyTimeStamp: TextView = view.findViewById(R.id.ImageChatMyTimeStamp)
        private val messageCheckTv:TextView = view.findViewById(R.id.messageCheckTv)

        fun bind(item: DirectMessageRvData) {
            MyMessage.text = item.message
            chatMyTimeStamp.text = item.toShowTimeStr
            messageCheckTv.text = item.message_check

        }
    }



    inner class YourMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val YourImgProfile: ImageView = view.findViewById(R.id.chatOthersImage)
        private val YourNicName: TextView = view.findViewById(R.id.chatOthersNickName)
        private val YourMessage: TextView = view.findViewById(R.id.chatOthersMessageText)
        private val chatOthersTimeStamp: TextView = view.findViewById(R.id.chatOthersTimeStamp)



        fun bind(item: DirectMessageRvData) {

            Glide.with(YourImgProfile.context).load(item.userProfileImage).into(YourImgProfile)
            YourImgProfile.setOnClickListener(View.OnClickListener {
                var toMoveUserProfileActivity : Intent = Intent(YourImgProfile.context, UserProfileActivity::class.java)
                toMoveUserProfileActivity.putExtra("writer_user_tb_id", item.user_tb_id)
                startActivity(YourImgProfile.context, toMoveUserProfileActivity, null)
            })

            YourNicName.text = item.userNicName
            YourMessage.text = item.message
            chatOthersTimeStamp.text = item.toShowTimeStr




        }
    }
    inner class MyImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val chatMyIv: ImageView = view.findViewById(R.id.chatMyIv)
        private val chatMyTimeStamp: TextView = view.findViewById(R.id.ImageChatMyTimeStamp)
        private val messageCheckTv:TextView = view.findViewById(R.id.messageCheckTv)

        fun bind(item: DirectMessageRvData) {

            Glide.with(chatMyIv.context).load(chatMyIv.context.getString(R.string.http_request_base_url)+item.message).into(chatMyIv)
            chatMyTimeStamp.text = item.toShowTimeStr
            messageCheckTv.text = item.message_check


            chatMyIv.setOnClickListener(View.OnClickListener {
                var toChatImageZoomActivity : Intent = Intent(chatMyIv.context, ChatImageZoom::class.java)
                toChatImageZoomActivity.putExtra("imagePath", item.message)
                startActivity(chatMyIv.context, toChatImageZoomActivity, null)
            })

        }
    }

    inner class YourImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val YourImgProfile: ImageView = view.findViewById(R.id.chatOthersImage)
        private val YourNicName: TextView = view.findViewById(R.id.chatOthersNickName)
        private val chatOthersIv: ImageView = view.findViewById(R.id.chatOthersIv)
        private val chatOthersTimeStamp: TextView = view.findViewById(R.id.chatOthersTimeStamp)



        fun bind(item: DirectMessageRvData) {
            Glide.with(YourImgProfile.context).load(item.userProfileImage).into(YourImgProfile)

            YourImgProfile.setOnClickListener(View.OnClickListener {
                var toMoveUserProfileActivity : Intent = Intent(YourImgProfile.context, UserProfileActivity::class.java)
                toMoveUserProfileActivity.putExtra("writer_user_tb_id", item.user_tb_id)
                startActivity(YourImgProfile.context, toMoveUserProfileActivity, null)
            })

            YourNicName.text = item.userNicName
            Glide.with(chatOthersIv.context).load(chatOthersIv.context.getString(R.string.http_request_base_url)+item.message).into(chatOthersIv)
            chatOthersTimeStamp.text = item.toShowTimeStr


            chatOthersIv.setOnClickListener(View.OnClickListener {
                var toChatImageZoomActivity : Intent = Intent(chatOthersIv.context, ChatImageZoom::class.java)
                toChatImageZoomActivity.putExtra("imagePath", item.message)
                startActivity(chatOthersIv.context, toChatImageZoomActivity, null)
            })


        }
    }




    inner class DateLineHolder(view: View) : RecyclerView.ViewHolder(view) {


        private val dateLineTv: TextView = view.findViewById(R.id.dateLineTv)



        fun bind(item: DirectMessageRvData) {


            dateLineTv.text = item.message



        }
    }
}