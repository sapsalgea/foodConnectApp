package com.example.abled_food_connect.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.databinding.PermissionItemBinding

class PermissionAdapter(val array: ArrayList<Bitmap>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            PermissionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return permissionViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is permissionViewHolder) {
            holder.bind(array[position])
        }
    }

    override fun getItemCount(): Int {
        return array.size
    }

    class permissionViewHolder(
        private val context: Context,
        private val binding: PermissionItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bitmap: Bitmap) {
            binding.PermissionItemImageView.setImageBitmap(bitmap)
        }

    }
}