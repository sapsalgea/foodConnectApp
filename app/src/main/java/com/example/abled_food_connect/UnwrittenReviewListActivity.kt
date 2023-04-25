package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.adapter.MyScheduleTodayScheduleListRvAdapter
import com.example.abled_food_connect.data.MyPageUserScheduleData
import com.example.abled_food_connect.data.MyPageUserScheduleDataItem
import com.example.abled_food_connect.databinding.ActivityUnwrittenReviewListBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UnwrittenReviewListActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityUnwrittenReviewListBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    var ScheduleArrayList = ArrayList<MyPageUserScheduleDataItem>()
    lateinit var unwrittenReviewListRv : RecyclerView

    override fun onStart() {
        super.onStart()
        UnWrittenReviewListRvGet()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unwritten_review_list)


        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityUnwrittenReviewListBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.


        setSupportActionBar(binding.Toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.Toolbar.title = "후기작성"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        unwrittenReviewListRv = binding.unwrittenReviewListRv


        unwrittenReviewListRv.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        unwrittenReviewListRv.setHasFixedSize(false)




    }


    fun UnWrittenReviewListRvGet(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.UnWrittenReviewListRvInterface::class.java)

        //db에 방이 있는지 확인한다.
        val user_schedule_get = api.un_written_review_list_get(MainActivity.loginUserNickname)


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
                    ScheduleArrayList = items.scheduleList as ArrayList<MyPageUserScheduleDataItem>





                    if(ScheduleArrayList.size >0) {
                        binding.noticeNoReviewTv.visibility = View.GONE

                        var mAdapter = MyScheduleTodayScheduleListRvAdapter(ScheduleArrayList)
                        mAdapter.notifyDataSetChanged()

                        unwrittenReviewListRv.adapter = mAdapter
                    }else{
                        binding.noticeNoReviewTv.visibility = View.VISIBLE
                        unwrittenReviewListRv.visibility =View.GONE
                    }


                }



            }

            override fun onFailure(call: Call<MyPageUserScheduleData>, t: Throwable) {
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