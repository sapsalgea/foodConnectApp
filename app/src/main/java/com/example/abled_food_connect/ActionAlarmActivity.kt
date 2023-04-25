package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abled_food_connect.adapter.ActionAlarmRvAdapter
import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.databinding.ActivityActionAlarmBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ActionAlarmActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityActionAlarmBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!



    //알람 리사이클러뷰

    var ActionAlarmAdapter = ActionAlarmRvAdapter()
    var ActionAlarmArrayList = ArrayList<ActionAlarmListDataItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_alarm)


        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityActionAlarmBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)
        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.


        setSupportActionBar(binding.Toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.Toolbar.title = "알림"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //리사이클러뷰

        binding.actionAlarmRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.actionAlarmRv.setHasFixedSize(false)

        //리사이클러뷰 구분선
        val dividerItemDecoration =
            DividerItemDecoration(binding.actionAlarmRv.context, LinearLayoutManager(this).orientation)
        binding.actionAlarmRv.addItemDecoration(dividerItemDecoration)

        binding.actionAlarmRv.adapter = ActionAlarmAdapter


        //알림리스트를 불러온다.
        ActionAlarmGet()

        //엑티비티에 들어와 알람목록을 확인했다는 표시를 한다.
        ActionAlarmActivityEnterCheck()


        binding.ActionAlarmSwipeRefresh.setOnRefreshListener {
            ActionAlarmGet()
            ActionAlarmActivityEnterCheck()
            binding.ActionAlarmSwipeRefresh.isRefreshing = false //서버 통신 완료 후 호출해줍니다.
        }






    }


    fun ActionAlarmGet(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.ActionAlarmListGetInterface::class.java)
        val list_get = api.action_alarm_list_get(MainActivity.user_table_id)


        list_get.enqueue(object : Callback<ActionAlarmListData> {
            override fun onResponse(
                call: Call<ActionAlarmListData>,
                response: Response<ActionAlarmListData>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                if(response.body() != null) {
                    val ReviewLikeBtnClickData: ActionAlarmListData = response.body()!!

                    ActionAlarmAdapter.datas.clear()
                    ActionAlarmArrayList = ReviewLikeBtnClickData!!.ActionAlarmList as ArrayList<ActionAlarmListDataItem>
                    if(ActionAlarmArrayList.size == 0){
                        binding.ifNoDataAlertTv.visibility = View.VISIBLE
                        binding.actionAlarmRv.visibility = View.GONE
                    }
                    ActionAlarmAdapter.datas = ActionAlarmArrayList
                    ActionAlarmAdapter.notifyDataSetChanged()



                }


            }

            override fun onFailure(call: Call<ActionAlarmListData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }



    //엑티비티에 들어와 알람목록을 확인했다는 표시를 한다.
    fun ActionAlarmActivityEnterCheck(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.ActionAlarmActivityEnterCheckInterface::class.java)
        val list_get = api.action_alarm_activity_enter_check(MainActivity.user_table_id)


        list_get.enqueue(object : Callback<ActionAlarmActivityEnterCheckData> {
            override fun onResponse(
                call: Call<ActionAlarmActivityEnterCheckData>,
                response: Response<ActionAlarmActivityEnterCheckData>
            ) {
                Log.d("알람목록을 확인", "성공 : ${response.raw()}")
                Log.d("알람목록을 확인", "성공 : ${response.body().toString()}")

                if(response.body() != null) {

                }
            }

            override fun onFailure(call: Call<ActionAlarmActivityEnterCheckData>, t: Throwable) {
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