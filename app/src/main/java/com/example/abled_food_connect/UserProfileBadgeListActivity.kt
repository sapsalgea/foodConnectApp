package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.abled_food_connect.adapter.UserProfileBadgeActivityViewPagerAdapter
import com.example.abled_food_connect.databinding.ActivityUserProfileBadgeListBinding
import com.example.abled_food_connect.fragments.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class UserProfileBadgeListActivity : AppCompatActivity() {

    /*
       코틀린 뷰 바인딩을 적용시켰습니다.
     */
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityUserProfileBadgeListBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    lateinit var userProfileBadgeListRv : RecyclerView


    //뱃지
    private lateinit var viewPager : ViewPager2
    private lateinit var tabLayout : TabLayout


    //탭 레이아웃으로 뱃지, 이전 랭킹 가져올때 필요한 user_tb_id, 닉네임 변수
    companion object{
        var user_tb_id = 0
        lateinit var userNicName : String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_badge_list)


        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityUserProfileBadgeListBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)


        setSupportActionBar(binding.Toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.Toolbar.title = "뱃지/지난랭킹보기"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        viewPager = findViewById<ViewPager2>(R.id.viewPager)
        tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        val pagerAdapter = UserProfileBadgeActivityViewPagerAdapter(this)

        // 2개의 Fragment Add
        pagerAdapter.addFragment(UserProfileBadgeListFragment())
        pagerAdapter.addFragment(UserProfileRakingListFragment())

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
                    tab.text = "뱃지보기"
                }
                else -> {
                    tab.text = "지난랭킹보기"
                }
            }
        }.attach()


        if(intent.getStringExtra("user_nicname") != null){
            userNicName = intent.getStringExtra("user_nicname")!!
            user_tb_id = intent.getIntExtra("user_tb_id", 0)!!
            Log.d("userNicName", userNicName)
            Log.d("user_tb_id", user_tb_id.toString())
        }



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