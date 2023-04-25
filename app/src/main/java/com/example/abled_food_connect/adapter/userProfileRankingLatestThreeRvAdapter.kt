package com.example.abled_food_connect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.userProfileRankingLatestThreeDataItem

class userProfileRankingLatestThreeRvAdapter(val List: ArrayList<userProfileRankingLatestThreeDataItem>) : RecyclerView.Adapter<userProfileRankingLatestThreeRvAdapter.CustromViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.ranking_latest_three_rv_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return List.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {
        holder.seasonNameTv.text = "S${List.get(position).season_name}"
        holder.seasonTierTv.text = List.get(position).user_tier
    }

    fun addItem(prof:userProfileRankingLatestThreeDataItem){

        List.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        List.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seasonNameTv = itemView.findViewById<TextView>(R.id.seasonNameTv)
        val seasonTierTv = itemView.findViewById<TextView>(R.id.seasonTierTv)
    }

}