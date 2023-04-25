package com.example.abled_food_connect

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abled_food_connect.adapter.MeetingUserEvaluationRvAdapter
import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.databinding.ActivityMeetingUserEvaluationBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MeetingUserEvaluationActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityMeetingUserEvaluationBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private var room_id : Int = 0

    lateinit var meetingEndUserListRv :RecyclerView

    var userList =  ArrayList<MeetingEvaluationUserListRvDataItem>()

    //버튼을 여러번 누르면, 누른만큼 전송이 된다. 따라서 연속클릭을 방지하기위한 변수를 만들었다.
    var clickedBtnCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_user_evaluation)

        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityMeetingUserEvaluationBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.


        setSupportActionBar(binding.Toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.Toolbar.title = "모임원평가"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        meetingEndUserListRv = binding.meetingEndUserListRv
        meetingEndUserListRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        meetingEndUserListRv.setHasFixedSize(false)


        room_id = intent.getIntExtra("room_id", 0)

        Log.d("room_id", room_id.toString())

        MeetingEnduserLoading(room_id)

        binding.sendReviewBtn.setOnClickListener(View.OnClickListener {

            var isNull = false
            for(i in 0..userList.size-1){
                if(userList.get(i).user_evaluation_what_did_you_say == null || userList.get(i).user_evaluation_what_did_you_say.length == 0){
                    isNull = true
                }
            }

            if (isNull ==true){
                Toast.makeText(applicationContext, "평가가 완료되지 않은 모임원이 있습니다.", Toast.LENGTH_SHORT).show()
            }

            else if(isNull == false){


                //clickedBtnCount은 여러번 클릭하는 것을 방지하기 위함이다.
                if(clickedBtnCount == 0){

                    clickedBtnCount = clickedBtnCount+1

                    var makeGson = GsonBuilder().create()

                    try {
                        // 제이슨으로 변환

                        val userListJson = Gson().toJsonTree(userList, object : TypeToken<ArrayList<MeetingEvaluationUserListRvDataItem>>(){}.type)

                        Log.d("평가완료", userListJson.toString())


                        //버튼을 클릭한 유저가 노쇼상태인지 아닌지 확인한다.
                        val retrofit = Retrofit.Builder()
                            .baseUrl(getString(R.string.http_request_base_url))
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                        val api = retrofit.create(API.MeetingUserEvaluationWritingBeforeNoShowCheck::class.java)

                        val meeting_user_evaluation_writing_before_no_show_check = api.meeting_user_evaluation_writing_before_no_show_check(room_id,MainActivity.user_table_id)


                        meeting_user_evaluation_writing_before_no_show_check.enqueue(object : Callback<MeetingUserEvaluationWritingBeforeNoShowCheckData> {

                            override fun onResponse(
                                call: Call<MeetingUserEvaluationWritingBeforeNoShowCheckData>,
                                response: Response<MeetingUserEvaluationWritingBeforeNoShowCheckData>
                            ) {
                                Log.d("노쇼유저인가?", "리뷰 컨텐츠 : ${response.raw()}")
                                Log.d("노쇼유저인가?", "가져온값 : ${response.body().toString()}")

                                var items : MeetingUserEvaluationWritingBeforeNoShowCheckData? =  response.body()

                                //노쇼라면 토스트메시지를, 노쇼가 아니라면 리뷰 제출을 한다.
                                if (items != null) {
                                    if(items.isNoShow == 1){
                                        Toast.makeText(applicationContext, "노쇼한 유저는 모임원 평가를 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                                    }else{
                                        MeetingUserEvaluationWriting(userListJson.toString())
                                    }
                                }

                            }

                            override fun onFailure(call: Call<MeetingUserEvaluationWritingBeforeNoShowCheckData>, t: Throwable) {
                                Log.d(ReviewFragment.TAG, "실패 : $t")

                            }
                        })









                        

                    }catch (e : JSONException){
                    }
                }


            }


        })



    }


    fun MeetingEnduserLoading(room_id:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.MeetingEvaluationUserListRvInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val user_profile_data_get = api.meeting_evaluation_user_list_get(room_id)


        user_profile_data_get.enqueue(object : Callback<MeetingEvaluationUserListRvData> {
            override fun onResponse(
                call: Call<MeetingEvaluationUserListRvData>,
                response: Response<MeetingEvaluationUserListRvData>
            ) {
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.body().toString()}")

                var items : MeetingEvaluationUserListRvData? =  response.body()

                var dbGetUserList = items!!.userList as ArrayList<MeetingEvaluationUserListRvDataItem>


                //호스트 게스트 정렬 호스트인 경우 맨 위에 뜰 수 있게 한다.
                dbGetUserList.sortWith(object: Comparator<MeetingEvaluationUserListRvDataItem>{
                    override fun compare(p1: MeetingEvaluationUserListRvDataItem, p2: MeetingEvaluationUserListRvDataItem): Int = when {
                        p1.is_host > p2.is_host -> -1
                        p1.is_host == p2.is_host -> 0
                        else -> 1
                    }
                })

                userList.clear()

                for(i in 0..dbGetUserList.size - 1){
                    if(dbGetUserList.get(i).user_nickname!=MainActivity.loginUserNickname){
                        userList.add(dbGetUserList.get(i))
                    }
                }




               var mAdapter =  MeetingUserEvaluationRvAdapter(userList)

                mAdapter.setItemClickListener(object: MeetingUserEvaluationRvAdapter.OnItemClickListener{
                    override fun onClick(v: View, position: Int, clickedText: String) {
                        userList[position].user_evaluation_what_did_you_say = clickedText
                        Log.d("TAG", userList.toString())
                    }
                })

                mAdapter.notifyDataSetChanged()
                meetingEndUserListRv.adapter = mAdapter




                //함께했던 모임원 모두가 계정탈퇴로 확인되면, 자동으로 모임원평가가 완료된다.
                var deleteAccoutCount = 0

                for(i in 0..userList.size - 1){
                    Log.d("deleteAccoutCount", userList.get(i).is_account_delete.toString())
                    if(userList.get(i).is_account_delete == 1){
                        deleteAccoutCount++

                        //평가 문구는 "선택안함"으로 정한다.
                        userList.get(i).user_evaluation_what_did_you_say="선택안함"
                    }
                }

                var alertText = ""

                if(userList.size == 1){
                    //탈퇴한 모임원이 1명 인 경우의 문구
                    alertText = "함께했던 모임원이 계정탈퇴를 했습니다.\n확인을 누르시면, 자동으로 평가가 완료됩니다."
                }else{
                    //탈퇴한 모임원이 복수일 경우의 문구
                    alertText = "함께했던 모임원 모두가 계정탈퇴를 했습니다.\n확인을 누르시면, 자동으로 평가가 완료됩니다."
                }

                if(deleteAccoutCount == userList.size){


                    var builder = AlertDialog.Builder(this@MeetingUserEvaluationActivity)
                    builder.setTitle("자동완료")
                    builder.setMessage(alertText)

                    // 버튼 클릭시에 무슨 작업을 할 것인가!
                    var listener = object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            when (p1) {
                                DialogInterface.BUTTON_POSITIVE ->{

                                    Log.d("TAG", userList.toString())
                                    binding.sendReviewBtn.performClick()

                                }


                            }
                        }
                    }

                    builder.setPositiveButton("확인", listener)
                    builder.show()




                }




            }

            override fun onFailure(call: Call<MeetingEvaluationUserListRvData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }



    fun MeetingUserEvaluationWriting(meeting_user_evaluation_Json:String){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.MeetingUserEvaluationWritingInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val meeting_user_evaluation_send = api.meeting_user_evaluation_writing(meeting_user_evaluation_Json,MainActivity.user_table_id,MainActivity.loginUserNickname)


        meeting_user_evaluation_send.enqueue(object : Callback<MeetingUserEvaluationWritingData> {

            override fun onResponse(
                call: Call<MeetingUserEvaluationWritingData>,
                response: Response<MeetingUserEvaluationWritingData>
            ) {
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "가져온값 : ${response.body().toString()}")


                val items: MeetingUserEvaluationWritingData = response.body()!!

                if(items.success == true){
                    Toast.makeText(applicationContext, "모임원 평가를 완료했습니다.\n"+"랭킹포인트 ${items.get_season_point}점을 획득하셨습니다.\n"+"현재 시즌 랭킹포인트 : ${items.now_season_total_rangking_point}", Toast.LENGTH_LONG).show()
//                Toast.makeText(applicationContext,
//                    "모임원 평가를 완료하였습니다.",
//                    Toast.LENGTH_SHORT).show()

                    onBackPressed()
                    finish()
                    //var items : MeetingEvaluationUserListRvData? =  response.body()
                }






            }

            override fun onFailure(call: Call<MeetingUserEvaluationWritingData>, t: Throwable) {
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