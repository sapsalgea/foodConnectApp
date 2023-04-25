package com.example.abled_food_connect

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.adapter.ReviewChildPageCommenRvAdapter
import com.example.abled_food_connect.data.ReviewChildPageCommentGetData
import com.example.abled_food_connect.data.ReviewChildPageCommentGetDataItem
import com.example.abled_food_connect.data.ReviewDeleteCheckData
import com.example.abled_food_connect.databinding.ActivityReviewCommentChildBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewCommentChildActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityReviewCommentChildBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    companion object {
        var review_id : Int = -1

        //groupNum 몇번째 부모에 속해있는 자식 코멘트인가.
        //db에 저장될때 자식 코멘트는 부모와 동일한 groupNum을 가진다.
        var groupNum : Int = 0
    }



    //리뷰작성자와, 댓글 작성자 모두에게 알림이 갈수 있도록 리뷰작성자 유저테이블id도 자식댓글엑티비티로 넘겨준다.
    var reviewWritingUserId : Int = 0

    private lateinit var comment_content : String
    private var comment_class : Int = 0
    private var sendTargetUserTable_id : Int = 0
    private lateinit var sendTargetUserNicName : String


    //리뷰 작성자 이름 - 댓글 목록에서 댓글 작성자와 리뷰 작성자가 일치하면 닉네임 옆에 [작성자]라는 표시가 뜬다
    private lateinit var writerNicname : String
    var WriterUserTbId : Int = 0


    //코멘트 리사이클러뷰
    lateinit var review_comment_rv_adapter: ReviewChildPageCommenRvAdapter
    var comment_ArrayList = ArrayList<ReviewChildPageCommentGetDataItem>()
    lateinit var reviewCommentRv : RecyclerView




    //부모로 등록되는 댓글인지, 자식으로 등록되는 댓글인지 구별
    //여기는 자식 댓글을 달기 때문에 무조건 1이다.
    var childOrParent : Int = 1





    //이전 엑티비티에서 답글달기 버튼을 누른경우
    var commentEtOpen : Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_review_comment_child)


        //키보드가 화면 안가리게함
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);




        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityReviewCommentChildBinding.inflate(layoutInflater)

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


        //인텐트를 통해 자식댓글을 보기 위한 데이터를 가져온다.
        customGetIntent()



        //삭제된 리뷰인지 확인
        reviewDeleteCheck(review_id)

        if(commentEtOpen == true){
            binding.childToCommentAlertTextBar.visibility = View.VISIBLE
            binding.childToCommentAlertTextBar.showKeyboard()
            binding.writingCommentEt.requestFocus()
        }

        //처음 자식 댓글 엑티비티에 들어왔을 때에는 부모 댓글 닉네임에게 댓글을 다는 것으로 설정해둔다.
        sendTargetUserTable_id = WriterUserTbId
        sendTargetUserNicName = writerNicname


        binding.tosendTargetUserNicName.text = sendTargetUserNicName


        CommentLoading(review_id,groupNum)





        //리사이클러뷰
        reviewCommentRv = binding.reviewCommentChildRv
        reviewCommentRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        //리사이클러뷰 구분선
        val dividerItemDecoration =
            DividerItemDecoration(reviewCommentRv.context, LinearLayoutManager(this).orientation)

        reviewCommentRv.addItemDecoration(dividerItemDecoration)
        reviewCommentRv.setHasFixedSize(false)




        binding.childCommentCloseBtn.setOnClickListener(View.OnClickListener {
            binding.childToCommentAlertTextBar.visibility = View.GONE


        })


        binding.writingCommentEt.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(view: View, hasFocus: Boolean) {
                if (hasFocus) {
                    binding.childToCommentAlertTextBar.visibility = View.VISIBLE
                } else {
                    //  .. 포커스 뺏겼을 때
                }
            }
        })

        binding.writingCommentEt.setOnClickListener(View.OnClickListener {
            binding.childToCommentAlertTextBar.visibility = View.VISIBLE
        })




        //댓글작성버튼 클릭
        binding.sendCommentBtn.setOnClickListener(View.OnClickListener {
            comment_content = binding.writingCommentEt.text.toString()

            binding.writingCommentEt.setText(binding.writingCommentEt.text.toString().replace(" ",""))
            if(binding.writingCommentEt.text.toString().length>0){


                CommentWritingBtnClick(review_id,comment_content,childOrParent,sendTargetUserTable_id,sendTargetUserNicName,groupNum)


                binding.sendCommentBtn.hideKeyboard()
                binding.childCommentCloseBtn.performClick()


                //댓글 내용을 입력했던 Edittext를 비워준다.
                binding.writingCommentEt.setText(null)

            }else{
                Toast.makeText(applicationContext, "답글을 입력해주세요.", Toast.LENGTH_LONG).show()
            }


        })

    }




    //클릭 시, 삭제된 리뷰인지 확인.
    fun reviewDeleteCheck(review_id:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.deleteReviewCheckInterface::class.java)
        val review_delete = api.review_delete_check(review_id)


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

                        onBackPressed()
                        finish()

                    }else{


                    }

                }





            }

            override fun onFailure(call: Call<ReviewDeleteCheckData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }







    //댓글 목록 불러오기
    fun CommentLoading(review_id:Int, groupNum: Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.reviewChildPageCommentListGet::class.java)
        val reviewCommentListGet = api.reviewChildPageCommentListGetCalling(review_id,groupNum)
        reviewCommentListGet.enqueue(object : Callback<ReviewChildPageCommentGetData> {
            override fun onResponse(
                call: Call<ReviewChildPageCommentGetData>,
                response: Response<ReviewChildPageCommentGetData>
            ) {

                if(response.body() != null) {
                    val reviewChildPageCommentGetData: ReviewChildPageCommentGetData = response.body()!!
                    var isSuccess: Boolean = reviewChildPageCommentGetData.success

                    comment_ArrayList = reviewChildPageCommentGetData.childPageCommentList as ArrayList<ReviewChildPageCommentGetDataItem>

                    Log.d(ReviewFragment.TAG, "자식목록불러와 ${reviewChildPageCommentGetData.childPageCommentList}")
                    Log.d(ReviewFragment.TAG, "자식목록불러와 : ${isSuccess}")

                    if(comment_ArrayList.size == 0){

                        var builder = AlertDialog.Builder(this@ReviewCommentChildActivity)
                        builder.setTitle("알림")
                        builder.setMessage("삭제된 댓글입니다.")

                        // 버튼 클릭시에 무슨 작업을 할 것인가!
                        var listener = object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                when (p1) {
                                    DialogInterface.BUTTON_POSITIVE ->{
                                        onBackPressed()
                                    }

                                }
                            }
                        }

                        builder.setPositiveButton("닫기", listener)
//

                        builder.show()
                    }else{

                        review_comment_rv_adapter =  ReviewChildPageCommenRvAdapter(comment_ArrayList,writerNicname,this@ReviewCommentChildActivity)
                        review_comment_rv_adapter.notifyDataSetChanged()
                        reviewCommentRv.adapter = review_comment_rv_adapter

                        //클릭리스너 등록
                        review_comment_rv_adapter.setItemClickListener( object : ReviewChildPageCommenRvAdapter.ItemClickListener{
                            override fun onClick(view: View, commentWriterUserTbId: Int, commentWriterUserNicname: String, childOrParent : Int, groupNum : Int) {
                                binding.childToCommentAlertTextBar.visibility = View.VISIBLE
                                binding.tosendTargetUserNicName.text = commentWriterUserNicname
                                binding.writingCommentEt.requestFocus()
                                view.hideKeyboard()
                                view.showKeyboard()

                                //답글달기 버튼을 누르면 해당 댓글작성자의 tb_id, 닉네임, 그룹넘버를 가져온다.
                                this@ReviewCommentChildActivity.sendTargetUserTable_id = commentWriterUserTbId
                                this@ReviewCommentChildActivity.sendTargetUserNicName = commentWriterUserNicname
                                ReviewCommentChildActivity.groupNum = groupNum

                                //1일 경우 자식 댓글(코멘트로 등록됨)
                                this@ReviewCommentChildActivity.childOrParent = 1

                            }
                        })
                    }





                }


            }

            override fun onFailure(call: Call<ReviewChildPageCommentGetData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }




    //리뷰작성버튼클릭
    fun CommentWritingBtnClick(review_id:Int,comment:String,comment_class:Int,sendTargetUserTable_id:Int,sendTargetUserNicName:String,groupNum:Int){

        var which_text_choose = comment_ArrayList.get(0).comment_content
        var comment_writer_nicname = MainActivity.loginUserNickname


        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.reviewChildPageCommentWriting::class.java)
        val review_Like_Btn_Click = api.review_child_page_comment_send_btn_click(reviewWritingUserId,which_text_choose,comment_writer_nicname,review_id,MainActivity.user_table_id,comment,comment_class,sendTargetUserTable_id,sendTargetUserNicName,groupNum)
        review_Like_Btn_Click.enqueue(object : Callback<ReviewChildPageCommentGetData> {
            override fun onResponse(
                call: Call<ReviewChildPageCommentGetData>,
                response: Response<ReviewChildPageCommentGetData>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                if(response.body() != null) {
                    val reviewChildPageCommentGetData: ReviewChildPageCommentGetData = response.body()!!
                    var isSuccess: Boolean = reviewChildPageCommentGetData.success

                    var review_deleted = reviewChildPageCommentGetData.review_deleted



                    //만약 댓글을 달았는데, 리뷰가 삭제되었다면.
                    if(review_deleted == 1){
                        onBackPressed()
                        finish()
                    }else{



                        comment_ArrayList = reviewChildPageCommentGetData.childPageCommentList as ArrayList<ReviewChildPageCommentGetDataItem>


                        //부모 댓글이 있는지 확인한다.
                        var Iscomment_order_zero = 0
                        for(i in 0..comment_ArrayList.size-1){

                            if(comment_ArrayList.get(0).comment_order ==0){
                                Iscomment_order_zero = 1
                            }
                        }

                        Log.d("제로값", Iscomment_order_zero.toString())

                        //부모댓글이 없으면, 댓글을 남길 수 없다는 창이 뜬다.
                        if(Iscomment_order_zero == 0){


                            var builder = androidx.appcompat.app.AlertDialog.Builder(this@ReviewCommentChildActivity)
                            builder.setTitle("알림")
                            builder.setMessage("대댓글을 남길 수 없습니다.\n"+"댓글 작성자가 댓글을 삭제하셨습니다.")

                            // 버튼 클릭시에 무슨 작업을 할 것인가!
                            var listener = object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    when (p1) {
                                        DialogInterface.BUTTON_POSITIVE ->{
                                            onBackPressed()
                                        }


                                    }
                                }
                            }

                            builder.setPositiveButton("확인", listener)

                            builder.show()

                        }else{
                            Log.d(ReviewFragment.TAG, "목록불러와 ${reviewChildPageCommentGetData.childPageCommentList}")
                            Log.d(ReviewFragment.TAG, "목록불러와 : ${isSuccess}")
                            Log.d(ReviewFragment.TAG, "목록불러와 : ${reviewChildPageCommentGetData.comment_count}")



                            review_comment_rv_adapter =  ReviewChildPageCommenRvAdapter(comment_ArrayList,writerNicname,this@ReviewCommentChildActivity)
                            review_comment_rv_adapter.notifyDataSetChanged()
                            reviewCommentRv.adapter = review_comment_rv_adapter



                            //클릭리스너 등록
                            review_comment_rv_adapter.setItemClickListener( object : ReviewChildPageCommenRvAdapter.ItemClickListener{
                                override fun onClick(view: View, commentWriterUserTbId: Int, commentWriterUserNicname: String, childOrParent : Int, groupNum:Int) {
                                    binding.childToCommentAlertTextBar.visibility = View.VISIBLE
                                    binding.tosendTargetUserNicName.text = commentWriterUserNicname
                                    binding.writingCommentEt.requestFocus()
                                    view.hideKeyboard()
                                    view.showKeyboard()

                                    this@ReviewCommentChildActivity.sendTargetUserTable_id = commentWriterUserTbId
                                    this@ReviewCommentChildActivity.sendTargetUserNicName = commentWriterUserNicname
                                    ReviewCommentChildActivity.groupNum = groupNum

                                    //1일 경우 자식 댓글(코멘트로 등록됨)
                                    this@ReviewCommentChildActivity.childOrParent = 1

                                }
                            })

                            Handler().postDelayed(Runnable {
                                //댓글엑티비티에오면 스크롤을 댓글창이 보이게 맞춰준다.
                                binding.childNestedSv.scrollTo(0, reviewCommentRv.bottom)
                            }, 500)
                        }

                    }











                }


            }

            override fun onFailure(call: Call<ReviewChildPageCommentGetData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }




    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun View.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }



    //fcm 관련 메서드

    //본 엑티비티를 보고있는데, 새로운 인텐트가 오면 알림.
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //FCM에 DM 메시지일 경우.
        customGetIntent()

    }


    fun customGetIntent(){

        //MainActivity 컴페오에 데이터가 없으면 쉐어드에서 불러온다.
        if(MainActivity.loginUserNickname.length == 0){
            sharedLoadData()
        }


        //FCM DM메시지 처리
        if (intent!!.getBooleanExtra("isChildComment",false) == true) {

            reviewWritingUserId = intent.getIntExtra("reviewWritingUserId",0)
            review_id = intent.getIntExtra("review_id",0)
            groupNum = intent.getIntExtra("groupNum",0)
            WriterUserTbId = intent.getIntExtra("sendTargetUserTable_id",0)
            writerNicname = intent.getStringExtra("sendTargetUserNicName").toString()
            commentEtOpen = intent.getBooleanExtra("commentEtOpen",false)

            Log.d("reviewWritingUserId", "fcm값뭐야"+reviewWritingUserId.toString())
            Log.d("WriterUserTbId", "WriterUserTbId값뭐야"+WriterUserTbId.toString())
            Log.d("writerNicname", "writerNicname값뭐야"+writerNicname.toString())


        }else{
            //부모엑티비티에서 넘어 온 경우
            reviewWritingUserId = intent.getIntExtra("reviewWritingUserId",0)
            review_id = intent.getIntExtra("review_id",0)
            groupNum = intent.getIntExtra("groupNum",0)
            WriterUserTbId = intent.getIntExtra("parent_comment_writing_user_id",0)
            writerNicname = intent.getStringExtra("parent_comment_writing_user_nicname").toString()
            commentEtOpen = intent.getBooleanExtra("commentEtOpen",false)

            Log.d("reviewWritingUserId", "fcm값뭐야"+reviewWritingUserId.toString())
        }
    }


    private fun sharedLoadData() {
        val pref = getSharedPreferences("pref_user_data", 0)
        MainActivity.user_table_id = pref.getInt("user_table_id", 0)
        MainActivity.loginUserId = pref.getString("loginUserId", "")!!
        MainActivity.loginUserNickname = pref.getString("loginUserNickname", "")!!
        MainActivity.userThumbnailImage = pref.getString("userThumbnailImage", "")!!
        MainActivity.userAge = pref.getInt("userAge",0)
        MainActivity.userGender = pref.getString("userGender","")!!
        MainActivity.ranking_explanation_check = pref.getInt("ranking_explanation_check",0)
    }


    //액션버튼 메뉴 액션바에 집어 넣기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.review_comment_child_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {

            R.id.refreshChildCommentBtn -> {
                Toast.makeText(applicationContext, "댓글 새로고침을 눌렀습니다.", Toast.LENGTH_SHORT).show()
                CommentLoading(review_id,groupNum)
            }

            android.R.id.home -> {
                finish()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
}