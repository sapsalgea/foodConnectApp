package com.example.abled_food_connect.adapter

import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.MeetingEvaluationUserListRvDataItem

class MeetingUserEvaluationRvAdapter(val meetingEndUserList: ArrayList<MeetingEvaluationUserListRvDataItem>) : RecyclerView.Adapter<MeetingUserEvaluationRvAdapter.CustromViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.meeting_user_evaluation_rv_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return meetingEndUserList.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {

        //작성자 프로필
        Glide.with(holder.profileIv.context)
            .load(holder.profileIv.context.getString(R.string.http_request_base_url)+ meetingEndUserList.get(position).thumbnail_image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(holder.profileIv)

        if(meetingEndUserList.get(position).is_host == true){
            holder.isHostTv.text = "호스트 "
        }else{
            holder.isHostTv.visibility = View.GONE
        }

        holder.userNicNameTv.text = meetingEndUserList.get(position).user_nickname

        //만약 평가하려는 모임원이 탈퇴했다면, 평가버튼이 사라지고 탈퇴했다는 문구가 뜬다.
        if(meetingEndUserList.get(position).is_account_delete == 1){


            holder.userAccountDeleteAlertTv.visibility = View.VISIBLE

            holder.evaluationCheckLL.visibility  = View.GONE





        }else{

        }

        holder.userEvaluationBtn.setOnClickListener {

            val popupMenu: PopupMenu = PopupMenu(holder.userEvaluationBtn.context,holder.userEvaluationBtn)
            popupMenu.menuInflater.inflate(R.menu.meeting_user_evaluation_pop_up_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.Item1 ->{
                        holder.userEvaluationTv.text = "유쾌함"
                        itemClickListener.onClick(it, position,"유쾌함")
                    }

                    R.id.Item2 -> {
                        holder.userEvaluationTv.text = "고독한미식가"
                        itemClickListener.onClick(it, position,"고독한미식가")
                    }
                    R.id.Item3 ->{
                        holder.userEvaluationTv.text = "재미있음"
                        itemClickListener.onClick(it, position,"재미있음")
                    }
                    R.id.Item4 ->{
                        holder.userEvaluationTv.text = "시끄러움"
                        itemClickListener.onClick(it, position,"시끄러움")
                    }
                    R.id.Item5 -> {
                        holder.userEvaluationTv.text = "무뚝뚝"
                        itemClickListener.onClick(it, position, "무뚝뚝")
                    }
                    R.id.Item6 ->{
                        holder.userEvaluationTv.text = "맛잘알"
                        itemClickListener.onClick(it, position,"맛잘알")
                    }
                    R.id.Item7 ->{
                        holder.userEvaluationTv.text = "친화력갑"
                        itemClickListener.onClick(it, position,"친화력갑")
                    }
                    R.id.Item8 ->{
                        holder.userEvaluationTv.text = "미소지기"
                        itemClickListener.onClick(it, position,"미소지기")
                    }
                    R.id.Item9 ->{
                        holder.userEvaluationTv.text = "부담스러움"
                        itemClickListener.onClick(it, position,"부담스러움")
                    }

                    R.id.Item10 ->{
                        holder.userEvaluationTv.text = "선택안함"
                        itemClickListener.onClick(it, position,"선택안함")
                    }

                }
                true
            })
            popupMenu.show()
        }




        //스크롤을 다시 올렸을때, 체크박스가 풀리지 않고 재활용된 뷰가 제대로 뜨게 하기 위함.
        if(meetingEndUserList.get(position).user_evaluation_what_did_you_say == "노쇼"){
            holder.noShowCheckBox.isChecked = true
            holder.userEvaluationBtn.isEnabled = false
            holder.userEvaluationBtn.setBackgroundColor(Color.GRAY)
            holder.userEvaluationTv.text = "노쇼"

        }else{
            //노쇼체크를 푼 경우
            holder.noShowCheckBox.isChecked = false
            holder.userEvaluationBtn.isEnabled = true
            holder.userEvaluationBtn.setBackgroundResource(R.drawable.comment_writer_edge)
            holder.userEvaluationTv.text = "평가 항목을 선택해주세요."
        }




        holder.noShowCheckBox.setOnClickListener {

            var builder = AlertDialog.Builder(holder.noShowCheckBox.context)
            builder.setTitle("노쇼")
            builder.setMessage(meetingEndUserList.get(position).user_nickname+"님께서 모임장소에 나오지 않으셨나요?")

            // 버튼 클릭시에 무슨 작업을 할 것인가!
            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when (p1) {
                        DialogInterface.BUTTON_NEGATIVE ->{
                            //노쇼체크를 한 경우
                            holder.noShowCheckBox.isChecked = true
                            holder.userEvaluationBtn.isEnabled = false
                            holder.userEvaluationBtn.setBackgroundColor(Color.GRAY)
                            holder.userEvaluationTv.text = "노쇼"
                            meetingEndUserList.get(position).user_evaluation_what_did_you_say = "노쇼"

                            if(meetingEndUserList.get(position).user_evaluation_what_did_you_say == "노쇼"){
                                Log.d("노쇼클릭", "노쇼")
                            }
                        }

                        DialogInterface.BUTTON_POSITIVE ->{
                            //노쇼체크를 푼 경우
                            holder.noShowCheckBox.isChecked = false
                            holder.userEvaluationBtn.isEnabled = true
                            holder.userEvaluationBtn.setBackgroundResource(R.drawable.comment_writer_edge)
                            holder.userEvaluationTv.text = "평가 항목을 선택해주세요."
                            meetingEndUserList.get(position).user_evaluation_what_did_you_say = ""
                        }



                    }
                }
            }

            builder.setNegativeButton("네", listener)
            builder.setPositiveButton("아니오", listener)


            builder.show()
        }



    }

    fun addItem(prof:MeetingEvaluationUserListRvDataItem){

        meetingEndUserList.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        meetingEndUserList.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //함께했던 유저가 탈퇴했을 때, 나오는 문구
        val userAccountDeleteAlertTv = itemView.findViewById<TextView>(R.id.userAccountDeleteAlertTv)

        var evaluationCheckLL = itemView.findViewById<LinearLayout>(R.id.evaluationCheckLL)

        val profileIv = itemView.findViewById<ImageView>(R.id.profileIv)
        val isHostTv = itemView.findViewById<TextView>(R.id.isHostTv)
        val userNicNameTv = itemView.findViewById<TextView>(R.id.userNicNameTv)
        val userEvaluationTv = itemView.findViewById<TextView>(R.id.userEvaluationTv)
        val userEvaluationBtn = itemView.findViewById<LinearLayout>(R.id.userEvaluationBtn)


        var noShowCheckBox = itemView.findViewById<CheckBox>(R.id.noShowCheckBox)
    }


    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int, clickedText: String)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener

}