package com.example.abled_food_connect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.UserProfileEvaluationListRvData

class UserProfileEvaluationListRvAdapter (val List: ArrayList<UserProfileEvaluationListRvData>) : RecyclerView.Adapter<UserProfileEvaluationListRvAdapter.CustromViewHolder>(){


    //
    var numberingNumber = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_profile_evaluation_list_rv_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return List.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {
        holder.numberingTv.text = numberingNumber.toString()
        holder.typeNameTv.text = List.get(position).type_name
        holder.typeCountTv.text = List.get(position).type_count.toString()

        numberingNumber = numberingNumber+1

    }

    fun addItem(prof:UserProfileEvaluationListRvData){

        List.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        List.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val typeImageIv = itemView.findViewById<ImageView>(R.id.typeImageIv)


        val numberingTv = itemView.findViewById<TextView>(R.id.numberingTv)
        val typeNameTv = itemView.findViewById<TextView>(R.id.typeNameTv)
        val typeCountTv = itemView.findViewById<TextView>(R.id.typeCountTv)


    }

}