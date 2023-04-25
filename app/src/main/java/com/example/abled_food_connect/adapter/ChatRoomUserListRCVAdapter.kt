package com.example.abled_food_connect.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.abled_food_connect.ChatRoomActivity
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.UserProfileActivity
import com.example.abled_food_connect.data.LoadRoomUsers
import com.example.abled_food_connect.data.MessageData
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class ChatRoomUserListRCVAdapter(
    val context: Context,
    val arrayList: ArrayList<LoadRoomUsers>,
    val hostName: String,
    val roomId :String,
    val members:String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
   val gson = Gson()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ChatroomUserHolder(
            LayoutInflater.from(context).inflate(R.layout.chat_room_user_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatRoomUserData: LoadRoomUsers = arrayList[position]
        val holder = holder as ChatroomUserHolder

        holder.profileImage.load(context.getString(R.string.http_request_base_url) + chatRoomUserData.userThumbnail)
        holder.userId.text = chatRoomUserData.userNickname
        holder.itemView.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra("writer_user_tb_id", chatRoomUserData.userIndexId.toInt())
            context.startActivity(intent)
        }
        holder.userKickButton.setOnClickListener {
            AlertDialog.Builder(context).setMessage("${chatRoomUserData.userNickname}님을 내보내시겠습니까?").setPositiveButton("확인"
            ) { dialog, which ->

                exitRoom(chatRoomUserData)
            }.setNegativeButton("취소",null).create().show()
        }
        if (hostName == MainActivity.loginUserNickname) {
            if (chatRoomUserData.userNickname == MainActivity.loginUserNickname) {
                holder.userKickButton.visibility = View.INVISIBLE
            } else {
                holder.userKickButton.visibility = View.VISIBLE
            }

        } else {
            holder.userKickButton.visibility = View.INVISIBLE
        }
        if (chatRoomUserData.userNickname == hostName) {
            holder.hostUserCrown.visibility = View.VISIBLE
        } else {
            holder.hostUserCrown.visibility = View.INVISIBLE
        }
        if (chatRoomUserData.userNickname == MainActivity.loginUserNickname) {
            holder.meIcon.visibility = View.VISIBLE
        } else {
            holder.meIcon.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ChatroomUserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage = itemView.findViewById<CircleImageView>(R.id.ChatRoomUserProfileImage)
        var userId = itemView.findViewById<TextView>(R.id.chatUserListId)
        var userKickButton = itemView.findViewById<ImageButton>(R.id.userKickButton)
        var hostUserCrown = itemView.findViewById<ImageView>(R.id.hostUserCrown)
        var meIcon = itemView.findViewById<ImageView>(R.id.meIcon)

    }
    private fun exitRoom(user:LoadRoomUsers) {
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)
            .kickRoom(
                roomId,
                user.userIndexId.toString(),
                user.userNickname
            )
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.body() == "true") {

                            val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                            val date = Date()
                            val strDate = sdf.format(date)

                            ChatRoomActivity.socket.emit(
                                "outRoom", gson.toJson(
                                    MessageData(
                                        "EXITROOM",
                                        "GETOUTROOM",
                                        roomId,
                                        user.userNickname, "SERVER",
                                        strDate, members,0,ChatRoomActivity.hostName
                                    )
                                )
                            )
                        ChatRoomActivity.listSocket.emit(
                            "newMessage",
                            gson.toJson(
                                MessageData(
                                    "OUTMEMBER",
                                    "SERVER",
                                    roomId,
                                    user.userNickname,
                                    "SERVER",
                                    strDate,
                                    members,
                                    0, ChatRoomActivity.hostName
                                )
                            )
                        )


                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {

                }
            })
    }

    private fun createOkHttpClient(): OkHttpClient {
        //Log.d ("TAG","OkhttpClient");
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }
}