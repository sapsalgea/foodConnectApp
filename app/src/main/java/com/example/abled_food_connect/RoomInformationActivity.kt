package com.example.abled_food_connect


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.abled_food_connect.broadcastReciver.RoomInformationBroadcast
import com.example.abled_food_connect.data.SubscriptionData
import com.example.abled_food_connect.databinding.ActivityRoomInformationBinding
import com.example.abled_food_connect.retrofit.MapSearch
import com.example.abled_food_connect.retrofit.RoomAPI
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.InputStream


class RoomInformationActivity : AppCompatActivity() {
    val binding by lazy { ActivityRoomInformationBinding.inflate(layoutInflater) }
    private var mapClick = true
    var roomId: String = ""
    lateinit var title: String
    lateinit var info: String
    lateinit var hostName: String
    lateinit var address: String
    lateinit var date: String
    lateinit var shopName: String
    var roomStatus: Double = 0.0
    lateinit var nowNumOfPeople: String
    lateinit var numOfPeople: String
    lateinit var hostIndex: String
    lateinit var keyWords: String
    lateinit var imageUrl: String
    lateinit var join: String
    var userAge: Int = 0
    private var userGender: String? = null
    var minimumAge: Int = 0
    var maximumAge: Int = 0
    lateinit var roomGender: String
    var mapX: Double = 0.0
    var mapY: Double = 0.0
    val TAG = "RoomInfoActivity"
    lateinit var broadcast:RoomInformationBroadcast
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var view = binding.root
        setContentView(view)
        val shared = getSharedPreferences("pref_user_data", Context.MODE_PRIVATE)
        userAge = shared.getInt("userAge", 0)
        userGender = shared.getString("userGender", "")
        val intent = intent
        if (!intent.hasExtra("roomId")) {
            restart(this)
        }
        if (intent.hasExtra("roomId")) {
            roomId = intent.getStringExtra("roomId")!!
        }
        setSupportActionBar(binding.RoomInformationToolbar)
        val tb = supportActionBar!!
        tb.setDisplayHomeAsUpEnabled(true)



        loadRoomInfo()

//        val title = intent.getStringExtra("title")
//        val info = intent.getStringExtra("info")
//        val hostName = intent.getStringExtra("hostName")
//        val address = intent.getStringExtra("address")!!
//        val date = intent.getStringExtra("date")
//        val shopName = intent.getStringExtra("shopName")
//        val roomStatus = intent.getDoubleExtra("roomStatus", 0.0);
//        var nowNumOfPeople = intent.getStringExtra("nowNumOfPeople")
//        Log.e("지금 숫자", nowNumOfPeople!!)
//        val numOfPeople = intent.getStringExtra("numOfPeople")
//        val hostIndex = intent.getStringExtra("hostIndex")
//        val keyWords = intent.getStringExtra("keyWords")
//        val imageUrl = intent.getStringExtra("imageUrl")
//        val join = intent.getStringExtra("join")
//        val minimumAge = intent.getIntExtra("minimumAge", 0)
//        val maximumAge = intent.getIntExtra("maximumAge", 0)
//        val roomGender = intent.getStringExtra("roomGender")
//        val mapX = intent.getDoubleExtra("mapX", 0.0)
//        val mapY = intent.getDoubleExtra("mapY", 0.0)
//        if (join == "0") {
//            binding.RoomInfoSubscriptionRoomBtn.visibility = View.VISIBLE
//            binding.RoomInfoJoinRoomBtn.visibility = View.GONE
//        } else {
//            binding.RoomInfoSubscriptionRoomBtn.visibility = View.GONE
//            binding.RoomInfoJoinRoomBtn.visibility = View.VISIBLE
//        }


    }

    override fun onStart() {
        super.onStart()
        broadcast = RoomInformationBroadcast()
        val intentFilter = IntentFilter()
        intentFilter.addAction("RoomInfo")
        registerReceiver(broadcast, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        if (mapClick) {
        } else {
            finish()
        }
        unregisterReceiver(broadcast)
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

    fun getMapImage(x: Double?, y: Double?, placeName: String?, address: String) {
        var w = 400
        var h = 400
        var center = "126.978082,37.565577"
        var place: String? = null
        var marker: String? = null
        if (x != null && y != null) {
            center = "$x $y"
            place = "|label:$placeName"
            marker =
                "type:t${place}|size:mid|pos:$x $y|viewSizeRatio:2.0"
        }


        val retrofit = Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(MapSearch::class.java).getStaticMap(
            "kqfai8b97u",
            "NyaUzYcb3IWf1GKPNFDTYJTHIg9SUNtciSstiv5m",
            w,
            h,
            14,
            "basic",
            marker,
            center,
            2
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                val input: InputStream = response.body()!!.byteStream()
//                val bufferedInputStream = BufferedInputStream(input)
                val bitmap: Bitmap = BitmapFactory.decodeStream(input)
                binding.RoomInfoMapImageView.setImageBitmap(bitmap)
                binding.RoomInfoMapImageView.setOnClickListener {
                    mapClick = true

                    val url =
                        "nmap://place?lat=${y}&lng=${x}&name=${placeName}\n\n${address}&appname=${packageName}"
//                    val url = "nmap://search?query=${placeName}&appname=${packageName}"

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    intent.addCategory(Intent.CATEGORY_BROWSABLE)

                    val list = packageManager.queryIntentActivities(
                        intent,
                        PackageManager.MATCH_DEFAULT_ONLY
                    )
                    if (list == null || list.isEmpty()) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://map.naver.com/?query=$address")
                            )
                        )
                    } else {
                        startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("StaticMap", "실패")
            }
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

    fun loadRoomInfo() {
        val retrofit = Retrofit.Builder()
            .baseUrl("ServerIP")
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        retrofit.create(RoomAPI::class.java).loadRoomInfo(roomId, MainActivity.user_table_id)
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {

                    if (response.body() != null) {
                        try {
                            val roomInfo = JSONObject(response.body()!!)
                            roomId = roomInfo.getString("roomId")
                            title = roomInfo.getString("title")
                            info = roomInfo.getString("info")
                            hostName = roomInfo.getString("hostName")
                            address = roomInfo.getString("address")
                            date = roomInfo.getString("date")
                            shopName = roomInfo.getString("shopName")
                            roomStatus = roomInfo.getDouble("roomStatus")
                            nowNumOfPeople = roomInfo.getString("nowNumOfPeople")
                            numOfPeople = roomInfo.getString("numOfPeople")
                            hostIndex = roomInfo.getString("hostIndex")
                            keyWords = roomInfo.getString("keyWords")
                            imageUrl = roomInfo.getString("imageUrl")
                            join = roomInfo.getString("joinCount")
                            minimumAge = roomInfo.getInt("minimumAge")
                            maximumAge = roomInfo.getInt("maximumAge")
                            roomGender = roomInfo.getString("gender")
                            mapX = roomInfo.getDouble("map_x")
                            mapY = roomInfo.getDouble("map_y")


                        } catch (e: Exception) {
                            Log.e(TAG, "onResponse: 에러남 $e")
                        }
                        binding.RoomInfoSubscriptionRoomBtn.setOnClickListener {

                            if (roomId != null) {
                                if (numOfPeople!!.toInt() > nowNumOfPeople!!.toInt()) {
                                    when (true) {

                                        roomGender.equals(userGender) -> {
                                            Log.e("userAge", userAge.toString())
                                            if (userAge in minimumAge..maximumAge) {
                                                joinSubscription(
                                                    roomId,
                                                    MainActivity.user_table_id.toString()
                                                )
                                            } else {
                                                val dialog =
                                                    AlertDialog.Builder(this@RoomInformationActivity)
                                                dialog.setMessage("해당방의 모집 나이에 맞지 않습니다.")
                                                    .setPositiveButton("확인", null)
                                                    .show()
                                            }
                                        }
                                        roomGender.equals("any") -> {
                                            Log.e("userAge", userAge.toString())
                                            if (userAge in minimumAge..maximumAge) {
                                                joinSubscription(
                                                    roomId,
                                                    MainActivity.user_table_id.toString()
                                                )
                                            } else {
                                                val dialog =
                                                    AlertDialog.Builder(this@RoomInformationActivity)
                                                dialog.setMessage("해당방의 모집 나이에 맞지 않습니다.")
                                                    .setPositiveButton("확인", null)
                                                    .show()
                                            }
                                        }
                                        else -> {
                                            val dialog =
                                                AlertDialog.Builder(this@RoomInformationActivity)
                                            dialog.setMessage("해당방의 모집 성별이 맞지 않습니다.")
                                                .setPositiveButton("확인", null)
                                                .show()
                                        }


                                    }


                                } else {
                                    joinSubscription(roomId, MainActivity.user_table_id.toString())
                                }
                            }

                        }
                        if (join == "0") {
                            binding.RoomInfoSubscriptionRoomBtn.visibility = View.VISIBLE
                            binding.RoomInfoJoinRoomBtn.visibility = View.GONE
                        } else {
                            binding.RoomInfoSubscriptionRoomBtn.visibility = View.GONE
                            binding.RoomInfoJoinRoomBtn.visibility = View.VISIBLE
                        }
                        if (roomStatus>0&&nowNumOfPeople == numOfPeople) {
                            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_full)
                            binding.RoomInformationStatus.text  = "FULL"
                        } else
                        if (roomStatus > 5) {
                            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_recruitment)
                            binding.RoomInformationStatus.text = "모집중"
                        } else if (roomStatus > 0.9) {
                            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_imminent)
                            val text: String = getString(R.string.room_status_imminent_time)
                            binding.RoomInformationStatus.text =
                                String.format(text, Math.round(roomStatus).toInt())

                        } else if (roomStatus < 0.9 && roomStatus > 0.0) {
                            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_deadline_imminent)
                            binding.RoomInformationStatus.text = "임박"

                        } else if (roomStatus < 0) {
                            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_deadline)
                            binding.RoomInformationStatus.text = "마감"
                            binding.RoomInfoSubscriptionRoomBtn.isEnabled = false
                        }
                        getMapImage(mapX, mapY, shopName, address)
                        binding.RankingCircleView.load(getString(R.string.http_request_base_url) + imageUrl)
                        {
                            this.placeholder(R.drawable.ic_logo)
                        }
                        binding.RoomInformationCategoryTitleTextview.text = title
                        binding.RoomInfomationDate.text = date
                        binding.RoomInformationCategoryIntroduceTextview.text = info
                        binding.RoomInformationCategoryAddressTextview.text = address
                        binding.RoomInformationCategoryNumOfPeopleTextview.text =
                            "${nowNumOfPeople}/${numOfPeople}명"
                        binding.RoomInfoHostIdTextView.text = hostName
                        binding.RoomInfoShopName.text = shopName



                        binding.RankingCircleView.borderWidth = 20
//        binding.RankingGold.visibility = View.VISIBLE
                        binding.RankingCircleView.borderColor = getColor(R.color.app_theme_color)
//        binding.RankingCircleView.borderColor = Color.parseColor("#ffcd00")

                        binding.RoomInfoJoinRoomBtn.setOnClickListener(View.OnClickListener {
//            val join = API()
//            join.joinRoom(this, roomId.toString(), MainActivity.loginUserNickname)
                            mapClick = false
                            val intent =
                                Intent(this@RoomInformationActivity, ChatRoomActivity::class.java)
                            intent.putExtra("roomId", roomId)
//                            intent.putExtra("title", title)
//                            intent.putExtra("info", info)
                            intent.putExtra("hostName", hostName)
//                            intent.putExtra("address", address)
//                            intent.putExtra("date", date)
//                            intent.putExtra("shopName", shopName)
//                            intent.putExtra("roomStatus", roomStatus)
//                            intent.putExtra("numOfPeople", numOfPeople.toString())
//                            intent.putExtra("keyWords", keyWords)
//                            intent.putExtra("nowNumOfPeople", nowNumOfPeople)
//                            intent.putExtra("mapX", mapX)
//                            intent.putExtra("mapY", mapY)
                            intent.putExtra("imageUrl", imageUrl)
                            startActivity(intent)


                        })

                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {

                }
            })

    }

    fun joinSubscription(room: String, userIndex: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        retrofit.create(RoomAPI::class.java).joinSubscription(room, userIndex)
            .enqueue(object : Callback<SubscriptionData> {
                override fun onResponse(
                    call: Call<SubscriptionData>,
                    response: Response<SubscriptionData>
                ) {
                    if (response.isSuccessful) {

                        when (response.body()!!.status) {

                            "true" -> {
                                val dialog = AlertDialog.Builder(this@RoomInformationActivity)
                                dialog.setTitle("참여 신청을 보냈습니다.")
                                dialog.setPositiveButton("확인", null)
                                dialog.show()
                            }
                            "null" -> {
                                val dialog = AlertDialog.Builder(this@RoomInformationActivity)
                                dialog.setTitle("참여 신청에 실패 하였습니다.")
                                dialog.setPositiveButton("확인", null)
                                dialog.show()
                            }
                            "full" -> {

                                val dialog = AlertDialog.Builder(this@RoomInformationActivity)
                                dialog.setTitle("방이 꽉 찼습니다")
                                dialog.setPositiveButton("확인", null)
                                dialog.show()
                            }
                            else -> {
                                val dialog = AlertDialog.Builder(this@RoomInformationActivity)
                                dialog.setTitle("이미 보낸 신청이 있습니다.")
                                dialog.setPositiveButton("확인", null)
                                dialog.show()
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<SubscriptionData>, t: Throwable) {

                }

            })
    }

    private fun restart(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }


}