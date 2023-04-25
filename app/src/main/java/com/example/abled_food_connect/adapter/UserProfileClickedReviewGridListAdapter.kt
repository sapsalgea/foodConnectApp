package com.example.abled_food_connect.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.abled_food_connect.R
import com.example.abled_food_connect.UserProfileClickedReviewGridListActivity
import com.example.abled_food_connect.UserProfileClickedReviewVerticalListActivity
import com.example.abled_food_connect.data.ReviewDetailViewRvDataItem
import com.example.abled_food_connect.data.UserProfileData
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfileClickedReviewGridListAdapter () : RecyclerView.Adapter<UserProfileClickedReviewGridListAdapter.CustromViewHolder>(){


    var reviewDetailViewRvDataArraylist = ArrayList<ReviewDetailViewRvDataItem>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_fragment_grid_view_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return reviewDetailViewRvDataArraylist.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {

        Glide.with(holder.reveiwPicture.context)
            .load(holder.reveiwPicture.context.getString(R.string.http_request_base_url)+reviewDetailViewRvDataArraylist.get(position).review_picture_0)
            .apply(RequestOptions().centerCrop())
            .override(700)
            .into(holder.reveiwPicture)


        holder.reveiwPicture.setOnClickListener(View.OnClickListener {


            reviewDeleteCheck(holder.reveiwPicture.context,reviewDetailViewRvDataArraylist.get(position).review_id,position)


        })



    }

    fun addItem(prof: ReviewDetailViewRvDataItem){

        reviewDetailViewRvDataArraylist.add(prof)
        notifyDataSetChanged()

    }


    fun removeItem(position : Int){
        Log.d("TAG", position.toString())


        notifyItemRemoved(position)
        notifyItemRangeChanged(0,reviewDetailViewRvDataArraylist.size-1)
        reviewDetailViewRvDataArraylist.removeAt(position)


    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reveiwPicture = itemView.findViewById<ImageView>(R.id.reveiwPicture)

    }



    //그리드뷰에서 클릭한 리뷰가 삭제된 리뷰인지 확인한다.
    fun reviewDeleteCheck(context: Context,review_id:Int,position: Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.deletedReviewCheckUserProfileClickedReviewGridListRvInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val user_profile_data_get = api.deleted_Review_Check_user_profile_clicked_review_grid_rv_list_get(review_id)


        user_profile_data_get.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.body().toString()}")

                var items : String? =  response.body()

                Log.d("TAG", items.toString())

                if(items == "true"){
                    //리뷰가 삭제됨을 알리는 다이얼로그가 뜬다.


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
                    //삭제되지 않았으므로 엑티비티 전환이 일어난다.


                    var toMoveVerticalListActivityIntent = Intent(context, UserProfileClickedReviewVerticalListActivity::class.java)

                    //그리드뷰의 어레이리스트를 버티컬뷰 어레이리스트로 사용할 수 있게 넘겨준다.
                    toMoveVerticalListActivityIntent.putExtra("reviewDetailViewRvDataArraylist", reviewDetailViewRvDataArraylist)

                    //몇번째 포지션 아이템을 선택했는가. 버티컬뷰로 이동했을때 해당 포지션의 스크롤로 이동한다.
                    toMoveVerticalListActivityIntent.putExtra("whatClickPositionInGridView", position)

                    //그리드뷰에서 클릭한 데이터인가?.
                    toMoveVerticalListActivityIntent.putExtra("isSentGridView", "yes")

                    startActivity(context,toMoveVerticalListActivityIntent,null)

                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }

}