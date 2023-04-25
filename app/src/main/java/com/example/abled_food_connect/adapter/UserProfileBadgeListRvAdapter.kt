package com.example.abled_food_connect.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.UserProfileBadgeListDataItem

class UserProfileBadgeListRvAdapter(val List: ArrayList<UserProfileBadgeListDataItem>) : RecyclerView.Adapter<UserProfileBadgeListRvAdapter.CustromViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_profile_badge_list_rv_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return List.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {

        holder.badgeNameTv.text = List.get(position).badge_name

        if(List.get(position).badge_achieve_check == 0){
            //뱃지를 획득하지 못한 경우
            holder.badgeImageIv.load(holder.badgeImageIv.context.getString(R.string.http_request_base_url)+List.get(position).badge_fail_goal_image)
            holder.badgeNameTv.setTextColor(Color.GRAY)

        }else{
            //뱃지를 획득한 경우
            holder.badgeImageIv.load(holder.badgeImageIv.context.getString(R.string.http_request_base_url)+List.get(position).badge_achieve_goal_image){
                transformations(CircleCropTransformation())
            }

            holder.badgeNameTv.setTextColor(Color.BLACK)
            holder.badgeNameTv.setTypeface(null, Typeface.BOLD)
        }


        holder.badgeBtn.setOnClickListener {
            var builder = AlertDialog.Builder(holder.badgeBtn.context)


            var layoutInflater = LayoutInflater.from(holder.badgeBtn.context)


            var v1 =  layoutInflater.inflate(R.layout.badge_list_info_dialog, null)
            builder.setView(v1)

            var dialogBadgeImageIv = v1.findViewById<ImageView>(R.id.dialogBadgeImageIv)
            var dialogBadgeNameTv = v1.findViewById<TextView>(R.id.dialogBadgeNameTv)
            var howToAchieveNoticeTv = v1.findViewById<TextView>(R.id.howToAchieveNoticeTv)
            var howToAchieveInfoTv = v1.findViewById<TextView>(R.id.howToAchieveInfoTv)

            dialogBadgeNameTv.text = "${List.get(position).badge_name}"

            if(List.get(position).badge_achieve_check == 0){
                //뱃지를 획득하지 못한 경우
                dialogBadgeImageIv.load(holder.badgeImageIv.context.getString(R.string.http_request_base_url)+List.get(position).badge_fail_goal_image)
                howToAchieveNoticeTv.text = "수집방법"
                howToAchieveNoticeTv.setTextColor(Color.GRAY)
                howToAchieveInfoTv.text = "${List.get(position).how_to_achieve_goal}"

            }else{
                //뱃지를 획득한 경우
                dialogBadgeImageIv.load(holder.badgeImageIv.context.getString(R.string.http_request_base_url)+List.get(position).badge_achieve_goal_image){
                    transformations(CircleCropTransformation())
                }
                howToAchieveNoticeTv.text = "수집완료"
                howToAchieveNoticeTv.setTextColor(Color.BLACK)
                howToAchieveInfoTv.text = "${List.get(position).congratulations_message}"
            }

            // p0에 해당 AlertDialog가 들어온다. findViewById를 통해 view를 가져와서 사용
            var listener = DialogInterface.OnClickListener { p0, p1 ->
                var alert = p0 as AlertDialog


            }

            builder.setPositiveButton("확인", null)
            //builder.setNegativeButton("취소", null)

            builder.show()
        }
    }

    fun addItem(prof:UserProfileBadgeListDataItem){

        List.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        List.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val badgeBtn = itemView.findViewById<ConstraintLayout>(R.id.badgeBtn)
        val badgeImageIv = itemView.findViewById<ImageView>(R.id.badgeImageIv)
        val badgeNameTv = itemView.findViewById<TextView>(R.id.badgeNameTv)


    }

}