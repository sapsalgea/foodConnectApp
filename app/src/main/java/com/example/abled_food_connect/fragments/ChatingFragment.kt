package com.example.abled_food_connect.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_SETTLING
import com.example.abled_food_connect.R
import com.example.abled_food_connect.adapter.ChatFragmentViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ChatingFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    companion object {
        const val TAG: String = "채팅 프래그먼트 로그"
        fun newInstance(): ChatingFragment {
            return ChatingFragment()
        }

        //0은 그룹채팅, 1은 DM이다.
        var isGroupOrDm = 0

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "채팅 프래그먼트 onCreate()")

        viewPager = ViewPager2(requireContext())
        tabLayout = TabLayout(requireContext())

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "채팅 프래그먼트 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "채팅 프래그먼트 onCreateView()")
        val view = inflater.inflate(R.layout.chating_fragments, container, false)
        viewPager = view.findViewById(R.id.chatViewPager)
        tabLayout = view.findViewById(R.id.ChatFragmentTabLayout)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "채팅 프래그먼트 onViewCreated()")
        val pagerAdapter = ChatFragmentViewPagerAdapter(childFragmentManager, lifecycle)
        pagerAdapter.addFragment(ChatGroupFragment())
        pagerAdapter.addFragment(ChatDMFragment())



        viewPager.adapter = pagerAdapter


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })





        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "그룹채팅"
                }
                else -> {
                    tab.text = "DM"
                }
            }

        }.attach()


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                isGroupOrDm = tab.position
            }
        })




        //FCM으로 DM메시지를 받은 경우, DM리스트를 보여주기 위함
        viewPager.post {
            if(isGroupOrDm == 1) {
                viewPager.setCurrentItem(1, false)
            }
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}