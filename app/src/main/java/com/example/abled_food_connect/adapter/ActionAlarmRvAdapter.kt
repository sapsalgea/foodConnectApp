package com.example.abled_food_connect.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.R
import com.example.abled_food_connect.ReviewCommentActivity
import com.example.abled_food_connect.UserProfileActivity
import com.example.abled_food_connect.data.ActionAlarmListDataItem

class ActionAlarmRvAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var datas = ArrayList<ActionAlarmListDataItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val view : View?
        return when(viewType) {

            //부모댓글일때
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.action_alarm_parent_comment,
                    parent,
                    false
                )
                parentCommentViewHolder(view)
            }

            //자식댓글일때
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.action_alarm_child_comment,
                    parent,
                    false
                )
                childCommentViewHolder(view)
            }

            //좋아요가 눌렸을때
            2 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.action_alarm_like_click,
                    parent,
                    false
                )
                userLikeAlertViewHolder(view)
            }

            //else도 좋아요가 눌렸을때이다. 추가할때 수정요망
            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.action_alarm_like_click,
                    parent,
                    false
                )
                userLikeAlertViewHolder(view)
            }
        }
    }
    override fun getItemCount(): Int = datas.size


    override fun getItemViewType(position: Int): Int {
        var type : Int = 0

        if(datas[position].action_type =="parent_comment"){
            type = 0
        } else if(datas[position].action_type =="child_comment"){
            type = 1
        } else if(datas[position].action_type =="review_like_btn"){
            type = 2
        }

        return type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            0 -> {
                (holder as parentCommentViewHolder).bind(datas[position])

            }
            1 -> {
                (holder as childCommentViewHolder).bind(datas[position])

            }

            2 -> {
                (holder as userLikeAlertViewHolder).bind(datas[position])
            }
        }
    }

    inner class parentCommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val parentCommentClickBtnLL: LinearLayout = view.findViewById(R.id.parentCommentClickBtnLL)
        private val parentCommentDatetimeTv: TextView = view.findViewById(R.id.parentCommentDatetimeTv)
        private val parentCommentTitleTv: TextView = view.findViewById(R.id.parentCommentTitleTv)


        fun bind(item: ActionAlarmListDataItem) {
            parentCommentDatetimeTv.text = item.time_ago

            var which_text_choose = spannable{ bold(color(Color.BLACK,"'"+item.which_text_choose+"'")) }
            var sender_user_tb_nicname = spannable{ bold(color(Color.BLACK,"'"+item.sender_user_tb_nicname+"'")) }
            var sender_comment_content = spannable{ bold(color(Color.BLACK,item.sender_comment_content)) }

            parentCommentTitleTv.text = which_text_choose+ " 글에 "+ sender_user_tb_nicname + "님이 댓글을 남기셨습니다. : "+ sender_comment_content


            parentCommentClickBtnLL.setOnClickListener(View.OnClickListener {
                var ParentCommentintent : Intent = Intent(parentCommentClickBtnLL.context, ReviewCommentActivity::class.java)
                ParentCommentintent.putExtra("review_id", item.review_id)
                parentCommentClickBtnLL.context.startActivity(ParentCommentintent, null)


            })



        }
    }
    inner class childCommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val childCommentClickBtnLL: LinearLayout = view.findViewById(R.id.childCommentClickBtnLL)
        private val childCommentDatetimeTv: TextView = view.findViewById(R.id.childCommentDatetimeTv)
        private val childCommentTitleTv: TextView = view.findViewById(R.id.childCommentTitleTv)


        fun bind(item: ActionAlarmListDataItem) {
            childCommentDatetimeTv.text = item.time_ago

            var which_text_choose = spannable{ bold(color(Color.BLACK,"'"+item.which_text_choose+"'")) }
            var sender_user_tb_nicname = spannable{ bold(color(Color.BLACK,"'"+item.sender_user_tb_nicname+"'")) }
            var sender_comment_content = spannable{ bold(color(Color.BLACK,item.sender_comment_content)) }


            childCommentTitleTv.text = which_text_choose+ " 댓글에 "+ sender_user_tb_nicname + "님이 답글을 남기셨습니다. : "+ sender_comment_content

            childCommentClickBtnLL.setOnClickListener(View.OnClickListener {
                var childCommentintent : Intent = Intent(childCommentClickBtnLL.context, ReviewCommentActivity::class.java)
                childCommentintent.putExtra("isChildComment",true)
                childCommentintent.putExtra("review_id",item.review_id)
                childCommentintent.putExtra("groupNum",item.groupNum)
                childCommentintent.putExtra("sendTargetUserTable_id", item.sender_user_tb_id)
                childCommentintent.putExtra("sendTargetUserNicName",item.sender_user_tb_nicname)
                childCommentintent.putExtra("reviewWritingUserId",item.reviewWritingUserId)
                childCommentClickBtnLL.context.startActivity(childCommentintent, null)

            })

        }
    }

    inner class userLikeAlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val userLikeAlertLL: LinearLayout = view.findViewById(R.id.userLikeAlertLL)
        private val userLikeDatetimeTv: TextView = view.findViewById(R.id.userLikeDatetimeTv)
        private val userLikeTitleTv: TextView = view.findViewById(R.id.userLikeTitleTv)


        fun bind(item: ActionAlarmListDataItem) {
            userLikeDatetimeTv.text = item.time_ago

            var which_text_choose = spannable{ bold(color(Color.BLACK,"'"+item.which_text_choose+"'")) }
            var sender_user_tb_nicname = spannable{ bold(color(Color.BLACK,"'"+item.sender_user_tb_nicname+"'")) }

            userLikeTitleTv.text = which_text_choose+" 글을 "+ sender_user_tb_nicname +"님이 좋아합니다."

            userLikeAlertLL.setOnClickListener(View.OnClickListener {
                var ParentCommentintent : Intent = Intent(userLikeAlertLL.context, ReviewCommentActivity::class.java)
                ParentCommentintent.putExtra("review_id", item.review_id)
                userLikeAlertLL.context.startActivity(ParentCommentintent, null)

            })

        }
    }




    //텍스트 스팬
    fun spannable(func: () -> SpannableString) = func()
    private fun span(s: CharSequence, o: Any) = (if (s is String) SpannableString(s) else s as? SpannableString
        ?: SpannableString("")).apply { setSpan(o, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }

    operator fun SpannableString.plus(s: SpannableString) = SpannableString(TextUtils.concat(this, s))
    operator fun SpannableString.plus(s: String) = SpannableString(TextUtils.concat(this, s))

    fun bold(s: CharSequence) = span(s, StyleSpan(Typeface.BOLD))
    fun color(color: Int, s: CharSequence) = span(s, ForegroundColorSpan(color))

}