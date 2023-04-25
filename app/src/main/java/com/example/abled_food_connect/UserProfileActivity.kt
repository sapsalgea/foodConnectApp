package com.example.abled_food_connect

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.abled_food_connect.adapter.userProfileRankingLatestThreeRvAdapter
import com.example.abled_food_connect.data.UserProfileData
import com.example.abled_food_connect.data.userProfileRankingLatestThreeData
import com.example.abled_food_connect.data.userProfileRankingLatestThreeDataItem
import com.example.abled_food_connect.databinding.ActivityUserProfileBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfileActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityUserProfileBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private var clicked_user_tb_id: Int = 0
    private lateinit var clicked_user_NicName: String
    private lateinit var clicked_user_ProfileImage: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityUserProfileBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음껏 xml 파일 내의 뷰 id 접근이 가능해집니다.


        setSupportActionBar(binding.userProfileToolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.userProfileToolbar.title = "프로필"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //어떤 유저를 선택했는지 이전엑티비티에서 유저 테이블 아이디를 받아온다.
        clicked_user_tb_id = intent.getIntExtra("writer_user_tb_id", 0)

        Log.d("clicked_user_tb_id값", clicked_user_tb_id.toString())

        //유저 정보를 가져온다.
        userProfileLoading(clicked_user_tb_id)
        RankingLatestThreeLoading(clicked_user_tb_id)


        binding.rankingLatestRv.layoutManager =
            LinearLayoutManager(this).also { it.orientation = LinearLayoutManager.HORIZONTAL }
        binding.rankingLatestRv.setHasFixedSize(true)




        if (clicked_user_tb_id == MainActivity.user_table_id) {
            binding.toMoveDirectMessageActivityBtn.visibility = View.GONE
        }



        binding.toMoveDirectMessageActivityBtn.setOnClickListener(View.OnClickListener {
            var toDirectMessageActivity: Intent =
                Intent(applicationContext, DirectMessageActivity::class.java)
            toDirectMessageActivity.putExtra("writer_user_tb_id", clicked_user_tb_id)
            toDirectMessageActivity.putExtra("clicked_user_NicName", clicked_user_NicName)
            toDirectMessageActivity.putExtra("clicked_user_ProfileImage", clicked_user_ProfileImage)
            startActivity(toDirectMessageActivity, null)
        })



        binding.toMoveUserProfileBadgeListActivityBtn.setOnClickListener(View.OnClickListener {
            var toUserProfileBadgeListActivity: Intent =
                Intent(applicationContext, UserProfileBadgeListActivity::class.java)
            toUserProfileBadgeListActivity.putExtra("user_tb_id", clicked_user_tb_id)
            toUserProfileBadgeListActivity.putExtra("user_nicname", clicked_user_NicName)
            startActivity(toUserProfileBadgeListActivity, null)
        })


        binding.toMoveWrittenReviewListActivityBtn.setOnClickListener(View.OnClickListener {
            var toUserProfileClickedReviewGridListActivity: Intent =
                Intent(applicationContext, UserProfileClickedReviewGridListActivity::class.java)
            toUserProfileClickedReviewGridListActivity.putExtra(
                "writer_user_tb_id",
                clicked_user_tb_id
            )
            startActivity(toUserProfileClickedReviewGridListActivity, null)
        })


        binding.userHistoryBtn.setOnClickListener(View.OnClickListener {
            var toUserProfileJoinHistoryActivityIntent: Intent =
                Intent(applicationContext, UserProfileJoinHistoryActivity::class.java)
            toUserProfileJoinHistoryActivityIntent.putExtra("UserNicName", clicked_user_NicName)
            startActivity(toUserProfileJoinHistoryActivityIntent, null)
        })




        binding.toMoveUserProfileEvaluationListActivityBtn.setOnClickListener(View.OnClickListener {
            var toUserProfileEvaluationListActivity: Intent =
                Intent(applicationContext, UserProfileEvaluationListActivity::class.java)
            toUserProfileEvaluationListActivity.putExtra("user_tb_id", clicked_user_tb_id)
            toUserProfileEvaluationListActivity.putExtra("user_nicname", clicked_user_NicName)
            startActivity(toUserProfileEvaluationListActivity, null)
        })

    }


    fun RankingLatestThreeLoading(user_tb_id: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.rankingLatestThreeGetInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val ranking_get = api.ranking_latest_three_get(user_tb_id)


        ranking_get.enqueue(object : Callback<userProfileRankingLatestThreeData> {
            override fun onResponse(
                call: Call<userProfileRankingLatestThreeData>,
                response: Response<userProfileRankingLatestThreeData>
            ) {

                Log.d("최근랭킹 3개", "리뷰 컨텐츠 : ${response.raw()}")
                Log.d("최근 랭킹 3개", "리뷰 컨텐츠 : ${response.body().toString()}")

                var items: userProfileRankingLatestThreeData? = response.body()

                if (items != null) {

                    var RankingLatestThreeArrayList =
                        ArrayList<userProfileRankingLatestThreeDataItem>()
                    RankingLatestThreeArrayList =
                        items.RankingLatestThreeList as ArrayList<userProfileRankingLatestThreeDataItem>

                    val mAdapter =
                        userProfileRankingLatestThreeRvAdapter(RankingLatestThreeArrayList)
                    binding.rankingLatestRv.adapter = mAdapter
                }


            }

            override fun onFailure(call: Call<userProfileRankingLatestThreeData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }


    fun userProfileLoading(user_tb_id: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.UserProfileDataInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val user_profile_data_get = api.user_profile_data_get(user_tb_id)


        user_profile_data_get.enqueue(object : Callback<UserProfileData> {
            override fun onResponse(
                call: Call<UserProfileData>,
                response: Response<UserProfileData>
            ) {
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.body().toString()}")

                var items: UserProfileData? = response.body()


                if(items!!.account_delete == 1){

                    binding.whenAccountNotDeletedLinearLayout.visibility = View.GONE
                    binding.whenAccountDeletedLinearLayout.visibility = View.VISIBLE

                }else{

                    binding.whenAccountNotDeletedLinearLayout.visibility = View.VISIBLE


                    //작성자 프로필
                    Glide.with(applicationContext)
                        .load(getString(R.string.http_request_base_url) + items!!.profile_image)
                        .circleCrop()
                        .into(binding.userProfileIv)

                    clicked_user_ProfileImage = items!!.profile_image

                    binding.userProfileNicNameTv.text = items!!.nick_name
                    clicked_user_NicName = items!!.nick_name


                    if (items!!.introduction == null || items!!.introduction.length == 0) {
                        binding.userProfileIntroductionTv.text = "안녕하세요. ${items!!.nick_name}입니다."
                    } else {
                        binding.userProfileIntroductionTv.text = items.introduction
                    }

                    binding.reviewTitleAndReviewCountTv.text = "작성한 리뷰 ${items.review_count}개"

                    //랭킹관련

                    binding.rankTv.text = "(${items.rank}위)"


                    binding.tierTv.text = "${items.tier}"
                    binding.rankingPointTv.text = "${items.rank_point}PT"


                    Glide.with(applicationContext)
                        .load(getString(R.string.http_request_base_url) + items!!.tier_image)
                        .into(binding.tierBadgeImageIv)


                    binding.userProfileNicNameTv.text = items!!.nick_name
                    if(items.no_show_count >=3){
                        binding.noShowTv.visibility = View.VISIBLE
                        binding.noShowTv.text="노쇼:${items.no_show_count}회"
                        binding.noShowTv.background = resources.getDrawable(R.drawable.social_login_google_button)
                        binding.noShowTv.setTextColor(Color.WHITE)
                    }else if(items.no_show_count == 2){
                        binding.noShowTv.visibility = View.VISIBLE
                        binding.noShowTv.text="노쇼:${items.no_show_count}회"
                    }else{
                        binding.noShowTv.visibility = View.GONE
                    }

                }




            }

            override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}