package com.example.abled_food_connect.adapter


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.UserProfileActivity
import com.example.abled_food_connect.data.ReviewChildPageCommentGetDataItem
import com.example.abled_food_connect.data.CommentDeleteData
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewChildPageCommenRvAdapter(var myItemArrayChildPage: ArrayList<ReviewChildPageCommentGetDataItem>, reviewWriterNicname : String, activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var datas = myItemArrayChildPage
    var reviewWriterNicname = reviewWriterNicname


    //onBackpressed 사용하기 위해서 액티비티를 가져옴.
    var adapterUseActivity = activity


    //클릭리스너
    //클릭 인터페이스 정의
    interface ItemClickListener {
        fun onClick(view: View, commentWriterUserTbId: Int, nicname: String, parentOrchild : Int, groupNum : Int)
    }

    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }


    var selectPosition = 0

    var isParentComment = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.review_comment_item_parent,
                    parent,
                    false
                )
                parent_ViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.review_comment_item_child,
                    parent,
                    false
                )
                child_ViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = datas.size


    override fun getItemViewType(position: Int): Int {


        var type : Int = 0

        if(datas[position].comment_class == 0){
            type = 0
        } else if(datas[position].comment_class == 1){
            type = 1
        }
        return type

    }

    fun removeItem(position : Int){
        myItemArrayChildPage.removeAt(position)
        notifyItemRemoved(position)

    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (datas[position].comment_class) {
            0 -> {
                (holder as parent_ViewHolder).bind(datas[position])

            }
            else -> {
                (holder as child_ViewHolder).bind(datas[position])

            }
        }
    }

    inner class parent_ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        //댓글삭제버튼 reviewParentDotBtn
        private val reviewParentDotBtn: ImageView = view.findViewById(R.id.reviewParentDotBtn)

        private val commentProfileIv: ImageView = view.findViewById(R.id.commentProfileIv)
        private val commentNicnameTv: TextView = view.findViewById(R.id.commentNicnameTv)
        private val isReveiwWriterTv: TextView = view.findViewById(R.id.isReveiwWriterTv)
        private val commentWritingTimeTv: TextView = view.findViewById(R.id.commentWritingTimeTv)
        private val commentContentTv: TextView = view.findViewById(R.id.commentContentTv)
        private val childCommentWrtingBtn: TextView = view.findViewById(R.id.childCommentWrtingBtn)

        fun bind(itemChildPage: ReviewChildPageCommentGetDataItem) {

            if(MainActivity.user_table_id == itemChildPage.writing_user_id){
                reviewParentDotBtn.visibility = View.VISIBLE
            }

            reviewParentDotBtn.setOnClickListener(View.OnClickListener {
                var pop = PopupMenu(reviewParentDotBtn.context,reviewParentDotBtn)

                pop.menuInflater.inflate(R.menu.comment_3_dot_menu, pop.menu)

                // 2. 람다식으로 처리
                pop.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.commentDeleteBtn ->{
                            var builder = AlertDialog.Builder(reviewParentDotBtn.context)
                            builder.setTitle("댓글삭제")
                            builder.setMessage("댓글을 삭제하시겠습니까?")

                            // 버튼 클릭시에 무슨 작업을 할 것인가!
                            var listener = object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    when (p1) {
                                        DialogInterface.BUTTON_POSITIVE ->{
                                            Log.d("TAG", "닫기버튼 클릭")
                                        }

                                        DialogInterface.BUTTON_NEGATIVE ->{


                                            for(i in 0..myItemArrayChildPage.size-1){
                                                if(myItemArrayChildPage.get(i).comment_id == itemChildPage.comment_id){
                                                    selectPosition = i
                                                    break
                                                }
                                            }

                                            Log.d("포지션값", selectPosition.toString())
                                            isParentComment =1
                                            reviewDeleteBtnClick(reviewParentDotBtn.context,itemChildPage.comment_id,itemChildPage.review_id,itemChildPage.comment_class,itemChildPage.groupNum)

                                        }

                                    }
                                }
                            }

                            builder.setPositiveButton("취소", listener)
                            builder.setNegativeButton("삭제", listener)

                            builder.show()
                        }
                    }
                    false
                }
                pop.show()
            })



            Glide.with(commentProfileIv.context)
                .load(commentProfileIv.context.getString(R.string.http_request_base_url)+itemChildPage.profile_image)
                .circleCrop()
                //.apply(RequestOptions().cen())
                .into(commentProfileIv)

            //프로필 이미지 클릭시 프로필엑티비티로 이동
            commentProfileIv.setOnClickListener(View.OnClickListener {
                var toMoveUserProfileActivity : Intent = Intent(commentProfileIv.context, UserProfileActivity::class.java)
                toMoveUserProfileActivity.putExtra("writer_user_tb_id", itemChildPage.writing_user_id)
                startActivity(
                    commentProfileIv.context,
                    toMoveUserProfileActivity,
                    null
                )
            })






            commentNicnameTv.text = itemChildPage.nick_name


            //닉네임 클릭시 프로필엑티비티로 이동
            commentNicnameTv.setOnClickListener(View.OnClickListener {
                var toMoveUserProfileActivity : Intent = Intent(commentNicnameTv.context, UserProfileActivity::class.java)
                toMoveUserProfileActivity.putExtra("writer_user_tb_id", itemChildPage.writing_user_id)
                startActivity(
                    commentNicnameTv.context,
                    toMoveUserProfileActivity,
                    null
                )
            })

            if(itemChildPage.nick_name == reviewWriterNicname){
                isReveiwWriterTv.visibility = View.VISIBLE
            }
            commentWritingTimeTv.text = itemChildPage.comment_Writing_DateTime
            commentContentTv.text = itemChildPage.comment_content


            childCommentWrtingBtn.setOnClickListener {
                itemClickListner.onClick(it,itemChildPage.writing_user_id, itemChildPage.nick_name,1,itemChildPage.groupNum)
            }

        }
    }

    inner class child_ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        //자식댓글 삭제 commentChildDotBtn
        private val commentChildDotBtn: ImageView = view.findViewById(R.id.commentChildDotBtn)


        private val commentProfileIv: ImageView = view.findViewById(R.id.commentProfileIv)
        private val commentNicnameTv: TextView = view.findViewById(R.id.commentNicnameTv)
        private val isReveiwWriterTv: TextView = view.findViewById(R.id.isReveiwWriterTv)
        private val commentWritingTimeTv: TextView = view.findViewById(R.id.commentWritingTimeTv)
        private val commentContentTv: TextView = view.findViewById(R.id.commentContentTv)
        private val childCommentWrtingBtn: TextView = view.findViewById(R.id.childCommentWrtingBtn)

        fun bind(itemChildPage: ReviewChildPageCommentGetDataItem) {


            if(MainActivity.user_table_id == itemChildPage.writing_user_id){
                commentChildDotBtn.visibility = View.VISIBLE
            }

            commentChildDotBtn.setOnClickListener(View.OnClickListener {
                var pop = PopupMenu(commentChildDotBtn.context,commentChildDotBtn)

                pop.menuInflater.inflate(R.menu.comment_3_dot_menu, pop.menu)

                // 2. 람다식으로 처리
                pop.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.commentDeleteBtn ->{
                            var builder = AlertDialog.Builder(commentChildDotBtn.context)
                            builder.setTitle("댓글삭제")
                            builder.setMessage("댓글을 삭제하시겠습니까?")

                            // 버튼 클릭시에 무슨 작업을 할 것인가!
                            var listener = object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    when (p1) {
                                        DialogInterface.BUTTON_POSITIVE ->{
                                            Log.d("TAG", "닫기버튼 클릭")
                                        }

                                        DialogInterface.BUTTON_NEGATIVE ->{


                                            for(i in 0..myItemArrayChildPage.size-1){
                                                if(myItemArrayChildPage.get(i).comment_id == itemChildPage.comment_id){
                                                    selectPosition = i
                                                    break
                                                }
                                            }

                                            Log.d("포지션값", selectPosition.toString())

                                            reviewDeleteBtnClick(commentChildDotBtn.context,itemChildPage.comment_id,itemChildPage.review_id,itemChildPage.comment_class,itemChildPage.groupNum)
                                            Toast.makeText(commentChildDotBtn.context, "댓글을 삭제했습니다.", Toast.LENGTH_SHORT).show()
                                        }

                                    }
                                }
                            }

                            builder.setPositiveButton("취소", listener)
                            builder.setNegativeButton("삭제", listener)

                            builder.show()
                        }
                    }
                    false
                }
                pop.show()
            })

            Glide.with(commentProfileIv.context)
                .load(commentProfileIv.context.getString(R.string.http_request_base_url)+itemChildPage.profile_image)
                .circleCrop()
                //.apply(RequestOptions().cen())
                .into(commentProfileIv)

            //프로필 이미지 클릭시 프로필엑티비티로 이동
            commentProfileIv.setOnClickListener(View.OnClickListener {
                var toMoveUserProfileActivity : Intent = Intent(commentProfileIv.context, UserProfileActivity::class.java)
                toMoveUserProfileActivity.putExtra("writer_user_tb_id", itemChildPage.writing_user_id)
                startActivity(
                    commentProfileIv.context,
                    toMoveUserProfileActivity,
                    null
                )
            })

            commentNicnameTv.text = itemChildPage.nick_name


            //닉네임 클릭시 프로필엑티비티로 이동
            commentNicnameTv.setOnClickListener(View.OnClickListener {
                var toMoveUserProfileActivity : Intent = Intent(commentNicnameTv.context, UserProfileActivity::class.java)
                toMoveUserProfileActivity.putExtra("writer_user_tb_id", itemChildPage.writing_user_id)
                startActivity(
                    commentNicnameTv.context,
                    toMoveUserProfileActivity,
                    null
                )
            })



            if(itemChildPage.nick_name == reviewWriterNicname){
                isReveiwWriterTv.visibility = View.VISIBLE
            }
            commentWritingTimeTv.text = itemChildPage.comment_Writing_DateTime


            //@유저아이디 + 댓글 내용 만들기

            val username = "@"+itemChildPage.sendTargetUserNicName+" "
            val comment = itemChildPage.comment_content
            val text = username + comment

            val startName = text.indexOf(username)
            val endName = startName + username.length
            val startComment = text.indexOf(comment)
            val endComment = text.length

            val spannableString = SpannableString(text)
            val nameClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    var toMoveUserProfileActivity : Intent = Intent(commentWritingTimeTv.context, UserProfileActivity::class.java)
                    toMoveUserProfileActivity.putExtra("writer_user_tb_id", itemChildPage.sendTargetUserTable_id)
                    startActivity(
                        commentWritingTimeTv.context,
                        toMoveUserProfileActivity,
                        null
                    )
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.typeface = Typeface.DEFAULT_BOLD
                    ds.color = Color.BLUE
                    ds.isUnderlineText = false
                }
            }
            val commentClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {

                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.BLACK
                    ds.isUnderlineText = false
                }
            }

            spannableString.setSpan(nameClickableSpan, startName, endName, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(commentClickableSpan, startComment, endComment, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            commentContentTv.text = spannableString
            // 문자열에 사이트 링크로 사용되는 href 가 있다면 setMovementMethod()
            //함수를 사용해야 한다. 그렇게 해야 링크를 클릭해서 원하는 페이지로 이동할 수 있다.
            commentContentTv.movementMethod = LinkMovementMethod.getInstance()







            childCommentWrtingBtn.setOnClickListener {
                itemClickListner.onClick(it,itemChildPage.writing_user_id, itemChildPage.nick_name,1,itemChildPage.groupNum)
            }

        }
    }



    //댓글 삭제버튼
    fun reviewDeleteBtnClick(context : Context, what_click_comment_tb_id:Int,review_id:Int,parentOrChild: Int ,groupNum: Int){

        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.commentDeleteBtn::class.java)
        val comment_del = api.comment_delete_btn_click(MainActivity.user_table_id,what_click_comment_tb_id,review_id,parentOrChild,groupNum)



        comment_del.enqueue(object : Callback<CommentDeleteData> {
            override fun onResponse(
                call: Call<CommentDeleteData>,
                response: Response<CommentDeleteData>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                if(response.body() != null) {
                    val returnString: CommentDeleteData = response.body()!!

                    if(returnString.review_deleted == 1){
                        adapterUseActivity.onBackPressed()
                    }

                    else if(returnString.success =="true"){
                        Log.d("트루", "트루")

                        Toast.makeText(context, "댓글을 삭제했습니다.", Toast.LENGTH_SHORT).show()

                        if(isParentComment == 1){
                            adapterUseActivity.onBackPressed()
                        }
                        removeItem(selectPosition)
                    }else{
                        Log.d("false", "false")
                    }


                }


            }

            override fun onFailure(call: Call<CommentDeleteData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }

}