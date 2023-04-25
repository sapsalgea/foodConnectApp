package com.example.abled_food_connect.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.R
import com.example.abled_food_connect.UserProfileBadgeListActivity
import com.example.abled_food_connect.adapter.UserProfileBadgeListRvAdapter
import com.example.abled_food_connect.data.UserProfileBadgeListData
import com.example.abled_food_connect.data.UserProfileBadgeListDataItem
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class UserProfileBadgeListFragment : Fragment() {

    lateinit var userProfileBadgeListRv : RecyclerView
    lateinit var topNoticeTv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_user_profile_badge_list, container, false)

        userProfileBadgeListRv = view.findViewById(R.id.userProfileBadgeListRv)
//        topNoticeTv = view.findViewById(R.id.topNoticeTv)
//        topNoticeTv.text = "${UserProfileBadgeListActivity.userNicName}님의 뱃지 리스트"


        //rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        userProfileBadgeListRv.layoutManager = GridLayoutManager(context,3)
        userProfileBadgeListRv.setHasFixedSize(true)


        badgeListLoading(UserProfileBadgeListActivity.user_tb_id, UserProfileBadgeListActivity.userNicName)

        Log.d("나와라", UserProfileBadgeListActivity.user_tb_id.toString()+UserProfileBadgeListActivity.userNicName)

        return view
    }


    fun badgeListLoading(user_tb_id:Int , user_nic_name : String){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.UserProfileBadgeListDataGetInterface::class.java)


        val data_get = api.user_profile_badge_list_data_get(user_tb_id,user_nic_name)


        data_get.enqueue(object : Callback<UserProfileBadgeListData> {
            override fun onResponse(
                call: Call<UserProfileBadgeListData>,
                response: Response<UserProfileBadgeListData>
            ) {
                Log.d("뱃지리스트", "뱃지 컨텐츠 : ${response.raw()}")
                Log.d("뱃지리스트", "뱃지 컨텐츠 : ${response.body().toString()}")

                var items : UserProfileBadgeListData? =  response.body()
                var badgeArrayList = ArrayList<UserProfileBadgeListDataItem>()

                badgeArrayList = items!!.badgeList as ArrayList<UserProfileBadgeListDataItem>

                val mAdapter =  UserProfileBadgeListRvAdapter(badgeArrayList)
                userProfileBadgeListRv.adapter = mAdapter


            }

            override fun onFailure(call: Call<UserProfileBadgeListData>, t: Throwable) {
                Log.d("뱃지리스트", "실패 : $t")
            }
        })
    }

}