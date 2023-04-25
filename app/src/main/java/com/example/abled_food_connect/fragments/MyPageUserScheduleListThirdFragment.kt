package com.example.abled_food_connect.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.adapter.MyScheduleTodayScheduleListRvAdapter
import com.example.abled_food_connect.data.MyPageUserScheduleData
import com.example.abled_food_connect.data.MyPageUserScheduleDataItem
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MyPageUserScheduleListThirdFragment : Fragment() {


    var ScheduleArrayList = ArrayList<MyPageUserScheduleDataItem>()
    lateinit var ScheduleListRv : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(
            R.layout.fragment_my_page_user_schedule_list_third,
            container,
            false
        )



        ScheduleListRv = view.findViewById<RecyclerView>(R.id.scheduleThirdListRv)

        ScheduleListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        ScheduleListRv.setHasFixedSize(false)



        MyPageUserScheduleRvGet()
        return view
    }





    fun MyPageUserScheduleRvGet(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.MyPageUserScheduleRvInterface::class.java)

        //db에 방이 있는지 확인한다.
        val user_schedule_get = api.my_page_user_schedule_rv_get(MainActivity.loginUserNickname,1)


        user_schedule_get.enqueue(object : Callback<MyPageUserScheduleData> {
            override fun onResponse(
                call: Call<MyPageUserScheduleData>,
                response: Response<MyPageUserScheduleData>
            ) {
                Log.d(ReviewFragment.TAG, "스케쥴 조회결과 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "스케쥴 조회결과 : ${response.body().toString()}")

                var items : MyPageUserScheduleData? =  response.body()


                ScheduleArrayList.clear()
                if (items != null) {
                    var dbGetArraylist = items.scheduleList as ArrayList<MyPageUserScheduleDataItem>


                    for(i in 0..dbGetArraylist.size-1){
                        if(dbGetArraylist.get(i).meeting_result == 2){
                            ScheduleArrayList.add(dbGetArraylist.get(i))
                        }
                    }

                    var mAdapter =  MyScheduleTodayScheduleListRvAdapter(ScheduleArrayList)
                    mAdapter.notifyDataSetChanged()

                    ScheduleListRv.adapter = mAdapter


                }



            }

            override fun onFailure(call: Call<MyPageUserScheduleData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }
}