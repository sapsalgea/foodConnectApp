package com.example.abled_food_connect

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.abled_food_connect.adapter.PermissionAdapter
import com.example.abled_food_connect.databinding.ActivityPermissionGrantedBinding

class PermissionGrantedActivity : AppCompatActivity() {
    val binding by lazy { ActivityPermissionGrantedBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val array = ArrayList<Bitmap>()
        array.add(BitmapFactory.decodeResource(resources, R.drawable.permission1))
        array.add(BitmapFactory.decodeResource(resources, R.drawable.permission2))
        array.add(BitmapFactory.decodeResource(resources, R.drawable.permission3))
        array.add(BitmapFactory.decodeResource(resources, R.drawable.permission4))
        binding.PermissionIndicator.setViewPager(binding.PermissionViewPager)
        binding.PermissionIndicator.createIndicators(array.size, 0)
        binding.PermissionViewPager.adapter = PermissionAdapter(array)
        binding.PermissionViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.MoveSettingsButton.setOnClickListener {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
            finish()
        }
        binding.PermissionViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffsetPixels == 0) {
                    binding.PermissionViewPager.currentItem = position;
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("페이저", position.toString())
                binding.PermissionIndicator.animatePageSelected(position % array.size);
                if (position == array.size - 1) {
                    binding.MoveSettingsButton.visibility = View.VISIBLE
                } else {
                    binding.MoveSettingsButton.visibility = View.GONE
                }
            }
        })
    }
}