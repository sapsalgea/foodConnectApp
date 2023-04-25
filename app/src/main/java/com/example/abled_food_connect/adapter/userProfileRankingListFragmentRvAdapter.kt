package com.example.abled_food_connect.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.userProfileRankingLatestThreeDataItem

class userProfileRankingListFragmentRvAdapter (val List: ArrayList<userProfileRankingLatestThreeDataItem>) : RecyclerView.Adapter<userProfileRankingListFragmentRvAdapter.CustromViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.ranking_list_fragment_rv_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return List.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {

        Glide.with(holder.tierImageIv.context)
            .load(holder.tierImageIv.context.getString(R.string.http_request_base_url)+List.get(position).tier_image)
            .circleCrop()
            .into(holder.tierImageIv)

        holder.seasonNameTv.text = "S${List.get(position).season_name}"
        holder.seasonTierTv.text = List.get(position).user_tier
        holder.rankingPointTv.text ="${ List.get(position).ranking_point}PT"
        holder.rankTv.text = "${List.get(position).season_rank}위"



    }

    fun addItem(prof: userProfileRankingLatestThreeDataItem){

        List.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        List.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tierImageIv = itemView.findViewById<ImageView>(R.id.tierImageIv)
        val seasonNameTv = itemView.findViewById<TextView>(R.id.seasonNameTv)
        val seasonTierTv = itemView.findViewById<TextView>(R.id.seasonTierTv)
        val rankingPointTv = itemView.findViewById<TextView>(R.id.rankingPointTv)
        val rankTv = itemView.findViewById<TextView>(R.id.rankTv)
    }

}