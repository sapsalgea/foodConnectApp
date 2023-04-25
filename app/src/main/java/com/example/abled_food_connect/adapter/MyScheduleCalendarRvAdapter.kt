package com.example.abled_food_connect.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.MyScheduleCalendarData
import java.util.*
import kotlin.collections.ArrayList

class MyScheduleCalendarRvAdapter (val calDateList: ArrayList<MyScheduleCalendarData>) : RecyclerView.Adapter<MyScheduleCalendarRvAdapter.CustromViewHolder>(){

    //이전에 선택된 값은 다시 채워지지 않은 원으로 바꿔준다.
    var ClickNumber : Int = -1

    //오늘 날짜

    //시간에 사용하는 변수
    val todaycal = Calendar.getInstance()
    var year = todaycal.get(Calendar.YEAR)
    var month = (todaycal.get(Calendar.MONTH))
    var day = todaycal.get(Calendar.DATE)


    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_schedule_calendar_rv_itme,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return calDateList.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {


        holder.rvDay.setOnClickListener({

            ClickNumber = position
            notifyDataSetChanged();

            //클릭리스너로 포지션값을 엑티비티에 전달.
            //해당날짜의 리스트를 보여주기 위함
            itemClickListener.onClick(it, position)

        })

        if(ClickNumber == position){

            //오늘인 경우
            if(calDateList.get(position).rvYear ==  year && calDateList.get(position).rvMonth == month && calDateList.get(position).rvDay ==day){


                holder.rvDay.text = calDateList.get(position).rvDay.toString()
                var drawable: Drawable? = getDrawable(holder.rvDay.context, R.drawable.circular_textview_red_edge)
                holder.rvDay.background = drawable


                holder.rvDay.text = calDateList.get(position).rvDay.toString()

                //배경 테두리
                var backGroundDrawable: Drawable? = getDrawable(holder.rvDay.context, R.drawable.rv_day_tv_rectangle_background)
                holder.rvDayTvBackgroundLinearLayout.background = backGroundDrawable




            }

            else if(calDateList.get(position).meeting_result == 0){


                holder.rvDay.text = calDateList.get(position).rvDay.toString()
                var drawable: Drawable? = getDrawable(holder.rvDay.context, R.drawable.circular_textview_green_edge)
                holder.rvDay.background = drawable


                holder.rvDay.text = calDateList.get(position).rvDay.toString()

                //배경 테두리
                var backGroundDrawable: Drawable? = getDrawable(holder.rvDay.context, R.drawable.rv_day_tv_rectangle_background)
                holder.rvDayTvBackgroundLinearLayout.background = backGroundDrawable



                //모임이 취소또는 종료 - 주황색원
            }else if(calDateList.get(position).meeting_result == 1 || calDateList.get(position).meeting_result == 2){


                if(calDateList.get(position).evaluation_is_required == 0) {

                    holder.rvDay.text = calDateList.get(position).rvDay.toString()
                    var drawable: Drawable? = getDrawable(
                        holder.rvDay.context,
                        R.drawable.circular_textview_yellow_edge
                    )
                    holder.rvDay.background = drawable



                    holder.rvDay.text = calDateList.get(position).rvDay.toString()

                    //배경 테두리
                    var backGroundDrawable: Drawable? = getDrawable(holder.rvDay.context, R.drawable.rv_day_tv_rectangle_background)
                    holder.rvDayTvBackgroundLinearLayout.background = backGroundDrawable
                }else{

                    holder.rvDay.text = calDateList.get(position).rvDay.toString()
                    var drawable: Drawable? = getDrawable(
                        holder.rvDay.context,
                        R.drawable.circular_textview_orange_edge
                    )
                    holder.rvDay.background = drawable



                    holder.rvDay.text = calDateList.get(position).rvDay.toString()

                    //배경 테두리
                    var backGroundDrawable: Drawable? = getDrawable(holder.rvDay.context, R.drawable.rv_day_tv_rectangle_background)
                    holder.rvDayTvBackgroundLinearLayout.background = backGroundDrawable
                }





            }else{
                if(calDateList.get(position).rvDay!=0) {
                    holder.rvDay.text = calDateList.get(position).rvDay.toString()
                    //배경 테두리
                    var backGroundDrawable: Drawable? = getDrawable(holder.rvDay.context, R.drawable.rv_day_tv_rectangle_background)
                    holder.rvDayTvBackgroundLinearLayout.background = backGroundDrawable
                }
            }
        }else {


            if (calDateList.get(position).rvDay != 0) {

                //오늘인 경우
                if(calDateList.get(position).rvYear ==  year && calDateList.get(position).rvMonth == month && calDateList.get(position).rvDay ==day){

                    var drawable : Drawable? = getDrawable(holder.rvDay.context, R.drawable.circular_textview_red_full)
                    holder.rvDay.background =  drawable
                    holder.rvDay.setTextColor(Color.WHITE)


                    holder.rvDay.text = calDateList.get(position).rvDay.toString()

                }

                //모임이 진행중 - 초록색원
                else if (calDateList.get(position).meeting_result == 0) {

                    var drawable : Drawable? = getDrawable(holder.rvDay.context, R.drawable.circular_textview_green_full)
                    holder.rvDay.background =  drawable
                    holder.rvDay.setTextColor(Color.WHITE)

                    holder.rvDay.text = calDateList.get(position).rvDay.toString()

                //모임이 취소또는 종료 - 주황색원
               } else if (calDateList.get(position).meeting_result == 1 || calDateList.get(position).meeting_result == 2) {


                    if(calDateList.get(position).evaluation_is_required == 0) {
                        var drawable : Drawable? = getDrawable(holder.rvDay.context, R.drawable.social_login_kakao_button)
                        holder.rvDay.background =  drawable
                        holder.rvDay.setTextColor(Color.WHITE)
                        holder.rvDay.text = calDateList.get(position).rvDay.toString()

                    }else{
                        var drawable : Drawable? = getDrawable(holder.rvDay.context, R.drawable.circular_textview_orange_full)
                        holder.rvDay.background =  drawable
                        holder.rvDay.setTextColor(Color.WHITE)
                        holder.rvDay.text = calDateList.get(position).rvDay.toString()
                    }




                } else {

                    if(calDateList.get(position).rvDay!=0) {
                        holder.rvDay.text = calDateList.get(position).rvDay.toString()
                    }
                }

            }
        }












    }

    fun addItem(prof:MyScheduleCalendarData){

        calDateList.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){
        calDateList.removeAt(position)
        notifyDataSetChanged()
    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvDayTvBackgroundLinearLayout = itemView.findViewById<LinearLayout>(R.id.rvDayTvBackgroundLinearLayout)
        val rvDay = itemView.findViewById<TextView>(R.id.rvDayTv)


    }

}
