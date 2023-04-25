package com.example.abled_food_connect.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.abled_food_connect.*
import com.example.abled_food_connect.adapter.MainFragmentAdapter
import com.example.abled_food_connect.data.LoadingRoom
import com.example.abled_food_connect.data.MainFragmentItemData
import com.example.abled_food_connect.data.NewActionAlarmCheckData
import com.example.abled_food_connect.retrofit.API
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainFragment : Fragment() {
    private var mainFragmentListArray: ArrayList<MainFragmentItemData> = ArrayList()
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewAdapter: MainFragmentAdapter
    lateinit var hideRoom: LinearLayout
    lateinit var checkImage: ImageView
    private var check: Boolean = false
    lateinit var swipeRefresh: SwipeRefreshLayout
    lateinit var refreshTextView:SwipeRefreshLayout


    //브로드캐스트 리시버
    val broadcast = NewActionAlarmCheckBroadCastReceiver()

    companion object {
        const val TAG: String = "홈 프래그먼트 로그"
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "메인프래그먼트 onCreate()")


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "메인프래그먼트 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "메인프래그먼트 onCreateView()")
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        val pref = requireContext().getSharedPreferences("pref_user_data", 0)
        MainActivity.user_table_id = pref.getInt("user_table_id", 0)
        MainActivity.loginUserId = pref.getString("loginUserId", "")!!

        hideRoom = view.findViewById(R.id.hideJoinRoom)
        checkImage = view.findViewById(R.id.hideRoomCheck)
        swipeRefresh = view.findViewById(R.id.mainFragmentSwipeRefresh)
        refreshTextView = view.findViewById(R.id.mainFragmentSwipeRefreshTextView)
        recyclerView = view.findViewById(R.id.mainRcv) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mainFragmentListArray = ArrayList()
        recyclerViewAdapter =
            MainFragmentAdapter(requireContext(),this@MainFragment, mainFragmentListArray)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                LinearLayoutManager(this.context).orientation
            )
        )
        swipeRefresh.setOnRefreshListener {
            load()



            //신규 알림이 있는지 확인.
            //알림이 있으면 종모양을 바꿈
            NewActionAlarmCheck()

        }
        refreshTextView.setOnRefreshListener {
            load()


            //신규 알림이 있는지 확인.
            //알림이 있으면 종모양을 바꿈
            NewActionAlarmCheck()
        }
        hideRoom.setOnClickListener {
            check = when (check) {
                false -> {
                    recyclerViewAdapter.filter.filter(MainActivity.user_table_id.toString())
                    checkImage.setImageResource(R.drawable.ic_baseline_check_circle_24)
                    true
                }
                else -> {
                    recyclerViewAdapter.filter.filter(null)
                    checkImage.setImageResource(R.drawable.ic_baseline_noncheck_circle_24)
                    false
                }


            }


        }

        setHasOptionsMenu(true)



        //브로드캐스트 리시버
        val intentFilter = IntentFilter()
        intentFilter.addAction("NewActionAlarm")
        requireContext().registerReceiver(broadcast, intentFilter)


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "메인프래그먼트 onActivityCreated()")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "메인프래그먼트 onViewCreated()")

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "메인프래그먼트 onStart()")

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "메인프래그먼트 onResume()")
        load()


        //신규 알림이 있는지 확인.
        //알림이 있으면 종모양을 바꿈
        //MainFragment에 들어와야 작동
        for (fragment in parentFragmentManager.fragments) {
            if (fragment.isVisible) {
                if (fragment is MainFragment) {
                    NewActionAlarmCheck()
                }
            }
        }


    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "메인프래그먼트 onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "메인프래그먼트 onStop()")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "메인프래그먼트 onDestroyView()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "메인프래그먼트 onDestroy()")

        requireContext().unregisterReceiver(broadcast)
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "메인프래그먼트 onDetach()")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.room_search,menu)
        NewActionAlarmCheck()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.actionAlarmButton -> {
                activity.let {
                    val intent = Intent(context, ActionAlarmActivity::class.java)
                    startActivity(intent)
                }
            }


            R.id.toolbarSearchButton -> {
                activity.let {
                    val intent = Intent(context,RoomSearchActivity::class.java)
                    startActivity(intent)
                }
            }
            else->{

            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun load() {

        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)

        server.loadingRoomGet(MainActivity.loginUserId)
            .enqueue(object : Callback<LoadingRoom> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<LoadingRoom>,
                    response: Response<LoadingRoom>
                ) {
                    recyclerViewAdapter.unList.clear()
                    val list: LoadingRoom = response.body()!!
                    val array: ArrayList<MainFragmentItemData> = list.roomList
                    mainFragmentListArray.addAll(list.roomList)

                    recyclerView.adapter = recyclerViewAdapter
                    recyclerViewAdapter.notifyDataSetChanged()
                    swipeRefresh.isRefreshing = false
                    refreshTextView.isRefreshing = false
                    if(check){
                        recyclerViewAdapter.filter.filter(MainActivity.user_table_id.toString())
                    }else{
                        recyclerViewAdapter.filter.filter(null)
                    }
                }

                override fun onFailure(call: Call<LoadingRoom>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "통신실패:" + t.printStackTrace(),
                        Toast.LENGTH_SHORT
                    ).show()
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








    //새로운 알람목록이 있는지 확인한다.
    //새로운 알람이 있으면, 종 모양을 바꿔준다.
    fun NewActionAlarmCheck(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.NewActionAlarmCheckInterface::class.java)
        val list_get = api.new_action_alarm_check(MainActivity.user_table_id)


        list_get.enqueue(object : Callback<NewActionAlarmCheckData> {
            override fun onResponse(
                call: Call<NewActionAlarmCheckData>,
                response: Response<NewActionAlarmCheckData>
            ) {
                Log.d("신규알람있습니까", "성공 : ${response.raw()}")
                Log.d("신규알람있습니까", "성공 : ${response.body().toString()}")

                if(response.body() != null) {
                    var items =  response.body()



                    var toolbar = activity?.findViewById<Toolbar>(R.id.maintoolbar)

                    if(items!!.alarm_count == 0){

                        if (toolbar != null) {

                            for (fragment in parentFragmentManager.fragments) {
                                if (fragment.isVisible) {
                                    if (fragment is MainFragment) {
                                        toolbar.menu.findItem(R.id.actionAlarmButton).setIcon(R.drawable.ic_baseline_notifications_24)
                                    }
                                }
                            }
                        }

                    }else{


                        if (toolbar != null) {

                            for (fragment in parentFragmentManager.fragments) {
                                if (fragment.isVisible) {
                                    if (fragment is MainFragment) {
                                        toolbar.menu.findItem(R.id.actionAlarmButton).setIcon(R.drawable.ic_baseline_notifications_active_yellow_24)
                                    }
                                }
                            }
                        }


                    }

                }
            }

            override fun onFailure(call: Call<NewActionAlarmCheckData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }



    //브로드캐스트리시버



    inner class NewActionAlarmCheckBroadCastReceiver :BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {

            if (intent.action == "NewActionAlarm") {

                for (fragment in parentFragmentManager.fragments) {
                    if (fragment.isVisible) {
                        if (fragment is MainFragment) {
                            NewActionAlarmCheck()
                        }
                    }
                }

            }

            Log.d("리시버받음", "NewActionAlarm")


        }


    }

}
