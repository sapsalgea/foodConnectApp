package com.example.abled_food_connect.adapter

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.ReviewCommentActivity
import com.example.abled_food_connect.UserProfileActivity
import com.example.abled_food_connect.data.ReviewDeleteCheckData
import com.example.abled_food_connect.data.ReviewDetailViewRvDataItem
import com.example.abled_food_connect.data.ReviewLikeBtnClickData
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import me.relex.circleindicator.CircleIndicator3
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewSearchRvAdapter () : RecyclerView.Adapter<ReviewSearchRvAdapter.CustromViewHolder>(){


    var ReviewDetailList = ArrayList<ReviewDetailViewRvDataItem>()


    //클릭리스너

    //클릭 인터페이스 정의
    interface ItemClickListener {
        fun onClick(view: View, DetailViewPosition: Int, Review_id : Int)
    }

    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_search_rv_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return ReviewDetailList.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {




        //프로필 이미지
        Glide.with(holder.profileSearchIv.context)
            .load(holder.profileSearchIv.context.getString(R.string.http_request_base_url)+ReviewDetailList.get(position).profile_image)
            .circleCrop()
            .into(holder.profileSearchIv)

        //프로필 이미지 클릭시, 프로필 사용자의 리뷰 리스트 엑티비티로 이동
        holder.profileSearchIv.setOnClickListener(View.OnClickListener {

            itemClickListner.onClick(it,position,ReviewDetailList.get(position).review_id)

            var toMoveUserProfileActivity : Intent = Intent(holder.profileSearchIv.context, UserProfileActivity::class.java)
            toMoveUserProfileActivity.putExtra("writer_user_tb_id", ReviewDetailList.get(position).writer_user_tb_id)
            startActivity(
                holder.profileSearchIv.context as Activity,
                toMoveUserProfileActivity,
                null
            )


        })

        //닉네임
        holder.nicNameSearchTv.text = ReviewDetailList.get(position).writer_nicname

        //닉네임 클릭시, 닉네임 사용자의 리뷰 리스트 엑티비티로 이동
        holder.nicNameSearchTv.setOnClickListener(View.OnClickListener {

            itemClickListner.onClick(it,position,ReviewDetailList.get(position).review_id)

            var toMoveUserProfileActivity : Intent = Intent(holder.profileSearchIv.context, UserProfileActivity::class.java)
            toMoveUserProfileActivity.putExtra("writer_user_tb_id", ReviewDetailList.get(position).writer_user_tb_id)
            startActivity(
                holder.profileSearchIv.context as Activity,
                toMoveUserProfileActivity,
                null
            )


        })



        //프로필 이미지
        Glide.with(holder.searchImageIv.context)
            .load(holder.searchImageIv.context.getString(R.string.http_request_base_url)+ReviewDetailList.get(position).review_picture_0)
            .centerCrop()
            .into(holder.searchImageIv)


        //리뷰 작성일
        holder.writingDateSearchTv.text = ReviewDetailList.get(position).reporting_date
        //레스토랑 주소
        holder.restaurantAddressSearchTv.text = ReviewDetailList.get(position).restaurant_address
        //레스토랑 이름
        holder.restaurantNameSearchTv.text = ReviewDetailList.get(position).restaurant_name
        //리뷰 후기 사진 이미지 스위쳐
        //holder.reviewPictureIs



        var imagesList = mutableListOf<String>()
        imagesList.clear()

        imagesList.add(ReviewDetailList.get(position).review_picture_0)
        if(ReviewDetailList.get(position).review_picture_1 !=""){
            imagesList.add(ReviewDetailList.get(position).review_picture_1)
        }

        if(ReviewDetailList.get(position).review_picture_2 !=""){
            imagesList.add(ReviewDetailList.get(position).review_picture_2)
        }



        //리뷰 작성 내용
        holder.reviewDescriptionTv.text = ReviewDetailList.get(position).review_description


//        //좋아요 리니어 레이아웃 좋아요 버튼을 클릭
//        holder.likeBtn.setOnClickListener(View.OnClickListener {
//            RvAdapterReviewLikeBtnClick(ReviewDetailList.get(position).review_id,position,holder.likeBtn.context)
//
//            Log.d("무엇", ReviewDetailList.get(position).review_id.toString()+ MainActivity.user_table_id+ MainActivity.loginUserId)
//        })

        //좋아요 개수
        holder.likeCountTv.text = ReviewDetailList.get(position).like_count

        //좋아요 하트 변경

//        if(ReviewDetailList.get(position).heart_making == true) {
//            holder.heartIv.setColorFilter(Color.parseColor("#77ff0000"))
//        }else{
//            holder.heartIv.setColorFilter(Color.parseColor("#55111111"))
//        }


        //댓글 개수
        holder.commentCountTv.text = ReviewDetailList.get(position).comment_count




        holder.reviewClickBtn.setOnClickListener(View.OnClickListener {

            Log.d("포지션뭐야", position.toString())
            itemClickListner.onClick(it,position,ReviewDetailList.get(position).review_id)

            reviewDeleteCheck(holder.reviewClickBtn,holder.reviewClickBtn.context,ReviewDetailList.get(position).review_id,position)


        })

//        //ReviewCommentActivity
//        holder.commentBtn.setOnClickListener(View.OnClickListener {
//
//
//            itemClickListner.onClick(it,position,ReviewDetailList.get(position).review_id)
//
//            var toMoveCommentActivity : Intent = Intent(holder.commentBtn.context, ReviewCommentActivity::class.java)
//            toMoveCommentActivity.putExtra("review_id", ReviewDetailList.get(position).review_id)
//            ContextCompat.startActivity(
//                holder.commentBtn.context as Activity,
//                toMoveCommentActivity,
//                null
//            )
//
//
//        })





    }

    fun addItem(prof: ReviewDetailViewRvDataItem){

        ReviewDetailList.add(prof)
        notifyDataSetChanged()

    }

    fun removeItem(position : Int){

        notifyItemRemoved(position)
        notifyItemRangeChanged(0,ReviewDetailList.size-1)
        ReviewDetailList.removeAt(position)

    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        //프로필 이미지
        val profileSearchIv = itemView.findViewById<ImageView>(R.id.profileSearchIv)
        //닉네임
        val nicNameSearchTv = itemView.findViewById<TextView>(R.id.nicNameSearchTv)
        //리뷰 작성일
        val writingDateSearchTv = itemView.findViewById<TextView>(R.id.writingDateSearchTv)
        //레스토랑 주소
        val restaurantAddressSearchTv = itemView.findViewById<TextView>(R.id.restaurantAddressSearchTv)
        //레스토랑 이름
        val restaurantNameSearchTv = itemView.findViewById<TextView>(R.id.restaurantNameSearchTv)

        var searchImageIv = itemView.findViewById<ImageView>(R.id.searchImageIv)


        //리뷰 작성 내용
        val reviewDescriptionTv = itemView.findViewById<TextView>(R.id.reviewDescriptionTv)

        //좋아요 리니어레이아웃
        val likeBtn = itemView.findViewById<LinearLayout>(R.id.likeBtn)

        //좋아요 수
        val likeCountTv = itemView.findViewById<TextView>(R.id.likeCountTv)

        //좋아요 하트
        val heartIv = itemView.findViewById<ImageView>(R.id.heartIv)

        //댓글 수
        val commentCountTv = itemView.findViewById<TextView>(R.id.commentCountTv)

        //댓글 버튼
        val commentBtn = itemView.findViewById<LinearLayout>(R.id.commentBtn)

        val reviewClickBtn = itemView.findViewById<LinearLayout>(R.id.reviewClickBtn)

    }



    //클릭 시, 삭제된 리뷰인지 확인.
    fun reviewDeleteCheck(view: View,context: Context,review_tb_id:Int,position: Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.deleteReviewCheckInterface::class.java)
        val review_delete = api.review_delete_check(review_tb_id)


        review_delete.enqueue(object : Callback<ReviewDeleteCheckData> {
            override fun onResponse(
                call: Call<ReviewDeleteCheckData>,
                response: Response<ReviewDeleteCheckData>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                var items : ReviewDeleteCheckData? =  response.body()

                if (items != null) {
                    if(items.review_deleted == 1){


                        var builder = AlertDialog.Builder(context)
                        builder.setTitle("알림")
                        builder.setMessage("삭제된 리뷰입니다")

                        // 버튼 클릭시에 무슨 작업을 할 것인가!
                        var listener = object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                when (p1) {
                                    DialogInterface.BUTTON_POSITIVE ->{
                                        Log.d("TAG", "닫기버튼 클릭")

                                        removeItem(position)
                                    }



                                }
                            }
                        }

                        builder.setPositiveButton("확인", listener)

                        builder.show()

                    }else{



                        var toMoveCommentActivity : Intent = Intent(context, ReviewCommentActivity::class.java)
                        toMoveCommentActivity.putExtra("review_id", ReviewDetailList.get(position).review_id)
                        startActivity(
                            context as Activity,
                            toMoveCommentActivity,
                            null
                        )
                    }

                }





            }

            override fun onFailure(call: Call<ReviewDeleteCheckData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }





//    fun RvAdapterReviewLikeBtnClick(what_click_review_tb_id:Int,position : Int, context: Context){
//        val retrofit = Retrofit.Builder()
//            .baseUrl(context.getString(R.string.http_request_base_url))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val api = retrofit.create(API.reviewLikeBtnClick::class.java)
//        val review_Like_Btn_Click = api.review_Like_Btn_Click(what_click_review_tb_id,
//            MainActivity.user_table_id,
//            MainActivity.loginUserId)
//
//
//        review_Like_Btn_Click.enqueue(object : Callback<ReviewLikeBtnClickData> {
//            override fun onResponse(
//                call: Call<ReviewLikeBtnClickData>,
//                response: Response<ReviewLikeBtnClickData>
//            ) {
//                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
//                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")
//
//                if(response.body() != null) {
//                    val ReviewLikeBtnClickData: ReviewLikeBtnClickData = response.body()!!
//
//                    var heart_making = ReviewLikeBtnClickData.heart_making
//                    var how_many_like_count: Int = ReviewLikeBtnClickData.how_many_like_count
//                    var isSuccess: Boolean = ReviewLikeBtnClickData.success
//
//                    Log.d(ReviewFragment.TAG, "성공 현재 카운트 개수 : ${how_many_like_count}")
//                    Log.d(ReviewFragment.TAG, "성공 : ${isSuccess}")
//
//                    ReviewDetailList.get(position).like_count = how_many_like_count.toString()
//
//                    if(heart_making == true){
//                        ReviewDetailList.get(position).heart_making = true
//                        Log.d(ReviewFragment.TAG, "트루 : ${heart_making}")
//                    }else if(heart_making == false){
//                        ReviewDetailList.get(position).heart_making = false
//                        Log.d(ReviewFragment.TAG, "false : ${heart_making}")
//                    }
//
//                    notifyItemChanged(position)
//
//
//                }
//
//
//            }
//
//            override fun onFailure(call: Call<ReviewLikeBtnClickData>, t: Throwable) {
//                Log.d(ReviewFragment.TAG, "실패 : $t")
//            }
//        })
//    }

}