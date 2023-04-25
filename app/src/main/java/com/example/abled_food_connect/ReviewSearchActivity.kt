package com.example.abled_food_connect

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.adapter.ReviewSearchRvAdapter
import com.example.abled_food_connect.data.ReviewDetailViewLikeAndCommentCountCheckData
import com.example.abled_food_connect.data.ReviewDetailViewRvData
import com.example.abled_food_connect.data.ReviewDetailViewRvDataItem
import com.example.abled_food_connect.databinding.ActivityReviewSearchBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewSearchActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityReviewSearchBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    //리사이클러뷰 어래이리스트
    private var DetailRv_arrayList = ArrayList<ReviewDetailViewRvDataItem>()

    //리사이클러뷰
    lateinit var detail_rv : RecyclerView



    //리사이클러뷰 어댑터
    var mAdapter : ReviewSearchRvAdapter = ReviewSearchRvAdapter()


    private var whatClickPostion : Int = 0
    private var whatClickReviewId : Int = 0


    private var searchOptionNumber : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_search)

        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityReviewSearchBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)


        setSupportActionBar(binding.userProfileToolbar) //커스텀한 toolbar를 액션바로 사용
        //supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        //툴바에 백버튼 만들기
        supportActionBar?.setTitle("리뷰검색")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)





        //리사이클러뷰
        detail_rv = findViewById<RecyclerView>(R.id.review_Search_rv)
        detail_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        detail_rv.adapter = mAdapter

        detail_rv.setHasFixedSize(false)


        Log.d("테이블id", MainActivity.user_table_id.toString())
        Log.d("아이디", MainActivity.loginUserId)


        //리사이클러뷰 구분선
        val dividerItemDecoration =
            DividerItemDecoration(detail_rv.context, LinearLayoutManager(this).orientation)

        detail_rv.addItemDecoration(dividerItemDecoration)




        //팝업메뉴

        binding.optionBtn.setOnClickListener {
            var pop = PopupMenu(this,binding.optionNameTv)

            menuInflater.inflate(R.menu.review_search_menu, pop.menu)

            // 1. 리스너로 처리
            var listener = PopupListener()
            pop.setOnMenuItemClickListener(listener)

            // 2. 람다식으로 처리
            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.allOption -> {
                        binding.optionNameTv.text = "전체"
                        searchOptionNumber = 0
                    }
                    R.id.shopNameOption -> {
                        binding.optionNameTv.text = "가게명"
                        searchOptionNumber = 1
                    }
                    R.id.contentOption -> {
                        binding.optionNameTv.text = "내용"
                        searchOptionNumber= 2
                    }
                }
                false
            }
            pop.show()
        }


        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    reviewSearchLoading(binding.optionNameTv.text.toString(),query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }

    // 1. 리스너로 처리
    inner class PopupListener : PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(p0: MenuItem?): Boolean {
            when (p0?.itemId) {
                R.id.allOption -> {
                    binding.optionNameTv.text = "전체"
                    searchOptionNumber = 0
                }
                R.id.shopNameOption -> {
                    binding.optionNameTv.text = "가게명"
                    searchOptionNumber = 1
                }
                R.id.contentOption -> {
                    binding.optionNameTv.text = "내용"
                    searchOptionNumber = 2
                }
            }
            return false
        }

    }



    fun reviewSearchLoading(option_str:String, search_keyword:String){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.ReviewSearchRvInterface::class.java)
        val review_search_rv_using = api.review_search_rv_using_get(searchOptionNumber,search_keyword,MainActivity.user_table_id)


        review_search_rv_using.enqueue(object : Callback<ReviewDetailViewRvData> {
            override fun onResponse(
                call: Call<ReviewDetailViewRvData>,
                response: Response<ReviewDetailViewRvData>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                if(response.body() !=null){

                    var items : ReviewDetailViewRvData? =  response.body()

                    if(items!!.success == "true"){

                        DetailRv_arrayList.clear()

                        DetailRv_arrayList = items!!.roomList as ArrayList<ReviewDetailViewRvDataItem>

                        mAdapter.ReviewDetailList = DetailRv_arrayList
                        mAdapter.notifyDataSetChanged()


                        //클릭리스너 등록
                        mAdapter.setItemClickListener( object : ReviewSearchRvAdapter.ItemClickListener{
                            override fun onClick(view: View, position : Int, whatClickReviewId : Int) {
                                whatClickPostion = position
                                this@ReviewSearchActivity.whatClickReviewId = whatClickReviewId

                            }
                        })
                    }


                }





            }

            override fun onFailure(call: Call<ReviewDetailViewRvData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }





    override fun onRestart() {
        super.onRestart()
        Log.d("리스타트", whatClickPostion.toString())
        Log.d("리스타트", whatClickReviewId.toString())

        //리사이클러뷰를 갱신한다.리뷰를 보고 돌아왔을때, 좋아요를 클릭하거나 댓글을 달았다면, 숫자가 변경되어 있어야한다.
        ReviewDetailViewLikeAndCommentCountCheck(whatClickReviewId,applicationContext)

    }



    fun ReviewDetailViewLikeAndCommentCountCheck(what_click_review_tb_id:Int, context: Context){
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.reviewDetailViewLikeAndCommentCountCheck::class.java)
        val review_Like_And_CommentCount_Check = api.reviewDetailViewLikeAndCommentCountCheckGetCalling(what_click_review_tb_id,MainActivity.user_table_id)


        review_Like_And_CommentCount_Check.enqueue(object :
            Callback<ReviewDetailViewLikeAndCommentCountCheckData> {
            override fun onResponse(
                call: Call<ReviewDetailViewLikeAndCommentCountCheckData>,
                response: Response<ReviewDetailViewLikeAndCommentCountCheckData>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                if(response.body() != null) {
                    val reviewDetailViewLikeAndCommentCountCheckData: ReviewDetailViewLikeAndCommentCountCheckData = response.body()!!

                    var islikeClicked = reviewDetailViewLikeAndCommentCountCheckData.islikeClicked
                    var like_count = reviewDetailViewLikeAndCommentCountCheckData.like_count
                    var comment_count = reviewDetailViewLikeAndCommentCountCheckData.comment_count

                    var review_deleted  = reviewDetailViewLikeAndCommentCountCheckData.review_deleted

                    Log.d(ReviewFragment.TAG, "나오시오 : ${reviewDetailViewLikeAndCommentCountCheckData}")


                    if(review_deleted==1){

                        mAdapter.removeItem(whatClickPostion)

                    }else{

                        DetailRv_arrayList.get(whatClickPostion).heart_making = islikeClicked
                        DetailRv_arrayList.get(whatClickPostion).like_count = like_count.toString()
                        DetailRv_arrayList.get(whatClickPostion).comment_count = comment_count.toString()
                        mAdapter.notifyItemChanged(whatClickPostion)

                    }





                    //클릭리스너 등록
                    mAdapter.setItemClickListener( object : ReviewSearchRvAdapter.ItemClickListener{
                        override fun onClick(view: View, position : Int, whatClickReviewId : Int) {
                            whatClickPostion = position
                            this@ReviewSearchActivity.whatClickReviewId = whatClickReviewId

                        }
                    })








                }


            }

            override fun onFailure(call: Call<ReviewDetailViewLikeAndCommentCountCheckData>, t: Throwable) {
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

