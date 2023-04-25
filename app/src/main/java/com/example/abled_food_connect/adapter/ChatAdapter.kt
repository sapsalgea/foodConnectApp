package com.example.abled_food_connect.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.abled_food_connect.ChatImageZoom
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.ChatItem
import com.example.abled_food_connect.data.DatetimeToTime
import com.example.abled_food_connect.data.ItemType
import com.example.abled_food_connect.databinding.*
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import org.json.JSONArray

class ChatAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var arrayList = ArrayList<ChatItem>()
    var comArrayList = ArrayList<ChatItem>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val view: View
        val context: Context = parent.context
//        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        return when (viewType) {

            ItemType.LEFT_MESSAGE -> {
                val binding = ChatOthersMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
//                view = inflater.inflate(R.layout.chat_others_message, parent, false)
                LeftMessage(context, binding)
            }
            ItemType.CENTER_MESSAGE -> {
                val binding = ChatServerItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
//                view = inflater.inflate(R.layout.chat_server_item, parent, false)
                ServerMessage(binding)
            }
            ItemType.STARTANDEND -> {
                val binding =
                    ChatStarEndBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                StartAndEnd(binding)
            }
            ItemType.LEFT_IMAGE_MESSAGE -> {
                val binding =
                    ChatOthersImageMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                LeftImageMessage(context, binding)
            }
            ItemType.RIGHT_IMAGE_MESSAGE -> {
//                view = inflater.inflate(R.layout.chat_my_image_message, parent, false)
                val binding =
                    ChatMyImageMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                RightImageMessage(context, binding)
            }

            else -> {
//                view = inflater.inflate(R.layout.chat_my_message, parent, false)
                val binding =
                    ChatMyMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                RightMessage(binding)
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var chatItem: ChatItem = arrayList[position]

        if (holder is LeftMessage) {


            holder.bind(chatItem)


        } else if (holder is ServerMessage) {

            holder.bind(chatItem)
        } else if (holder is RightMessage) {
            holder.bind(chatItem)
//            var chatItem: ChatItem = arrayList[position]
//            var rightHolder = holder
//            val array = JsonParser.parseString(chatItem.readMember) as JsonArray
//            if (array.size() > 0) {
//                holder.rireadMembersMy.visibility = View.VISIBLE
//                holder.rireadMembersMy.text = array.size().toString()
//            } else {
//                holder.rireadMembersMy.visibility = View.INVISIBLE
//            }
//            rightHolder.rimessage.text = chatItem.content
//            rightHolder.ritime.text = chatItem.sendTime
//            rightHolder.rimessage.setOnLongClickListener(object : View.OnLongClickListener {
//                override fun onLongClick(v: View?): Boolean {
//                    val clipboardManager: ClipboardManager =
//                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                    val clipData: ClipData =
//                        ClipData.newPlainText("", rightHolder.rimessage.text.toString())
//                    clipboardManager.setPrimaryClip(clipData)
//                    Toast.makeText(context, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
//                    return true
//                }
//            })
        } else if (holder is LeftImageMessage) {
//            var chatItem: ChatItem = arrayList[position]
//            var leftHolder = holder
//            val array = JsonParser.parseString(chatItem.readMember) as JsonArray
//            if (array.size() > 0) {
//                holder.readMembersOthers.visibility = View.VISIBLE
//                holder.readMembersOthers.text = array.size().toString()
//            } else {
//                holder.readMembersOthers.visibility = View.INVISIBLE
//            }
//            leftHolder.itemView.setPadding(0, 0, 0, 0)
//
//            leftHolder.message.load(context.getString(R.string.http_request_base_url) + chatItem.content)
//            leftHolder.time.text = chatItem.sendTime
//            leftHolder.profileImage.load(context.getString(R.string.http_request_base_url) + chatItem.ThumbnailImage)
            holder.bind(chatItem)
        } else if (holder is RightImageMessage) {
            holder.bind(chatItem)
        } else if (holder is StartAndEnd) {

        }

    }

    override fun getItemViewType(position: Int): Int {
        return arrayList[position].viewType
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
    fun messageRefresh(){
        Log.e("어레이",arrayList.size.toString())
        Log.e("어레이2",comArrayList.size.toString())
        for(num in arrayList.indices){
            if (arrayList[num] != comArrayList[num]){
                Log.e("어레이비교 다름","${comArrayList[num].readMember} , ${arrayList[num].readMember}")
                arrayList[num] = comArrayList[num].copy()
                notifyItemChanged(num)
            }
        }
        comArrayList.clear()
    }


    class LeftMessage(private val context: Context, private val binding: ChatOthersMessageBinding) :
        RecyclerView.ViewHolder(binding.root), DatetimeToTime {
        fun bind(data: ChatItem) {


            binding.chatOthersNickName.text = data.name
            binding.chatOthersNickName.visibility = View.VISIBLE
            binding.chatOthersImage.visibility = View.VISIBLE
            binding.chatOthersImage.load(context.getString(R.string.http_request_base_url) + data.ThumbnailImage)


            val array = (JsonParser.parseString(data.readMember) as JsonArray).size()

            binding.chatOthersMessageText.text = data.content

            binding.chatOthersTimeStamp.text = isDatetimeToTime(data)
            if (array > 0) {
                binding.readMembersOthersMessage.visibility = View.VISIBLE
                binding.readMembersOthersMessage.text = array.toString()
            } else {
                binding.readMembersOthersMessage.visibility = View.GONE
            }
        }

    }

    class RightMessage(private val binding: ChatMyMessageBinding) :
        RecyclerView.ViewHolder(binding.root), DatetimeToTime {
        fun bind(data: ChatItem) {

            val array = JSONArray(data.readMember).length()

            binding.chatMyMessageText.text = data.content
            binding.chatMyTimeStamp.text = isDatetimeToTime(data)
            if (array > 0) {
                binding.readMembersMyMessage.visibility = View.VISIBLE
                binding.readMembersMyMessage.text = array.toString()
            } else {
                binding.readMembersMyMessage.visibility = View.GONE
            }

        }


    }

    class LeftImageMessage(
        private val context: Context,
        private val binding: ChatOthersImageMessageBinding
    ) : RecyclerView.ViewHolder(binding.root), DatetimeToTime {
        fun bind(data: ChatItem) {
            val array = (JsonParser.parseString(data.readMember) as JsonArray).size()
            binding.ImageChatOthersProFileImage.load(context.getString(R.string.http_request_base_url) + data.ThumbnailImage)
            binding.ImageChatOthersImage.load(context.getString(R.string.http_request_base_url) + data.content)
            {
//                size(OriginalSize)
//                transformations(RoundedCornersTransformation(30f))
            }
            binding.ImageChatOthersImage.setOnClickListener {
                val intent = Intent(context,ChatImageZoom::class.java)
                intent.putExtra("imagePath",data.content)
                context.startActivity(intent)
            }
            binding.ImageChatOthersNickName.text = data.name
            binding.ImageChatOthersTimeStamp.text = isDatetimeToTime(data)
            if (array > 0) {
                binding.ImageReadMembersOthersMessage.visibility = View.VISIBLE
                binding.ImageReadMembersOthersMessage.text = array.toString()
            } else {
                binding.ImageReadMembersOthersMessage.visibility = View.GONE
            }
        }
    }

    class RightImageMessage(
        private val context: Context,
        private val binding: ChatMyImageMessageBinding
    ) : RecyclerView.ViewHolder(binding.root), DatetimeToTime {
        fun bind(data: ChatItem) {
            val array = (JsonParser.parseString(data.readMember) as JsonArray).size()

            binding.ImageChatMyImage.load(context.getString(R.string.http_request_base_url) + data.content)
            {
//                size(OriginalSize)
//                transformations(RoundedCornersTransformation(30f))

            }
            binding.ImageChatMyImage.setOnClickListener {
                val intent = Intent(context,ChatImageZoom::class.java)
                intent.putExtra("imagePath",data.content)
                context.startActivity(intent)
            }
            binding.ImageChatMyTimeStamp.text = isDatetimeToTime(data)
            if (array > 0) {
                binding.ImageReadMembersMyMessage.visibility = View.VISIBLE

                binding.ImageReadMembersMyMessage.text = array.toString()
            } else {
                binding.ImageReadMembersMyMessage.visibility = View.GONE
            }
        }
    }

    class ServerMessage(private val binding: ChatServerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ChatItem) {
            binding.contentText.text = data.content
        }

    }

    class StartAndEnd(private val binding: ChatStarEndBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
    fun isArrayComparisonChange(array:ArrayList<ChatItem>){

    }

}