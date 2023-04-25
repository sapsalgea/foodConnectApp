package com.example.abled_food_connect.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.adapter.GroupChatListFragmentAdapter
import com.example.abled_food_connect.data.GroupChatListData
import com.example.abled_food_connect.data.LoadingGroupChat
import com.example.abled_food_connect.data.RoomData
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatGroupFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var Gadapter: GroupChatListFragmentAdapter
    lateinit var gson: Gson

    companion object {
        const val TAG: String = "그룹채팅 프래그먼트 로그"

        lateinit var socket: Socket

        fun newInstance(): ChatGroupFragment {
            return ChatGroupFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ChatGroupFragment.TAG, "그룹채팅 프래그먼트 onCreate()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(ChatGroupFragment.TAG, "그룹채팅 프래그먼트 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(ChatGroupFragment.TAG, "그룹채팅 프래그먼트 onCreateView()")
        val view = inflater.inflate(R.layout.fragment_chat_group, container, false)
        recyclerView = view.findViewById(R.id.ChatGroupRCV)
        Gadapter = GroupChatListFragmentAdapter(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        loadChatList()


        gson = Gson()


        return view
    }

    override fun onStart() {
        super.onStart()
        Log.d(ChatGroupFragment.TAG, "그룹채팅 프래그먼트 onStart()")
        socket = IO.socket(getString(R.string.chat_list_socket_url))
        init()
        socket.connect()


    }

    override fun onResume() {
        super.onResume()
        Log.d(ChatGroupFragment.TAG, "그룹채팅 프래그먼트 onResume()")

    }
    override fun onPause() {
        super.onPause()

        Log.d(ChatGroupFragment.TAG, "그룹채팅 프래그먼트 onPause()")
        socket.disconnect()
    }

    override fun onStop() {
        super.onStop()
        Log.d(ChatGroupFragment.TAG, "그룹채팅 프래그먼트 onStop()")

    }

    fun socketLoadChatList() {

        val retrofit = Retrofit.Builder()
            .baseUrl("ServerIP")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)
        server.loadGroupChatList(
            MainActivity.user_table_id.toString(),
            MainActivity.loginUserNickname
        ).enqueue(object : Callback<LoadingGroupChat> {
            override fun onResponse(
                call: retrofit2.Call<LoadingGroupChat>,
                response: Response<LoadingGroupChat>
            ) {
                val list: LoadingGroupChat? = response.body()
                if (list != null) {
                    Gadapter.chatList.clear()
                    val array: ArrayList<GroupChatListData> = list.roomList
                    Gadapter.chatList = array
                    recyclerView.adapter = Gadapter
                    Gadapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: retrofit2.Call<LoadingGroupChat>, t: Throwable) {

            }
        })

    }

    fun loadChatList() {

        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)
        server.loadGroupChatList(
            MainActivity.user_table_id.toString(),
            MainActivity.loginUserNickname
        ).enqueue(object : Callback<LoadingGroupChat> {
            override fun onResponse(
                call: retrofit2.Call<LoadingGroupChat>,
                response: Response<LoadingGroupChat>
            ) {
                val list: LoadingGroupChat? = response.body()
                if (list != null) {
                    Gadapter.chatList.clear()
                    val array: ArrayList<GroupChatListData> = list.roomList
                    Gadapter.chatList = array
                    recyclerView.adapter = Gadapter
                    Gadapter.notifyDataSetChanged()
                    for (item in array) {
                        socket.emit(
                            "listIn",
                            gson.toJson(
                                RoomData(
                                    MainActivity.loginUserNickname,
                                    item.roomId!!,
                                    MainActivity.user_table_id
                                )
                            )
                        )
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<LoadingGroupChat>, t: Throwable) {

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



    fun init() {

        socket.on(Socket.EVENT_CONNECT, Emitter.Listener {
            loadChatList()
        })
        socket.on("listRead") {
            socketLoadChatList()
        }
    }
}