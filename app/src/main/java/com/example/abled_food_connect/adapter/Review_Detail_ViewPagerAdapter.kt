package com.example.abled_food_connect.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.abled_food_connect.R

class Review_Detail_ViewPagerAdapter(private var images : List<String>) : RecyclerView.Adapter<Review_Detail_ViewPagerAdapter.Pager2ViewHolder>() {


    inner class  Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemImage: ImageView = itemView.findViewById(R.id.ivImage)


        init {

            itemView.setOnClickListener(View.OnClickListener {
                var position = adapterPosition
                //Toast.makeText(itemView.context,"${position+1}",Toast.LENGTH_SHORT)

            })

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Review_Detail_ViewPagerAdapter.Pager2ViewHolder {
        return Pager2ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.review_detail_view_viewpager2_item_list,parent,false))
    }

    override fun onBindViewHolder(holder: Review_Detail_ViewPagerAdapter.Pager2ViewHolder, position: Int){

        Glide.with(holder.itemImage.context)
            .load(holder.itemImage.context.getString(R.string.http_request_base_url)+images[position])
            .optionalFitCenter()
            //.apply(RequestOptions().cen())
            .into(holder.itemImage)

    }

    override fun getItemCount(): Int {
        return images.size
    }


}