package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abled_food_connect.adapter.MyScheduleTodayScheduleListRvAdapter
import com.example.abled_food_connect.adapter.UserProfileEvaluationListRvAdapter
import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.databinding.ActivityUserProfileBinding
import com.example.abled_food_connect.databinding.ActivityUserProfileEvaluationListBinding
import com.example.abled_food_connect.databinding.ActivityUserRegisterBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfileEvaluationListActivity : AppCompatActivity() {

    /*
       코틀린 뷰 바인딩을 적용시켰습니다.
     */
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityUserProfileEvaluationListBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    var UserProfileEvaluationListRvDataArrayList = ArrayList<UserProfileEvaluationListRvData>()


    lateinit var userProfileEvaluationListRv : RecyclerView

    lateinit var userNicName : String
    var user_tb_id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_evaluation_list)

        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityUserProfileEvaluationListBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)


        setSupportActionBar(binding.Toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.Toolbar.title = "받은 평가"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        if(intent.getStringExtra("user_nicname") != null){
            userNicName = intent.getStringExtra("user_nicname")!!
            user_tb_id = intent.getIntExtra("user_tb_id",0)
            UserProfileEvaluationListLoading(user_tb_id)
        }




        userProfileEvaluationListRv = binding.userProfileEvaluationListRv


        userProfileEvaluationListRv.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        userProfileEvaluationListRv.setHasFixedSize(false)


    }


    fun UserProfileEvaluationListLoading(user_tb_id:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.UserProfileEvaluationListInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val data_get = api.user_profile_evaluation_list_Request(user_tb_id)


        data_get.enqueue(object : Callback<UserProfileEvaluationListData> {
            override fun onResponse(
                call: Call<UserProfileEvaluationListData>,
                response: Response<UserProfileEvaluationListData>
            ) {
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.body().toString()}")

                var items : UserProfileEvaluationListData? =  response.body()








                if (items!!.evaluationList.size>0) {

                    if(items!!.evaluationList.get(0).delightful_type != 0){
                        UserProfileEvaluationListRvDataArrayList.add(UserProfileEvaluationListRvData("유쾌한 분이셨어요.",items!!.evaluationList.get(0).delightful_type))
                    }

                    if(items.evaluationList.get(0).gourmet_type != 0){
                        UserProfileEvaluationListRvDataArrayList.add(UserProfileEvaluationListRvData("고독한 미식가의 느낌이었어요.",items!!.evaluationList.get(0).gourmet_type))
                    }

                    if(items.evaluationList.get(0).funny_type != 0){
                        UserProfileEvaluationListRvDataArrayList.add(UserProfileEvaluationListRvData("재미있으신 분이셨어요",items!!.evaluationList.get(0).funny_type))
                    }

                    if(items.evaluationList.get(0).noisy_type != 0){
                        UserProfileEvaluationListRvDataArrayList.add(UserProfileEvaluationListRvData("시끄러운 분이셨어요",items!!.evaluationList.get(0).noisy_type))
                    }

                    if(items.evaluationList.get(0).curt_type != 0){
                        UserProfileEvaluationListRvDataArrayList.add(UserProfileEvaluationListRvData("무뚝뚝한 분이셨어요",items!!.evaluationList.get(0).curt_type))
                    }

                    if(items.evaluationList.get(0).food_smart_type != 0){
                        UserProfileEvaluationListRvDataArrayList.add(UserProfileEvaluationListRvData("음식에 대한 해박한 지식을 갖고 계셨어요",items!!.evaluationList.get(0).food_smart_type))
                    }

                    if(items.evaluationList.get(0).sociability_type != 0){
                        UserProfileEvaluationListRvDataArrayList.add(UserProfileEvaluationListRvData("친화력이 정말 최고에요!",items!!.evaluationList.get(0).sociability_type))
                    }

                    if(items.evaluationList.get(0).smile_type != 0){
                        UserProfileEvaluationListRvDataArrayList.add(UserProfileEvaluationListRvData("쑥스러움이 많으셨어요",items!!.evaluationList.get(0).smile_type))
                    }

                    if(items.evaluationList.get(0).uncomfortable_type != 0){
                        UserProfileEvaluationListRvDataArrayList.add(UserProfileEvaluationListRvData("부담스럽게 느껴졌어요.",items!!.evaluationList.get(0).uncomfortable_type))
                    }


                    UserProfileEvaluationListRvDataArrayList.sortWith(object: Comparator<UserProfileEvaluationListRvData>{
                        override fun compare(p1: UserProfileEvaluationListRvData, p2: UserProfileEvaluationListRvData): Int = when {
                            p1.type_count > p2.type_count -> -1
                            p1.type_count == p2.type_count -> 0
                            else -> 1
                        }
                    })

                }

                if(UserProfileEvaluationListRvDataArrayList.size >0) {
                    Log.d("TAG", UserProfileEvaluationListRvDataArrayList.get(0).type_name)
                    binding.noticeNoEvaluationTv.visibility = View.GONE

                    binding.topNoticeTv.text = "${userNicName}님이 받은 모임원 평가 내역입니다."

                    var mAdapter = UserProfileEvaluationListRvAdapter(UserProfileEvaluationListRvDataArrayList)
                    mAdapter.notifyDataSetChanged()

                    userProfileEvaluationListRv.adapter = mAdapter
                }else{
                    binding.noticeNoEvaluationTv.visibility = View.VISIBLE
                    binding.topNoticeleLinearLayout.visibility = View.GONE
                    userProfileEvaluationListRv.visibility = View.GONE
                }

            }

            override fun onFailure(call: Call<UserProfileEvaluationListData>, t: Throwable) {
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