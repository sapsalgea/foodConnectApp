package com.example.abled_food_connect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.abled_food_connect.databinding.ActivityMainFragmentBinding
import com.example.abled_food_connect.fragments.*
import com.example.abled_food_connect.retrofit.RoomAPI
import com.example.abled_food_connect.works.DatetimeCheckWork
import com.example.abled_food_connect.works.ScheduleCheckWork
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class MainFragmentActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainFragmentBinding.inflate(layoutInflater) }
    private lateinit var mainFragment: MainFragment
    private lateinit var reviewFragment: ReviewFragment
    private lateinit var rankingFragment: RankingFragment
    private lateinit var chatingFragment: ChatingFragment
    private lateinit var myPageFragment: MyPageFragment
    private var BackPressTime: Long = 0
    private var context: Context = this


    //태그 생성
    companion object obuserid {
        const val TAG: String = "홈 액티비티 로그"


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d(TAG, "홈액티비티 onCreate()")

        val pref = getSharedPreferences("pref_user_data", 0)
        MainActivity.user_table_id = pref.getInt("user_table_id", 0)
        MainActivity.loginUserId = pref.getString("loginUserId", "")!!
        MainActivity.loginUserNickname = pref.getString("loginUserNickname", "")!!
        MainActivity.userThumbnailImage = pref.getString("userThumbnailImage", "")!!
        MainActivity.userAge = pref.getInt("userAge",0)
        MainActivity.userGender = pref.getString("userGender","")!!

        //바텀네비게이션 클릭리스너 달기
        binding.bottomNav.setOnNavigationItemSelectedListener(
            onBottomOnNavigationItemSelectedListener
        )
val s = mutableListOf<String>()

        Log.e(TAG, "onCreate: ${s.toString()}", )
        //툴바 생성 및 타이틀 이름
        setSupportActionBar(binding.maintoolbar)
        val tb = supportActionBar!!
        tb.setTitle("홈")
        tb.setDisplayHomeAsUpEnabled(true)
        //첫화면에서 리뷰 플로팅 버튼 숨김
        binding.mainFragmentCreateReviewBtn.hide()
        if (intent.hasExtra("FCMRoomId")) {

            binding.bottomNav.selectedItemId = R.id.menu_chat
            val roomid = intent.getStringExtra("FCMRoomId")
            val hostName = intent.getStringExtra("hostName")
            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("roomId", roomid)
            intent.putExtra("hostName",hostName)
            startActivity(intent)

        }else if(intent.hasExtra("finishedGroup")){
            binding.bottomNav.selectedItemId = R.id.menu_mypage
            val intent = Intent(this, UnwrittenReviewListActivity::class.java)

            startActivity(intent)
        }

        else if(intent.getBooleanExtra("isDM",false) == true){
            customGetIntent()
        }

        //부모댓글알림일경우
        else if (intent!!.getBooleanExtra("isParentComment",false) == true) {
            customGetIntent()
        }

        //자식댓글 알림일 경우
        else if (intent!!.getBooleanExtra("isChildComment",false) == true) {
            customGetIntent()
        }

        else {
            //프래그먼트 인스턴스화
            mainFragment = MainFragment.newInstance()

            //프래그먼트 매니저에 메인프래그먼트 등록
            supportFragmentManager.beginTransaction().setCustomAnimations(
                R.animator.fade_in,
                R.animator.fade_out,
                R.animator.fade_in,
                R.animator.fade_out
            ).add(R.id.view, mainFragment).commit()
        }

        //방만들기 플로팅 버튼 클릭리스너
        binding.mainFragmentCreateRoomBtn.setOnClickListener {
            moveToCreateRoomActivity()
//            resistRoom()
//            test()

        }
        binding.mainFragmentCreateReviewBtn.setOnClickListener(
            View.OnClickListener
            {
                //바텀네비게이션 프래그먼트 셀렉트 리스너
                val nextIntent = Intent(this, UnwrittenReviewListActivity::class.java)
                startActivity(nextIntent)

            })



    }


    override fun onStart() {
        super.onStart()
        doWorkWithPeriodic(this)
    }

    override fun onStop() {
        super.onStop()
        if (intent.hasExtra("finishedGroup")) {
            intent.removeExtra("finishedGroup")
        }
    }

    override fun onResume() {
        super.onResume()
        val manager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage("GPS가 꺼저 있습니다. 활성화를 해주세요.")
                .setNegativeButton("취소", null)
                .setPositiveButton(
                    "설정"
                ) { dialog, which ->
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .show()

        }
        token()
    }


    //본 엑티비티를 보고있는데, 새로운 인텐트가 오면 알림.
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //FCM에 DM 메시지일 경우.
        customGetIntent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                onBackPressed()
            }
            else->{}
        }

        return super.onOptionsItemSelected(item)

    }

    fun customGetIntent(){

        //MainActivity 컴페오에 데이터가 없으면 쉐어드에서 불러온다.
        if(MainActivity.loginUserNickname.length == 0){
            sharedLoadData()
        }


        //FCM DM메시지 처리
        if (intent!!.getBooleanExtra("isDM",false) == true) {
            var fromUserProfileImage = intent.getStringExtra("fromUserProfileImage")
            var fromUserNickname = intent.getStringExtra("fromUserNickname")
            var fromUserTbId = intent.getStringExtra("fromUserTbId")


            binding.bottomNav.selectedItemId = R.id.menu_chat

            //isGroupOrDm을 1로 바꾼다.
            //그룹채팅 0, DM은 1이다.
            ChatingFragment.isGroupOrDm = 1

            val DMintent = Intent(this, DirectMessageActivity::class.java)
            DMintent.putExtra("writer_user_tb_id", fromUserTbId!!.toInt())
            DMintent.putExtra("clicked_user_NicName", fromUserNickname)
            DMintent.putExtra("clicked_user_ProfileImage", fromUserProfileImage)
            startActivity(DMintent)

        }



        //FCM 부모댓글 알림처리
        else if (intent!!.getBooleanExtra("isParentComment",false) == true) {
            var review_id = intent.getIntExtra("review_id",0)


            binding.bottomNav.selectedItemId = R.id.menu_home


            val ParentCommentintent = Intent(this, ReviewCommentActivity::class.java)
            ParentCommentintent.putExtra("review_id", review_id)
            startActivity(ParentCommentintent)

        }


        //FCM 자식댓글 알림처리
        else if (intent!!.getBooleanExtra("isChildComment",false) == true) {
            var isChildComment = intent.getBooleanExtra("isChildComment",false)
            var reviewWritingUserId = intent.getIntExtra("reviewWritingUserId",0)
            var review_id = intent.getIntExtra("review_id",0)
            var groupNum = intent.getIntExtra("groupNum",0)
            var sendTargetUserTable_id = intent.getIntExtra("sendTargetUserTable_id",0)
            var sendTargetUserNicName = intent.getStringExtra("sendTargetUserNicName").toString()





            binding.bottomNav.selectedItemId = R.id.menu_home


            val ChildCommentintent = Intent(this, ReviewCommentActivity::class.java)
            ChildCommentintent.putExtra("isChildComment",isChildComment)
            ChildCommentintent.putExtra("review_id",review_id)
            ChildCommentintent.putExtra("groupNum",groupNum)
            ChildCommentintent.putExtra("sendTargetUserTable_id", sendTargetUserTable_id)
            ChildCommentintent.putExtra("sendTargetUserNicName",sendTargetUserNicName)
            ChildCommentintent.putExtra("reviewWritingUserId",reviewWritingUserId)
            startActivity(ChildCommentintent)

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



    //바텀네비게이션 프래그먼트 셀렉트 리스너
    private val onBottomOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener {

            showFloatingButtonVisible(it.itemId)
            when (it.itemId) {

                R.id.menu_home -> {
                    Log.d(TAG, "메인 엑티비티 홈 버튼 클릭")

                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.title = "홈"



                    mainFragment = MainFragment.newInstance()
                    supportFragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.fade_in,
                        R.animator.fade_out,
                        R.animator.fade_in,
                        R.animator.fade_out
                    ).replace(R.id.view, mainFragment)
                        .commit()
                }
                R.id.menu_review -> {
                    Log.d(TAG, "메인 엑티비티 리뷰 버튼 클릭")
                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("리뷰")

                    binding.mainFragmentCreateRoomBtn.hide()
                    reviewFragment = ReviewFragment.newInstance()
                    supportFragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.fade_in,
                        R.animator.fade_out,
                        R.animator.fade_in,
                        R.animator.fade_out
                    ).replace(R.id.view, reviewFragment)
                        .commit()
                }
                R.id.menu_ranking -> {
                    Log.d(TAG, "메인 엑티비티 랭킹 버튼 클릭")
                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("랭킹")

                    binding.mainFragmentCreateRoomBtn.hide()
                    rankingFragment = RankingFragment.newInstance()
                    supportFragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.fade_in,
                        R.animator.fade_out,
                        R.animator.fade_in,
                        R.animator.fade_out
                    ).replace(R.id.view, rankingFragment)
                        .commit()
                }
                R.id.menu_chat -> {
                    Log.d(TAG, "메인 엑티비티 채팅 버튼 클릭")
                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("채팅")

                    binding.mainFragmentCreateRoomBtn.hide()
                    chatingFragment = ChatingFragment.newInstance()
                    supportFragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.fade_in,
                        R.animator.fade_out,
                        R.animator.fade_in,
                        R.animator.fade_out
                    ).replace(R.id.view, chatingFragment)
                        .commit()
                }
                R.id.menu_mypage -> {
                    Log.d(TAG, "메인 엑티비티 마이페이지 버튼 클릭")
                    myPageFragment = MyPageFragment.newInstance()
                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("마이페이지")

                    binding.mainFragmentCreateRoomBtn.hide()
                    supportFragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.fade_in,
                        R.animator.fade_out,
                        R.animator.fade_in,
                        R.animator.fade_out
                    ).replace(R.id.view, myPageFragment)
                        .commit()
                }

            }

            true
        }


    //프래그먼트에 따라 플로팅버튼 보여주기
    private fun showFloatingButtonVisible(itemid: Int) {
        when (itemid) {
            R.id.menu_home -> {
                binding.mainFragmentCreateRoomBtn.show()
                binding.mainFragmentCreateReviewBtn.hide()
            }
            R.id.menu_review -> {
                binding.mainFragmentCreateRoomBtn.hide()
                binding.mainFragmentCreateReviewBtn.show()
            }
            R.id.menu_ranking -> {
                binding.mainFragmentCreateRoomBtn.hide()
                binding.mainFragmentCreateReviewBtn.hide()
            }
            R.id.menu_chat -> {
                binding.mainFragmentCreateRoomBtn.hide()
                binding.mainFragmentCreateReviewBtn.hide()
            }
            R.id.menu_mypage -> {
                binding.mainFragmentCreateRoomBtn.hide()
                binding.mainFragmentCreateReviewBtn.hide()
            }

        }
    }

    /** 바텀네비게이션아이템 리스너 오버라이드 2021-05-17부로 코드중복으로 필요없음.
    //    override fun onNavigationItemSelected(item: MenuItem): Boolean {
    //        TODO("Not yet implemented")
    //        val tb: Toolbar = findViewById(R.id.maintoolbar)
    //        tb.visibility = View.VISIBLE
    //        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
    //
    //        when (item.itemId) {
    //            R.id.menu_home -> {
    //                Log.d(TAG, "메인 엑티비티 홈 버튼 클릭")
    //                val fragmentHoem = MainFragment()
    //
    //                transaction.replace(R.id.view, fragmentHoem, "home")
    //            }
    //            R.id.menu_review -> {
    //                Log.d(TAG, "메인 엑티비티 리뷰 버튼 클릭")
    //
    //            }
    //            R.id.menu_ranking -> {
    //                Log.d(TAG, "메인 엑티비티 랭킹 버튼 클릭")
    //
    //            }
    //            R.id.menu_chat -> {
    //                Log.d(TAG, "메인 엑티비티 채팅 버튼 클릭")
    //
    //            }
    //            R.id.menu_mypage -> {
    //                Log.d(TAG, "메인 엑티비티 마이페이지 버튼 클릭")
    //
    //            }
    //
    //        }
    //    }
     */

    /** test 방생성 코트 2021-24일부로 방생성 기능 구현으로 주석처리
    //        방 등록 리퀘스트 메소드
    //         private fun resistRoom() {
    //        val gson: Gson = GsonBuilder()
    //            .setLenient()
    //            .create()
    //
    //        val retrofit =
    //            Retrofit.Builder()
    //                .baseUrl(getString(R.string.http_request_base_url))
    //                .addConverterFactory(GsonConverterFactory.create(gson))
    //                .build()
    //
    //        val server = retrofit.create(RoomAPI::class.java)
    //
    //        server.createRoom("하이",
    //            "바이2222",
    //            "5",
    //            "2021-05-18",
    //            "18:00:00",
    //            "서울시관악구",
    //            "빵집",
    //            "빵집",
    //            "male",
    //            "22","33","호스트네임")
    //            .enqueue(object :Callback<String>{
    //            override fun onResponse(
    //                call: Call<String>,
    //                response: Response<String>
    //            ) {
    //                if(response.isSuccessful)
    //                    Log.e("성공",response.body().toString())
    //                else
    //                    Log.e("실패",response.body().toString())
    //            }
    //
    //            override fun onFailure(call: Call<String>, t: Throwable) {
    //
    //                }
    //
    //        })
    }*/
    fun moveToCreateRoomActivity() {
        val moveToCreateRoomActivityIntent =
            Intent(this@MainFragmentActivity, CreateRoomActivity::class.java)
        startActivity(moveToCreateRoomActivityIntent)
    }

    override fun onBackPressed() {


        if (System.currentTimeMillis() > BackPressTime + 2000) {
            BackPressTime = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        } else {
            super.onBackPressed()
        }

    }

    private fun doWorkWithPeriodic(context: Context) {

        val workRequest = PeriodicWorkRequestBuilder<DatetimeCheckWork>(3, TimeUnit.HOURS).build()
        val workRequestOne = OneTimeWorkRequestBuilder<ScheduleCheckWork>().build()
        /*
            ExistingPeriodicWorkPolicy.KEEP     :  워크매니저가 실행중이 아니면 새로 실행하고, 실행중이면 아무작업도 하지 않는다.
            ExistingPeriodicWorkPolicy.REPLACE  :  워크매니저를 무조건 다시 실행한다.
         */
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "DatetimeCheckWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        WorkManager.getInstance(context).enqueue(workRequestOne)

    }

    @SuppressLint("MissingPermission")
    fun token() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("토큰", token)

            val retrofit = Retrofit.Builder()
                .baseUrl(getString(R.string.http_request_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()

            retrofit.create(RoomAPI::class.java).tokenInsert(MainActivity.user_table_id, token)
                .enqueue(object :
                    Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            if (response.body() == "true") {

                            } else {

                            }
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {

                    }
                })


//        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val locationProvider = LocationManager.GPS_PROVIDER
//        val location: Location? = locationManager.getLastKnownLocation(locationProvider)
//        if (location!=null){
//            Log.e("로케이션","위치 : ${location.latitude} , ${location.longitude}")
//        }
        })
    }

    private fun createOkHttpClient(): OkHttpClient {
        //Log.d ("TAG","OkhttpClient");
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }
}

