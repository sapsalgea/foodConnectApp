package com.example.abled_food_connect.adapter

import android.content.Intent
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.UserProfileActivity
import com.example.abled_food_connect.data.RankingFragmentRvDataItem

class RankingFragmentRvAdapter(val rankingList: ArrayList<RankingFragmentRvDataItem>) : RecyclerView.Adapter<RankingFragmentRvAdapter.CustromViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.ranking_fragments_rv_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return rankingList.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {

//        if(rankingList.get(position).rank == "1"){
//            holder.rankTv.setBackgroundResource(R.drawable.red_star)
//            holder.rankTv.setTextColor(Color.WHITE)
//        }else if(rankingList.get(position).rank == "2"){
//            holder.rankTv.setBackgroundResource(R.drawable.red_star)
//            holder.rankTv.setTextColor(Color.WHITE)
//        }else if(rankingList.get(position).rank == "3"){
//            holder.rankTv.setBackgroundResource(R.drawable.red_star)
//            holder.rankTv.setTextColor(Color.WHITE)
//        }



        if(rankingList.get(position).user_tb_nicname == MainActivity.loginUserNickname){
            holder.rvItemLinearLayout.setBackgroundResource(R.drawable.rectangle_edge_bold)
            holder.showingMeTv.visibility = View.VISIBLE
            holder.rankTv.gravity = Gravity.CENTER_VERTICAL

        }


        if(rankingList.get(position).isTopMenu ==1){
            //메뉴일 경우
            holder.rankTv.text = rankingList.get(position).rank

            holder.profileImageIv.visibility = View.GONE

            holder.tierBadgeImageIv.visibility = View.GONE
            holder.rankTv.setTypeface(null, Typeface.BOLD)
            holder.nicnameTv.setTypeface(null, Typeface.BOLD)
            holder.rankingPointTv.setTypeface(null, Typeface.BOLD)
            holder.tierTv.setTypeface(null, Typeface.BOLD)

        }else{

            holder.tierBadgeImageIv.load(holder.profileImageIv.context.getString(R.string.http_request_base_url)+rankingList.get(position).tier_image)

            holder.profileImageIv.load(holder.profileImageIv.context.getString(R.string.http_request_base_url)+rankingList.get(position).profile_image)
//            Glide.with(holder.profileImageIv.context)
//                .load(holder.profileImageIv.context.getString(R.string.http_request_base_url)+rankingList.get(position).profile_image)
//                .into(holder.profileImageIv)

            holder.toProfileActivityLinearLayout.setOnClickListener(View.OnClickListener {
                var toMoveUserProfileActivity : Intent = Intent(holder.toProfileActivityLinearLayout.context, UserProfileActivity::class.java)
                toMoveUserProfileActivity.putExtra("writer_user_tb_id", rankingList.get(position).user_tb_id)
                holder.toProfileActivityLinearLayout.context.startActivity(toMoveUserProfileActivity, null)
            })


            //top 1~3등은 순위에 이미지를 넣어준다.
            if(rankingList.get(position).rank.toInt() ==1){

                holder.topRankMedalIv.visibility = View.VISIBLE
                holder.rankTv.visibility = View.GONE
                holder.topRankMedalIv.load(holder.profileImageIv.context.getString(R.string.http_request_base_url)+"ranking/top_badge_image/top1_medal_1.png")
            }
            else if(rankingList.get(position).rank.toInt() == 2){
                holder.topRankMedalIv.visibility = View.VISIBLE
                holder.rankTv.visibility = View.GONE
                holder.topRankMedalIv.load(holder.profileImageIv.context.getString(R.string.http_request_base_url)+"ranking/top_badge_image/top2_medal_1.png")
            }
            else if(rankingList.get(position).rank.toInt() == 3){
                holder.topRankMedalIv.visibility = View.VISIBLE
                holder.rankTv.visibility = View.GONE
                holder.topRankMedalIv.load(holder.profileImageIv.context.getString(R.string.http_request_base_url)+"ranking/top_badge_image/top3_medal_1.png")

            }
            //4위 이하..
            else{
                holder.rankTv.text = rankingList.get(position).rank
            }
        }




        holder.nicnameTv.text = rankingList.get(position).user_tb_nicname
        holder.rankingPointTv.text = rankingList.get(position).rank_point.toString()
        holder.tierTv.text = rankingList.get(position).tier



//        holder.gender.setOnClickListener {
//                view-> removeItem(position)
//        }
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }


    fun addItem(prof:RankingFragmentRvDataItem){

        rankingList.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        rankingList.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val rvItemLinearLayout = itemView.findViewById<LinearLayout>(R.id.rvItemLinearLayout)

        val toProfileActivityLinearLayout = itemView.findViewById<LinearLayout>(R.id.toProfileActivityLinearLayout)

        val profileImageIv = itemView.findViewById<ImageView>(R.id.profileImageIv)

        val showingMeTv = itemView.findViewById<TextView>(R.id.showingMeTv)


        val topRankMedalIv = itemView.findViewById<ImageView>(R.id.topRankMedalIv)
        val rankTv = itemView.findViewById<TextView>(R.id.rankTv)
        val nicnameTv = itemView.findViewById<TextView>(R.id.nicnameTv)
        val rankingPointTv = itemView.findViewById<TextView>(R.id.rankingPointTv)

        val tierTv =  itemView.findViewById<TextView>(R.id.tierTv)

        var tierBadgeImageIv = itemView.findViewById<ImageView>(R.id.tierBadgeImageIv)


    }

}