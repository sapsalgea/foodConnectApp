package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.viewpager2.widget.ViewPager2
import com.example.abled_food_connect.adapter.MyPageUserScheduleListActivityViewPagerAdapter
import com.example.abled_food_connect.databinding.ActivityMyPageUserScheduleListBinding
import com.example.abled_food_connect.fragments.MyPageUserScheduleListFirstFragment
import com.example.abled_food_connect.fragments.MyPageUserScheduleListSecondFragment
import com.example.abled_food_connect.fragments.MyPageUserScheduleListThirdFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MyPageUserScheduleListActivity : AppCompatActivity() {

    private lateinit var viewPager : ViewPager2
    private lateinit var tabLayout : TabLayout

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityMyPageUserScheduleListBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_user_schedule_list)


        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityMyPageUserScheduleListBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.


        setSupportActionBar(binding.userScheduleListToolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.userScheduleListToolbar.title = "일정 - 리스트로 보기"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        viewPager = findViewById<ViewPager2>(R.id.viewPager)
        tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        val pagerAdapter = MyPageUserScheduleListActivityViewPagerAdapter(this)

        // 3개의 Fragment Add
        pagerAdapter.addFragment(MyPageUserScheduleListFirstFragment())
        pagerAdapter.addFragment(MyPageUserScheduleListSecondFragment())
        pagerAdapter.addFragment(MyPageUserScheduleListThirdFragment())
        // Adapter

        viewPager.adapter = pagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("ViewPagerFragment", "Page ${position+1}")
            }
        })



        // TabLayout attach
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "예정된 모임"
                }
                1 -> {
                    tab.text = "모임완료"
                }
                else -> {
                    tab.text = "취소된 모임"
                }
            }
        }.attach()

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