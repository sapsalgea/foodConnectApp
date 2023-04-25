package com.example.abled_food_connect

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.abled_food_connect.databinding.ActivityChatImageZoomBinding

class ChatImageZoom : AppCompatActivity() {
    val binding by lazy { ActivityChatImageZoomBinding.inflate(layoutInflater) }
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val imagePath = intent.getStringExtra("imagePath")

        Glide.with(this).load(getString(R.string.http_request_base_url)+imagePath).into(binding.zoomImageView)

        binding.closeImageView.setOnClickListener {
            onBackPressed()
        }

    }
}