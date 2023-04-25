package com.example.abled_food_connect.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.UserProfileBadgeListActivity
import com.example.abled_food_connect.adapter.RankingFragmentRvAdapter
import com.example.abled_food_connect.adapter.userProfileRankingLatestThreeRvAdapter
import com.example.abled_food_connect.adapter.userProfileRankingListFragmentRvAdapter
import com.example.abled_food_connect.data.RankingFragmentRvData
import com.example.abled_food_connect.data.RankingFragmentRvDataItem
import com.example.abled_food_connect.data.userProfileRankingLatestThreeData
import com.example.abled_food_connect.data.userProfileRankingLatestThreeDataItem
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class UserProfileRakingListFragment : Fragment() {


    lateinit var userProfileRankingRv : RecyclerView

    lateinit var noListAlertTv : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_user_profile_raking_list, container, false)

        noListAlertTv = view.findViewById(R.id.noListAlertTv)
        userProfileRankingRv = view.findViewById(R.id.userProfileRankingRv)

        userProfileRankingRv.layoutManager = GridLayoutManager(context,3)
        userProfileRankingRv.setHasFixedSize(true)




        //랭킹을 불러온다
        rakingLoading(UserProfileBadgeListActivity.user_tb_id)


        return view
    }


    fun rakingLoading(user_tb_id:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.userProfileRankingGetInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val ranking_get = api.user_profile_ranking_get(user_tb_id)


        ranking_get.enqueue(object : Callback<userProfileRankingLatestThreeData> {
            override fun onResponse(
                call: Call<userProfileRankingLatestThreeData>,
                response: Response<userProfileRankingLatestThreeData>
            ) {
                Log.d("최근랭킹 3개", "리뷰 컨텐츠 : ${response.raw()}")
                Log.d("최근 랭킹 3개", "리뷰 컨텐츠 : ${response.body().toString()}")

                var items : userProfileRankingLatestThreeData? =  response.body()



                if (items != null) {

                    var RankingLatestThreeArrayList = ArrayList<userProfileRankingLatestThreeDataItem>()
                    RankingLatestThreeArrayList = items.RankingLatestThreeList as ArrayList<userProfileRankingLatestThreeDataItem>

                    if(RankingLatestThreeArrayList.size==0){
                        noListAlertTv.visibility = View.VISIBLE
                    }

                    val mAdapter =  userProfileRankingListFragmentRvAdapter(RankingLatestThreeArrayList)
                    userProfileRankingRv.adapter = mAdapter
                }

            }
            override fun onFailure(call: Call<userProfileRankingLatestThreeData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }


}