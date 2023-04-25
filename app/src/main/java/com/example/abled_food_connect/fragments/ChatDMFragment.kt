package com.example.abled_food_connect.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.adapter.ChattingFragmentDmRvAdapter
import com.example.abled_food_connect.data.ChattingFragmentDmRvData
import com.example.abled_food_connect.data.ChattingFragmentDmRvDataItem
import com.example.abled_food_connect.retrofit.API
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URISyntaxException

class ChatDMFragment : Fragment() {

    //리사이클러뷰 어래이리스트
    private var DM_List_arrayList = ArrayList<ChattingFragmentDmRvDataItem>()

    //리사이클러뷰
    lateinit var DM_List_rv : RecyclerView


    //그리드뷰 어댑터
    lateinit var DM_List_rv_Adapter : ChattingFragmentDmRvAdapter

    lateinit var dmNoListAlertTv : TextView


    //소켓통신
    //전역변수 설정
    lateinit var mySocket : Socket
    lateinit var username : String
    lateinit var roomNumber : String
    var messageText : String = ""

    //소켓통신으로 넘겨주는 방 이름을 저장하는 변수
    var roomNameStr : String = ""

    companion object {
        const val TAG: String = "DM 프래그먼트 로그"
        fun newInstance(): ChatDMFragment {
            return ChatDMFragment()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ChatDMFragment.TAG,"DM 프래그먼트 onCreate()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(ChatDMFragment.TAG,"DM 프래그먼트 onAttach()")
    }

    override fun onResume() {
        super.onResume()

        DM_Message_List_get()

    }


    override fun onPause() {
        super.onPause()
        mySocket.disconnect()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_dm, container, false)




        //소켓통신

        try {
            mySocket = IO.socket("ServerIP:3000")
            //소켓이 접속할 uri을 설정한다.
        }catch (e: URISyntaxException) {
            e.printStackTrace()
        }

//        //소켓통신을 시작한다.
//
//        mySocket.connect() // 소켓연결
//        //on은 서버로부터 받는 동작 수행.
//        //emit은 보내는 동작수행
//        mySocket.on(Socket.EVENT_CONNECT, DMFragmentOnConnect)
//        // 첫 연결이 되면 onConnect 메서드가 실행된다
//        // onConnect는 나의 user_tb_id와 방 모든 목록이
//        //서버로부터 메시지를 받는다. "message_from_server"을 키값으로 사용한다.
//        mySocket.on("dm_fragment_refresh", fromServerMessage_Get)
//
//
//        mySocket.on("some_one_login_broadcast",some_one_login_broadcast_refresh)



        //DM 내역이 없으면 출력되는 텍스트뷰
        dmNoListAlertTv = view.findViewById<TextView>(R.id.dmNoListAlertTv)

        //리사이클러뷰
        DM_List_rv = view.findViewById<RecyclerView>(R.id.ChatDmRCV)
        DM_List_rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        DM_List_rv.setHasFixedSize(false)



        //리사이클러뷰 구분선
        val dividerItemDecoration =
            DividerItemDecoration(DM_List_rv.context, LinearLayoutManager(context).orientation)

        DM_List_rv.addItemDecoration(dividerItemDecoration)



        //DM_Message_List_get()







        return view
    }


    val some_one_login_broadcast_refresh  : Emitter.Listener = Emitter.Listener {
        //서버에서 도착한 메시지 받기.
        Log.d("어떤 유저가 채팅방에 들어갔습니다.",  it[0].toString())


    }


    fun DM_Message_List_get(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.DMChattingListGet_Interface::class.java)

        //채팅내역을 가져온다
        val dm_message_list_get = api.dm_room_join_check(MainActivity.user_table_id)


        dm_message_list_get.enqueue(object : Callback<ChattingFragmentDmRvData> {
            override fun onResponse(
                call: Call<ChattingFragmentDmRvData>,
                response: Response<ChattingFragmentDmRvData>
            ) {
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.body().toString()}")

                var items : ChattingFragmentDmRvData? =  response.body()




                if(!DM_List_arrayList.isEmpty()){
                    Log.d(TAG, "실행됨")
                    DM_List_arrayList.clear()
                }


                DM_List_arrayList = items!!.chattingList as ArrayList<ChattingFragmentDmRvDataItem>


                Log.d("DM_List_arrayListsize", DM_List_arrayList.size.toString())

                if(DM_List_arrayList.size == 0){
                    dmNoListAlertTv.visibility =View.VISIBLE

                }else{
                    dmNoListAlertTv.visibility =View.GONE
                }





                DM_List_arrayList.sortWith(object: Comparator<ChattingFragmentDmRvDataItem>{
                    override fun compare(p1: ChattingFragmentDmRvDataItem, p2: ChattingFragmentDmRvDataItem): Int = when {
                        p1.send_time > p2.send_time -> -1
                        p1.send_time == p2.send_time -> 0
                        else -> 1
                    }
                })



                DM_List_rv_Adapter =  ChattingFragmentDmRvAdapter(DM_List_arrayList)
                DM_List_rv_Adapter.notifyDataSetChanged()
                DM_List_rv.adapter = DM_List_rv_Adapter


                for(i in 0..DM_List_arrayList.size-1){
                    roomNameStr = roomNameStr+","+DM_List_arrayList.get(i).room_name
                }



                mySocket.connect() // 소켓연결
                //on은 서버로부터 받는 동작 수행.
                //emit은 보내는 동작수행
                mySocket.on(Socket.EVENT_CONNECT, DMFragmentOnConnect)
                // 첫 연결이 되면 onConnect 메서드가 실행된다
                // onConnect는 나의 user_tb_id와 방 모든 목록이
                //서버로부터 메시지를 받는다. "message_from_server"을 키값으로 사용한다.
                mySocket.on("dm_fragment_refresh", fromServerMessage_Get)


                mySocket.on("some_one_login_broadcast",some_one_login_broadcast_refresh)


                //목록을 다 받고 소켓서버로 유저 아이디와 방목록을 보내준다
                var sendMessage = MainActivity.user_table_id.toString()+"@"+roomNameStr
                mySocket.emit("DMFragmentLogin", sendMessage)


            }

            override fun onFailure(call: Call<ChattingFragmentDmRvData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }


    //소켓통신
    val DMFragmentOnConnect: Emitter.Listener = Emitter.Listener {
        // onConnect는 닉네임과 방이름 목록 서버로 전달한다.
        //서버 측에서는 모든 방에 접속하게 한 후, 어떤 방에서 메시지가 오면, 클라이언트에서 메시지가 도착했음을 알리도록 한다.

        //mySocket.emit("user_room_number", roomNumber)
        //Log.d("Tag", "Socket is connected with ${username+roomNumber}")
    }

    val fromServerMessage_Get: Emitter.Listener = Emitter.Listener {
        //서버에서 도착한 메시지 받기.
        Log.d("Tag",  it[0].toString())

        DM_Message_List_ReLoading()


    }


    fun DM_Message_List_ReLoading(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.DMChattingListGet_Interface::class.java)

        //채팅내역을 가져온다
        val dm_message_list_get = api.dm_room_join_check(MainActivity.user_table_id)


        dm_message_list_get.enqueue(object : Callback<ChattingFragmentDmRvData> {
            override fun onResponse(
                call: Call<ChattingFragmentDmRvData>,
                response: Response<ChattingFragmentDmRvData>
            ) {
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.body().toString()}")

                var items : ChattingFragmentDmRvData? =  response.body()




                if(!DM_List_arrayList.isEmpty()){
                    Log.d(TAG, "실행됨")
                    DM_List_arrayList.clear()
                }
                DM_List_arrayList = items!!.chattingList as ArrayList<ChattingFragmentDmRvDataItem>


                if(DM_List_arrayList.size == 0){
                    dmNoListAlertTv.visibility =View.VISIBLE

                }else{
                    dmNoListAlertTv.visibility =View.GONE
                }

                DM_List_arrayList.sortWith(object: Comparator<ChattingFragmentDmRvDataItem>{
                    override fun compare(p1: ChattingFragmentDmRvDataItem, p2: ChattingFragmentDmRvDataItem): Int = when {
                        p1.send_time > p2.send_time -> -1
                        p1.send_time == p2.send_time -> 0
                        else -> 1
                    }
                })

                Log.d("목록나와라", DM_List_arrayList.toString())





                DM_List_rv_Adapter = ChattingFragmentDmRvAdapter(DM_List_arrayList)

                DM_List_rv_Adapter.notifyDataSetChanged()

                DM_List_rv.adapter = DM_List_rv_Adapter





            }

            override fun onFailure(call: Call<ChattingFragmentDmRvData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }





}