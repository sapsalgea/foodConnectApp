package com.example.abled_food_connect

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.adapter.UserProfileClickedReviewGridListAdapter
import com.example.abled_food_connect.data.ReviewDetailViewRvData
import com.example.abled_food_connect.data.ReviewDetailViewRvDataItem
import com.example.abled_food_connect.databinding.ActivityUserProfileClickedReviewGridListBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.interfaces.ReviewDetailRvInterface
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfileClickedReviewGridListActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityUserProfileClickedReviewGridListBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    //프로필 클릭 당한 사람의 user_tb id를 받는 변수
    lateinit var clicked_user_tb_id : String

    //리사이클러뷰 어래이리스트
    private var GridRv_arrayList = ArrayList<ReviewDetailViewRvDataItem>()

    //리사이클러뷰
    lateinit var gridRv : RecyclerView


    //리사이클러뷰 어댑터
    var mAdapter : UserProfileClickedReviewGridListAdapter = UserProfileClickedReviewGridListAdapter()



    //처음엑티비티를 실행하는지, 아니면 다시 돌아온 것인지 체크한다.






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_clicked_review_grid_list)


        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityUserProfileClickedReviewGridListBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.
        // 뷰 id도 파스칼케이스 + 카멜케이스의 네이밍규칙 적용으로 인해서 tv_message -> tvMessage 로 자동 변환 되었습니다.

        setSupportActionBar(binding.Toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.Toolbar.title = "리뷰"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)




        clicked_user_tb_id = intent.getIntExtra("writer_user_tb_id",0).toString()
        reviewDbLoading(clicked_user_tb_id)




        gridRv = binding.UserProfileClickedReviewGridRv
        gridRv.layoutManager = GridLayoutManager(applicationContext,3)


        gridRv.adapter = mAdapter
        //리사이클러뷰 구분선


        gridRv.addItemDecoration(HorizontalItemDecorator(5))
        gridRv.addItemDecoration(VerticalItemDecorator(5))


        gridRv.setHasFixedSize(false)


    }


    override fun onRestart() {
        super.onRestart()
        reviewDbLoading(clicked_user_tb_id)
    }



    fun reviewDbLoading(clicked_user_tb_id:String){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.UserProfileClickedReviewGridListRvInterface::class.java)
        val user_profile_clicked_review_grid_rv_using = api.user_profile_clicked_review_grid_list_get(clicked_user_tb_id,MainActivity.user_table_id)


        user_profile_clicked_review_grid_rv_using.enqueue(object : Callback<ReviewDetailViewRvData> {
            override fun onResponse(
                call: Call<ReviewDetailViewRvData>,
                response: Response<ReviewDetailViewRvData>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                var items : ReviewDetailViewRvData? =  response.body()


                Log.d(ReviewFragment.TAG, "성공 : ${items!!.roomList}")
                GridRv_arrayList.clear()
                GridRv_arrayList = items!!.roomList as ArrayList<ReviewDetailViewRvDataItem>

                mAdapter.reviewDetailViewRvDataArraylist = GridRv_arrayList
                mAdapter.notifyDataSetChanged()




            }

            override fun onFailure(call: Call<ReviewDetailViewRvData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }






    class HorizontalItemDecorator(private val divHeight : Int) : RecyclerView.ItemDecoration() {

        @Override
        override fun getItemOffsets(outRect: Rect, view: View, parent : RecyclerView, state : RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.left = divHeight
            outRect.right = divHeight
        }
    }

    class VerticalItemDecorator(private val divHeight : Int) : RecyclerView.ItemDecoration() {

        @Override
        override fun getItemOffsets(outRect: Rect, view: View, parent : RecyclerView, state : RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.top = divHeight
            outRect.bottom = divHeight
        }
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