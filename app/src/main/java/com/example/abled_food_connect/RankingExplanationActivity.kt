package com.example.abled_food_connect

import android.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.abled_food_connect.adapter.RankingExplanationFragmentStateAdapter
import com.example.abled_food_connect.data.MeetingUserEvaluationWritingData
import com.example.abled_food_connect.databinding.ActivityRankingExplanationBinding
import com.example.abled_food_connect.fragments.*
import com.example.abled_food_connect.retrofit.API
import me.relex.circleindicator.CircleIndicator3
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RankingExplanationActivity : AppCompatActivity() {


    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityRankingExplanationBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    var viewPagerPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_ranking_explanation)


        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityRankingExplanationBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.


        //유저테이블의 ranking_explanation_check값을 1로 바꿔준다.

        if(MainActivity.ranking_explanation_check == 0) {
            RankingExplanationActivityEnterCheck()
        }


        val pagerAdapter = RankingExplanationFragmentStateAdapter(this)
        // 3개의 Fragment Add
        pagerAdapter.addFragment(RankingExplanationFirstFragment())
        pagerAdapter.addFragment(RankingExplanationSecondFragment())
        pagerAdapter.addFragment(RankingExplanationThirdFragment())
        pagerAdapter.addFragment(RankingExplanationFourthFragment())
        pagerAdapter.addFragment(RankingExplanationFifthFragment())

        // Adapter
        binding.viewPager.adapter = pagerAdapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("ViewPagerFragment", "Page ${position+1}")

                viewPagerPosition = position

                if(viewPagerPosition != 0){
                    binding.prevBtn.visibility = View.VISIBLE
                }else{
                    binding.prevBtn.visibility = View.GONE
                }

                if(viewPagerPosition == 4){


                    binding.finishBtn.visibility = View.VISIBLE
                    binding.nextBtn.visibility = View.GONE
                }else{
                    binding.finishBtn.visibility = View.GONE
                    binding.nextBtn.visibility = View.VISIBLE
                }
            }
        })

        //binding.viewPager.setUserInputEnabled(false)

        binding.viewPagerIndicator.setViewPager(binding.viewPager)


        binding.prevBtn.setOnClickListener({
            binding.viewPager.setCurrentItem(viewPagerPosition-1)
        })

        binding.nextBtn.setOnClickListener({
            binding.viewPager.setCurrentItem(viewPagerPosition+1)
        })


        binding.finishBtn.setOnClickListener({
            onBackPressed()
        })


        Log.d("랭킹소개엑티비티값", MainActivity.ranking_explanation_check.toString())


    }


    fun RankingExplanationActivityEnterCheck(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(com.example.abled_food_connect.R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.RankingExplanationActivityEnterCheckInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val meeting_user_evaluation_send = api.ranking_explanation_activity_enter_check(MainActivity.user_table_id)


        meeting_user_evaluation_send.enqueue(object : Callback<String> {

            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "가져온값 : ${response.body().toString()}")


                MainActivity.ranking_explanation_check = 1

                val pref = getSharedPreferences("pref_user_data", 0)
                val edit = pref?.edit()
                if (edit != null) {
                    edit.putInt("ranking_explanation_check", 1)
                    edit.apply()//저장완료
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")

            }
        })
    }








}