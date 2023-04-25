package com.example.abled_food_connect.adapter

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.abled_food_connect.*
import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import me.relex.circleindicator.CircleIndicator3
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfileClickedReviewVerticalListAdapter () : RecyclerView.Adapter<UserProfileClickedReviewVerticalListAdapter.CustromViewHolder>(){


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

        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_detail_view_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return ReviewDetailList.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {




        //프로필 이미지
        Glide.with(holder.profileDetailIv.context)
            .load(holder.profileDetailIv.context.getString(R.string.http_request_base_url)+ReviewDetailList.get(position).profile_image)
            .circleCrop()
            .into(holder.profileDetailIv)

        //프로필 이미지 클릭시, 프로필 사용자의 리뷰 리스트 엑티비티로 이동
        holder.profileDetailIv.setOnClickListener(View.OnClickListener {

            itemClickListner.onClick(it,position,ReviewDetailList.get(position).review_id)

            var toMoveUserProfileActivity : Intent = Intent(holder.profileDetailIv.context, UserProfileActivity::class.java)
            toMoveUserProfileActivity.putExtra("writer_user_tb_id", ReviewDetailList.get(position).writer_user_tb_id)
            startActivity(
                holder.profileDetailIv.context as Activity,
                toMoveUserProfileActivity,
                null
            )


        })

        //닉네임
        holder.nicNameDetailTv.text = ReviewDetailList.get(position).writer_nicname

        //닉네임 클릭시, 닉네임 사용자의 리뷰 리스트 엑티비티로 이동
        holder.nicNameDetailTv.setOnClickListener(View.OnClickListener {

            itemClickListner.onClick(it,position,ReviewDetailList.get(position).review_id)


            var toMoveUserProfileActivity : Intent = Intent(holder.profileDetailIv.context, UserProfileActivity::class.java)
            toMoveUserProfileActivity.putExtra("writer_user_tb_id", ReviewDetailList.get(position).writer_user_tb_id)
            startActivity(
                holder.profileDetailIv.context as Activity,
                toMoveUserProfileActivity,
                null
            )


        })


        //리뷰 작성일
        holder.writingDateDetailTv.text = ReviewDetailList.get(position).reporting_date
        //레스토랑 주소
        holder.restaurantAddressDetailTv.text = ReviewDetailList.get(position).restaurant_address
        //레스토랑 이름
        holder.restaurantNameDetailTv.text = ReviewDetailList.get(position).restaurant_name
        //리뷰 후기 사진 이미지 스위쳐
        //holder.reviewPictureIs



        //리뷰 3dot 버튼

        if(ReviewDetailList.get(position).writer_user_tb_id == MainActivity.user_table_id){
            holder.reviewDotBtn.visibility = View.VISIBLE

        }else{
            holder.reviewDotBtn.visibility = View.GONE
        }

        holder.reviewDotBtn.setOnClickListener(View.OnClickListener {


            var pop = PopupMenu(holder.reviewDotBtn.context,holder.reviewDotBtn)

            pop.menuInflater.inflate(R.menu.review_content_3_dot_menu, pop.menu)

            // 2. 람다식으로 처리
            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.reviewDeleteBtn ->{
                        var builder = AlertDialog.Builder(holder.reviewDotBtn.context)
                        builder.setTitle("리뷰삭제")
                        builder.setMessage("리뷰를 삭제하시겠습니까?\n" +
                                "(리뷰로 얻은 랭킹포인트가 차감됩니다.)")



                        // 버튼 클릭시에 무슨 작업을 할 것인가!
                        var listener = object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                when (p1) {
                                    DialogInterface.BUTTON_POSITIVE ->{
                                        Log.d("TAG", "닫기버튼 클릭")
                                    }

                                    DialogInterface.BUTTON_NEGATIVE ->{

                                        reviewDeleteBtnClick(holder.reviewDotBtn.context,ReviewDetailList.get(position).review_id,ReviewDetailList.get(position).room_tb_id,position)

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




        var imagesList = mutableListOf<String>()
        imagesList.clear()

        imagesList.add(ReviewDetailList.get(position).review_picture_0)
        if(ReviewDetailList.get(position).review_picture_1 !=""){
            imagesList.add(ReviewDetailList.get(position).review_picture_1)
        }

        if(ReviewDetailList.get(position).review_picture_2 !=""){
            imagesList.add(ReviewDetailList.get(position).review_picture_2)
        }

        holder.view_pager2.adapter = Review_Detail_ViewPagerAdapter(imagesList)
        holder.view_pager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        holder.indicator.setViewPager(holder.view_pager2)
        if(imagesList.size>1) {
            holder.indicator.visibility = View.VISIBLE
            Log.d("보여준다", imagesList.size.toString())

        }

        if(imagesList.size==1) {
            holder.indicator.visibility = View.GONE
            Log.d("보여준다", imagesList.size.toString())

        }

//        별점 삭제
//        //맛 평가 별점
//        holder.ratingStarTasteDetailTv.text = ReviewDetailList.get(position).rating_star_taste.toString()
//
//        //서비스 평가 별점
//        holder.ratingStarServiceDetailTv.text = ReviewDetailList.get(position).rating_star_service.toString()
//
//        //위생 평가 별점
//        holder.ratingStarCleanDetailTv.text = ReviewDetailList.get(position).rating_star_clean.toString()
//
//        //인테리어 평가 별점
//        holder.ratingStarInteriorDetailTv.text =ReviewDetailList.get(position).rating_star_interior.toString()

        //리뷰 작성 내용
        holder.reviewDescriptionTv.text = ReviewDetailList.get(position).review_description


        //좋아요 리니어 레이아웃 좋아요 버튼을 클릭
        holder.likeBtn.setOnClickListener(View.OnClickListener {



            //처음 좋아요를 누르는 경우, 다이얼로그가 나와서 좋아요를 누르시겠습니까? 문구 나오게한다.
            if(ReviewDetailList.get(position).heart_making == false){
                var builder = AlertDialog.Builder(holder.likeBtn.context)
                builder.setTitle("알림")
                builder.setMessage("좋아요를 누르시겠습니까?\n" +
                        "(좋아요 버튼을 누르면 취소가 불가능합니다.)")

                // 버튼 클릭시에 무슨 작업을 할 것인가!
                var listener = object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when (p1) {
                            DialogInterface.BUTTON_POSITIVE ->{
                                Log.d("TAG", "닫기버튼 클릭")
                            }

                            DialogInterface.BUTTON_NEGATIVE ->{

                                RvAdapterReviewLikeBtnClick(ReviewDetailList.get(position).writer_user_tb_id,ReviewDetailList.get(position).review_description,ReviewDetailList.get(position).review_id,position,holder.likeBtn.context)

                                Log.d("무엇", ReviewDetailList.get(position).review_id.toString()+ MainActivity.user_table_id+ MainActivity.loginUserId)
                            }

                        }
                    }
                }

                builder.setPositiveButton("닫기", listener)
                builder.setNegativeButton("좋아요", listener)
                builder.show()


            }else{
                RvAdapterReviewLikeBtnClick(ReviewDetailList.get(position).writer_user_tb_id,ReviewDetailList.get(position).review_description,ReviewDetailList.get(position).review_id,position,holder.likeBtn.context)

                Log.d("무엇", ReviewDetailList.get(position).review_id.toString()+ MainActivity.user_table_id+ MainActivity.loginUserId)
            }



        })

        //좋아요 개수
        holder.likeCountTv.text = ReviewDetailList.get(position).like_count

        //좋아요 하트 변경

        if(ReviewDetailList.get(position).heart_making == true) {
            holder.heartIv.setColorFilter(Color.parseColor("#77ff0000"))
        }else{
            holder.heartIv.setColorFilter(Color.parseColor("#55111111"))
        }


        //댓글 개수
        holder.commentCountTv.text = ReviewDetailList.get(position).comment_count

        //ReviewCommentActivity
        holder.commentBtn.setOnClickListener(View.OnClickListener {


            itemClickListner.onClick(it,position,ReviewDetailList.get(position).review_id)


            reviewDeleteCheck(holder.commentBtn.context,ReviewDetailList.get(position).review_id,position)



        })








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
        val profileDetailIv = itemView.findViewById<ImageView>(R.id.profileDetailIv)
        //닉네임
        val nicNameDetailTv = itemView.findViewById<TextView>(R.id.nicNameDetailTv)
        //리뷰 작성일
        val writingDateDetailTv = itemView.findViewById<TextView>(R.id.writingDateDetailTv)
        //레스토랑 주소
        val restaurantAddressDetailTv = itemView.findViewById<TextView>(R.id.restaurantAddressDetailTv)
        //레스토랑 이름
        val restaurantNameDetailTv = itemView.findViewById<TextView>(R.id.restaurantNameDetailTv)
        //리뷰 후기 사진 이미지 스위쳐
        val view_pager2 = itemView.findViewById<ViewPager2>(R.id.view_pager2)


        val indicator = itemView.findViewById<CircleIndicator3>(R.id.indicator)




//        별점 삭제
//        //맛 평가 별점
//        val ratingStarTasteDetailTv = itemView.findViewById<TextView>(R.id.ratingStarTasteDetailTv)
//
//        //서비스 평가 별점
//        val ratingStarServiceDetailTv = itemView.findViewById<TextView>(R.id.ratingStarServiceDetailTv)
//
//        //위생 평가 별점
//        val ratingStarCleanDetailTv = itemView.findViewById<TextView>(R.id.ratingStarCleanDetailTv)
//
//        //인테리어 평가 별점
//        val ratingStarInteriorDetailTv = itemView.findViewById<TextView>(R.id.ratingStarInteriorDetailTv)

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

        val reviewDotBtn = itemView.findViewById<ImageView>(R.id.reviewDotBtn)

    }



    //클릭 시, 삭제된 리뷰인지 확인.
    fun reviewDeleteCheck(context: Context,review_tb_id:Int,position: Int){
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


    fun reviewDeleteBtnClick(context: Context,what_click_review_tb_id:Int,room_id: Int,position: Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.reviewDeleteBtn::class.java)
        val review_Like_Btn_Click = api.review_delete_btn_click(MainActivity.user_table_id,what_click_review_tb_id,room_id)


        review_Like_Btn_Click.enqueue(object : Callback<ReviewDeleteData> {
            override fun onResponse(
                call: Call<ReviewDeleteData>,
                response: Response<ReviewDeleteData>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                if(response.body() != null) {
                    val items: ReviewDeleteData = response.body()!!

                    if(items.success ==true){
                        //Toast.makeText(context, "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        Toast.makeText(context, "리뷰가 삭제되었습니다.\n"+"랭킹포인트 ${items.minus_season_point}점이 감소하였습니다.\n"+"현재 시즌 랭킹포인트 : ${items.now_season_total_rangking_point}", Toast.LENGTH_LONG).show()
                        removeItem(position)
                    }else{
                        Log.d("false", "false")
                    }


                }


            }

            override fun onFailure(call: Call<ReviewDeleteData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }





    fun RvAdapterReviewLikeBtnClick(sendTargetUserTable_id:Int, which_text_choose:String,what_click_review_tb_id:Int,position : Int, context: Context){
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.reviewLikeBtnClick::class.java)
        val review_Like_Btn_Click = api.review_Like_Btn_Click(
            sendTargetUserTable_id,
            MainActivity.loginUserNickname,
            which_text_choose,
            what_click_review_tb_id,
            MainActivity.user_table_id,
            MainActivity.loginUserId)


        review_Like_Btn_Click.enqueue(object : Callback<ReviewLikeBtnClickData> {
            override fun onResponse(
                call: Call<ReviewLikeBtnClickData>,
                response: Response<ReviewLikeBtnClickData>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                if(response.body() != null) {

                    val ReviewLikeBtnClickData: ReviewLikeBtnClickData = response.body()!!

                    var heart_making = ReviewLikeBtnClickData.heart_making
                    var how_many_like_count: Int = ReviewLikeBtnClickData.how_many_like_count
                    var isSuccess: Boolean = ReviewLikeBtnClickData.success
                    var review_deleted: Int = ReviewLikeBtnClickData.review_deleted

                    if(review_deleted == 1){
                        //리뷰 삭제됬으면 리스트에서 삭제.




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

//                                DialogInterface.BUTTON_NEGATIVE ->{
//
//                                }

                                }
                            }
                        }

                        builder.setPositiveButton("확인", listener)
                        //builder.setNegativeButton("닫기", listener)

                        builder.show()

                    }else{

                        //리뷰 삭제 안됬으면, 갱신

                        Log.d(ReviewFragment.TAG, "성공 현재 카운트 개수 : ${how_many_like_count}")
                        Log.d(ReviewFragment.TAG, "성공 : ${isSuccess}")

                        ReviewDetailList.get(position).like_count = how_many_like_count.toString()

                        if(heart_making == true){
                            ReviewDetailList.get(position).heart_making = true
                            Log.d(ReviewFragment.TAG, "트루 : ${heart_making}")
                        }else if(heart_making == false){
                            ReviewDetailList.get(position).heart_making = false
                            Log.d(ReviewFragment.TAG, "false : ${heart_making}")
                        }


                        //만약 클릭한 하트가 한번 더 클릭했을때, 취소하고 싶으면 하단문장을 지워라.
                        var isDoubleLikeButtonClicked = ReviewLikeBtnClickData.isDoubleLikeButtonClicked
                        if(isDoubleLikeButtonClicked == true){
                            var builder = AlertDialog.Builder(context)
                            builder.setTitle("알림")
                            builder.setMessage("이미 좋아요를 클릭하셨습니다.")

                            // 버튼 클릭시에 무슨 작업을 할 것인가!
                            var listener = object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    when (p1) {
                                        DialogInterface.BUTTON_POSITIVE ->{
                                            Log.d("TAG", "닫기버튼 클릭")
                                        }

//                                        DialogInterface.BUTTON_NEGATIVE ->{
//
//                                            reviewDeleteBtnClick(review_id,room_id)
//
//                                        }

                                    }
                                }
                            }

                            builder.setPositiveButton("닫기", listener)
//                            builder.setNegativeButton("삭제", listener)

                            builder.show()
                        }

                        notifyItemChanged(position)
                    }




                }


            }

            override fun onFailure(call: Call<ReviewLikeBtnClickData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }

}