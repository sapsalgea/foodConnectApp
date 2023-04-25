package com.example.abled_food_connect.adapter

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.abled_food_connect.fragments.ChatDMFragment
import com.example.abled_food_connect.fragments.ChatGroupFragment
import com.example.abled_food_connect.fragments.ChatingFragment
import com.example.abled_food_connect.fragments.MyPageFragment


class ChatFragmentViewPagerAdapter(fragment: FragmentManager,lifecycle:Lifecycle) :
    FragmentStateAdapter(fragment,lifecycle) {
    val fragments = ArrayList<Fragment>()
    private lateinit var chatDMFragment: ChatDMFragment
    private lateinit var chatGroupFragment: ChatGroupFragment
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
        notifyItemInserted(fragments.size - 1)
    }

    fun removeFragment() {
        fragments.removeLast()
        notifyItemRemoved(fragments.size)
    }

}