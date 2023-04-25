package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.adapter.UserProfileJoinHistoryRvAdapter
import com.example.abled_food_connect.data.UserProfileJoinHistoryRvData
import com.example.abled_food_connect.data.UserProfileJoinHistoryRvDataItem
import com.example.abled_food_connect.databinding.ActivityUserProfileJoinHistoryBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfileJoinHistoryActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityUserProfileJoinHistoryBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    var ScheduleArrayList = ArrayList<UserProfileJoinHistoryRvDataItem>()
    lateinit var UserProfileJoinHistoryRv : RecyclerView


    lateinit var UserNicName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_join_history)

        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityUserProfileJoinHistoryBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.


        setSupportActionBar(binding.Toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.

       //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        UserNicName = intent.getStringExtra("UserNicName")!!

        if (UserNicName != null) {
            UserProfileJoinHistoryLoading(UserNicName)

            binding.Toolbar.title = "${UserNicName}님의 참여내역"
        }


        UserProfileJoinHistoryRv = binding.UserProfileJoinHistoryRv

        UserProfileJoinHistoryRv.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        UserProfileJoinHistoryRv.setHasFixedSize(false)

    }


    fun UserProfileJoinHistoryLoading(nicName:String){

        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.UserProfileJoinHistoryRvInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val data_get = api.user_profile_join_history_rv_list_get(nicName)


        data_get.enqueue(object : Callback<UserProfileJoinHistoryRvData> {
            override fun onResponse(
                call: Call<UserProfileJoinHistoryRvData>,
                response: Response<UserProfileJoinHistoryRvData>
            ) {
                Log.d("히스토리", "히스토리 : ${response.raw()}")
                Log.d("히스토리", "히스토리 : ${response.body().toString()}")


                var items : UserProfileJoinHistoryRvData? =  response.body()

                var hostCount = items!!.host_count
                var guestCount = items!!.guest_count

                binding.hostPlusGuestCountTv.text = (hostCount+guestCount).toString()
                binding.hostCountTv.text = hostCount.toString()
                binding.guestConutTv.text = guestCount.toString()


                ScheduleArrayList.clear()
                if (items != null) {
                    ScheduleArrayList = items.scheduleList as ArrayList<UserProfileJoinHistoryRvDataItem>

                    if(ScheduleArrayList.size ==0){
                        binding.noHistoryTv.visibility = View.VISIBLE
                        binding.UserProfileJoinHistoryRv.visibility = View.GONE
                    }else{

                        var mAdapter =  UserProfileJoinHistoryRvAdapter(ScheduleArrayList,UserNicName)
                        mAdapter.notifyDataSetChanged()

                        UserProfileJoinHistoryRv.adapter = mAdapter
                    }





                }

            }

            override fun onFailure(call: Call<UserProfileJoinHistoryRvData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {

            android.R.id.home -> {
                finish()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
}