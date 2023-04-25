package com.example.abled_food_connect

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.lujun.androidtagview.TagView
import com.example.abled_food_connect.array.Age
import com.example.abled_food_connect.array.MaximumAge
import com.example.abled_food_connect.array.MinimumAge
import com.example.abled_food_connect.array.NumOfPeople
import com.example.abled_food_connect.databinding.ActivityCreateRoomActivityBinding
import com.example.abled_food_connect.retrofit.API
import com.example.abled_food_connect.retrofit.MapSearch
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import net.daum.android.map.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CreateRoomActivity : AppCompatActivity() {
    lateinit var mapFragment: MapFragment
    val binding by lazy { ActivityCreateRoomActivityBinding.inflate(layoutInflater) }
    private var genderMaleSelected: Boolean = false
    private var genderFemaleSelected: Boolean = false
    private var genderAnySelected: Boolean = false
    private val SEARCHMAPRESULTCODE = 110
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION


    )
    private val ACCESS_FINE_LOCATION = 1000
    private var x: Double? = null
    private var y: Double? = null
    /*태그 리스트*/val PERMISSIONS_REQUEST_CODE = 100
    val BACKGROUND_PERMISSIONS_REQUEST_CODE = 1110
    var tagArray: ArrayList<String> = ArrayList()
    lateinit var marker: Marker
    lateinit var placeName: String
    lateinit var address: String
    lateinit var roadAddress: String
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        marker = Marker()
        /*카카오 맵 API*/
//        map = MapView(this)
//        mapview = binding.mapView
//        mapview.addView(map)
        /*바인딩 뷰 변수화*/
        val numOfPeople = binding.CreateRoomActivityNumOfPeopleInput
        val maximum = binding.maximumAgeTextView
        val minimum = binding.minimumAgeTextView
        placeName = String()
        context = this

        /*드롭다운 어댑터 설정*/
        maximum.setAdapter(setAdapter(Age()))
        minimum.setAdapter(setAdapter(Age()))
        numOfPeople.setAdapter(setAdapter(NumOfPeople()))

        onClickListenerGroup()



        getMapImage(null, null, null)

    }


    override fun onStart() {
        super.onStart()
        if (checkLocationService()) {
            // GPS가 켜져있을 경우
            permissionCheck()
        } else {
            // GPS가 꺼져있을 경우
            Toast.makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
//        checkBackgroundLocationPermissionAPI30(PERMISSIONS_REQUEST_CODE)

    }

    override fun onPause() {
        super.onPause()

    }

    override fun onStop() {
        super.onStop()


    }

    override fun onDestroy() {
        super.onDestroy()
    }


    /*


    메소드 구역



    */

    /**
     * 퍼미션 리절트
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size) {
            var check_result = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if (check_result) {
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[1]

                    )
                ) {
                    Toast.makeText(
                        this,
                        "권한 설정이 거부되었습니다.\n앱을 사용하시려면 다시 실행해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {

                    Toast.makeText(this, "권한 설정이 거부되었습니다.\n설정에서 권한을 허용해야 합니다..", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

//        if (requestCode == ACCESS_FINE_LOCATION) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 권한 요청 후 승인됨 (추적 시작)
//                Toast.makeText(this, "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
//
//            } else {
//                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
//                Toast.makeText(this, "위치 권한이 거절되었습니다", Toast.LENGTH_SHORT).show()
//                permissionCheck()
//            }
//        }
    }

    /**
     * 위치권한 퍼미션 체크
     * */
    private fun checkPermissions() {
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if (rejectedPermissionList.isNotEmpty()) {
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(
                this,
                rejectedPermissionList.toArray(array),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }


    /**
     * 나이 어댑터
     * */
    private fun setAdapter(Age: Age): ArrayAdapter<Int> {


        return ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, Age.numArray())

    }

    private fun setAdapter(age: MinimumAge, num: Int): ArrayAdapter<Int> {


        return ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, age.numArray(num))

    }

    private fun setAdapter(age: MaximumAge, num: Int): ArrayAdapter<Int> {


        return ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, age.numArray(num))

    }

    /**
     * 모집인원 어댑터
     * */
    private fun setAdapter(NumOfPeople: NumOfPeople): ArrayAdapter<Int> {


        return ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            NumOfPeople.numArray(3)
        )

    }

    /**
     *입력 항목 체크 메서드
     */
    private fun inputCheck(): Boolean {

        if (binding.CreateRoomActivityRoomTitleInput.length() == 0) {
            Toast.makeText(this, "방 제목을 입력해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, 0)
            binding.CreateRoomTitleInputLayout.requestFocus()
            return false
        } else if (binding.CreateRoomActivityRoomInformationInput.length() == 0) {
            Toast.makeText(this, "방 소개를 입력해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomTitleInputLayout.bottom)
            binding.CreateRoomInformationInputLayout.requestFocus()
            return false
        } else if (binding.CreateRoomActivityNumOfPeopleInput.length() == 0) {
            Toast.makeText(this, "모집 인원수를 선택해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomInformationInputLayout.bottom)
            binding.CreateRoomActivityNumOfPeopleInput.requestFocus()
            return false
        } else if (binding.CreateRoomActivityDateInput.length() == 0) {
            Toast.makeText(this, "날짜를 선택해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomActivityNumOfPeopleInput.bottom)
            binding.CreateRoomActivityDateInput.requestFocus()
            return false
        } else if (binding.CreateRoomActivityTimeInput.length() == 0) {
            Toast.makeText(this, "시간을 선택해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomActivityNumOfPeopleInput.bottom)
            binding.CreateRoomActivityTimeInput.requestFocus()
            return false
        } else if (!timeCompare("${binding.CreateRoomActivityDateInput.text.toString()} ${binding.CreateRoomActivityTimeInput.text.toString()}")) {
            Toast.makeText(this, "입력한 시간이 이미 지나간 시간입니다", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomActivityNumOfPeopleInput.bottom)

            return false
        } else if (placeName.isEmpty()) {
            Toast.makeText(this, "지도 버튼을 통해 모임장소를 선택해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomActivityTimeInput.bottom)
            return false
        } else if (!genderMaleSelected && !genderFemaleSelected && !genderAnySelected) {
            Toast.makeText(this, "모집 성별 선택해주세요", Toast.LENGTH_LONG).show()
            return false
        } else if (binding.minimumAgeTextView.length() == 0) {
            Toast.makeText(this, "최소나이를 선택해주세요", Toast.LENGTH_LONG).show()
            binding.minimumAgeTextView.requestFocus()
            return false

        } else if (binding.maximumAgeTextView.length() == 0) {
            Toast.makeText(this, "최대나이를 선택해주세요", Toast.LENGTH_LONG).show()
            binding.maximumAgeTextView.requestFocus()
            return false

        } else if (binding.maximumAgeTextView.text.toString()
                .toInt() < binding.minimumAgeTextView.text.toString().toInt()
        ) {
            Toast.makeText(this, "최대나이가 최소나이 보다 작을 수 없습니다.", Toast.LENGTH_LONG).show()
            binding.maximumAgeTextView.requestFocus()
            return false
        } else {
            return true
        }
    }


    /**
     * 모집 성별 선택 메소드
     */
    private fun limitGender(gender: String): Boolean {

        when (gender) {
            "male" -> {
                binding.CreateRoomActivityMaleImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_male",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                binding.CreateRoomActivityFemaleImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_female_not_focus",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                binding.CreateRoomActivityGenderAnyImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_maleandfemale_not_focus",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                genderFemaleSelected = false
                genderAnySelected = false
                return true
            }
            "female" -> {
                binding.CreateRoomActivityMaleImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_male_not_focus",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                binding.CreateRoomActivityFemaleImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_female",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                binding.CreateRoomActivityGenderAnyImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_maleandfemale_not_focus",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                genderMaleSelected = false
                genderAnySelected = false
                return true
            }
            else -> {
                binding.CreateRoomActivityMaleImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_male_not_focus",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                binding.CreateRoomActivityFemaleImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_female_not_focus",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                binding.CreateRoomActivityGenderAnyImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_maleandfemale",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                genderFemaleSelected = false
                genderMaleSelected = false
                return true
            }
        }
    }

    /**
     * 입력된 방정보 서버에 리퀘스트
     */
    private fun createRoom() {
        val tile = binding.CreateRoomActivityRoomTitleInput.text.toString()
        val info = binding.CreateRoomActivityRoomInformationInput.text.toString()
        val numOfPeople = binding.CreateRoomActivityNumOfPeopleInput.text.toString()
        val date = binding.CreateRoomActivityDateInput.text.toString()
        val time = binding.CreateRoomActivityTimeInput.text.toString()
        val address = address
        val roadAddress = roadAddress
        val placeName = placeName
        val shopName = placeName
        val keyWord = tagArray.toString()
        var gender: String = ""
        var mapx = x?.toDouble()
        var mapy = y?.toDouble()

        if (genderMaleSelected) {
            gender = "male"
        } else if (genderFemaleSelected) {
            gender = "female"
        } else if (genderAnySelected) {
            gender = "any"
        }

        val minAge = binding.minimumAgeTextView.text.toString()
        val maxAge = binding.maximumAgeTextView.text.toString()
        val hostName = MainActivity.loginUserNickname

        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit =
            Retrofit.Builder()
                .baseUrl(getString(R.string.http_request_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()

        val server = retrofit.create(RoomAPI::class.java)

        server.createRoom(
            MainActivity.user_table_id.toString(),
            tile,
            info,
            numOfPeople,
            date,
            time,
            address,
            roadAddress,
            placeName,
            shopName,
            keyWord,
            gender,
            minAge,
            maxAge,
            hostName,
            MainActivity.user_table_id,
            mapx.toString(),
            mapy.toString()
        )
            .enqueue(object : Callback<API.createRoomHost> {
                override fun onResponse(
                    call: Call<API.createRoomHost>,
                    response: Response<API.createRoomHost>
                ) {

                    val room: API.createRoomHost? = response.body()

                    if (room!!.success) {
                        Toast.makeText(this@CreateRoomActivity, "방 생성", Toast.LENGTH_SHORT).show()

                        val intent =
                            Intent(this@CreateRoomActivity, RoomInformationActivity::class.java)
                        intent.putExtra("roomId", room.roomId.roomId)
                        intent.putExtra("title", room.roomId.title)
                        intent.putExtra("info", room.roomId.info)
                        intent.putExtra("hostName", room.roomId.hostName)
                        intent.putExtra("address", room.roomId.address)
                        intent.putExtra("date", room.roomId.date)
                        intent.putExtra("shopName", room.roomId.shopName)
                        intent.putExtra("roomStatus", room.roomId.roomStatus)
                        intent.putExtra("nowNumOfPeople", room.roomId.nowNumOfPeople)
                        intent.putExtra("hostIndex", room.roomId.hostIndex)
                        Log.e("nowNumOfPeople", room.roomId.nowNumOfPeople!!)
                        intent.putExtra("numOfPeople", room.roomId.numOfPeople)
                        intent.putExtra("keyWords", room.roomId.keyWords)
                        intent.putExtra("mapX", room.roomId.mapX)
                        intent.putExtra("mapY", room.roomId.mapY)
                        intent.putExtra("nowNumOfPeople", room.roomId.nowNumOfPeople.toString())
                        intent.putExtra("imageUrl", MainActivity.userThumbnailImage)
                        intent.putExtra("join", "1")
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onFailure(call: Call<API.createRoomHost>, t: Throwable) {

                }

            })
    }

    /**
     * Retrofit.Builder Client 옵션 메소드
     */
    private fun createOkHttpClient(): OkHttpClient {
        //Log.d ("TAG","OkhttpClient");
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }

    /**
     * 오늘 날짜 까지 제한하여 데이트피커 띄우기
     */
    private fun dateCalendarDialog() {
        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        var datePicker = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                binding.CreateRoomActivityDateInput.setText(
                    "${year}-${plusZero(month + 1)}-${
                        plusZero(
                            dayOfMonth
                        )
                    }"
                )
            }
        }

        var builder = DatePickerDialog(this, datePicker, year, month, day - 1)

        builder.datePicker.minDate = System.currentTimeMillis()
        builder.show()

    }

    /**
     * 타임피커 띄우기
     */
    private fun timeCalendarDialog() {
        var time = Calendar.getInstance(Locale.KOREA)
        var hour = time.get(Calendar.HOUR)
        var minute = time.get(Calendar.MINUTE)

        var timePicker = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

                binding.CreateRoomActivityTimeInput.setText(
                    "${plusZero(hourOfDay)}:${
                        plusZero(
                            minute
                        )
                    }"
                )
            }
        }
        var builder = TimePickerDialog(this, timePicker, hour, minute, true)

        builder.show()
    }

    /**
     * 현재 시스템시간과 설정한 약속시간 비교
     */
    private fun timeCompare(time: String): Boolean {
        var simpleTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
        var now: Long = System.currentTimeMillis()
        var nowTime = simpleTime.format(Date(now))
        var transNowTime: Date = simpleTime.parse(nowTime)
        var settingTime: Date = simpleTime.parse(time)
        Log.e("날짜 비교", "${transNowTime}/${settingTime}")
        Log.e("결과", settingTime.after(transNowTime).toString())
        return settingTime.after(transNowTime)

    }

    /**
     * 데이트/타임 피커에서 선택한 숫자가 10보다 낮을 경우
     * 숫자 앞에 0을 븉여 두자릿수로 표현하는 메소드
     */
    private fun plusZero(int: Int): String {
        return if (int < 10) {
            "0$int"
        } else {
            int.toString()
        }
    }

    /**
     * 온클릭 메서드들 그룹
     */
    private fun onClickListenerGroup() {
        /*방만들기 버튼클릭*/
        binding.CreateRoomButton.setOnClickListener {
            if (inputCheck()) {
                createRoom()
            }
        }
        setSupportActionBar(binding.CreateRoomActivityToolbar)
        /*툴바 타이틀 세팅*/
        val tb = supportActionBar!!
        tb.title = "방만들기"
        tb.setDisplayHomeAsUpEnabled(true)


        /*성별 선택 남자 온클릭 리스너*/
        binding.CreateRoomActivityMaleImageView.setOnClickListener {
            genderMaleSelected = limitGender("male")
        }
        /*성별 선택 여자 온클릭 리스너*/
        binding.CreateRoomActivityFemaleImageView.setOnClickListener {
            genderFemaleSelected = limitGender("female")
        }
        /*성별 선택 상관없음 온클릭 리스너*/
        binding.CreateRoomActivityGenderAnyImageView.setOnClickListener {
            genderAnySelected = limitGender("any")
        }


        //태그 리스트 사이즈 0 이면 텍스트뷰 보이기
        if (tagArray.size == 0) {
            binding.emptyKeyWordTextView.visibility = View.VISIBLE
        }
        /*태그 입력창 온클릭 리스너*/
        binding.CreateRoomActivityKeyWordInput.setOnClickListener {

        }
        /*날짜 텍스트박스에 포커스생겼을때*/
        binding.CreateRoomActivityDateInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                dateCalendarDialog()
            } else {

            }
        }
        /*날짜 텍스트박스 온클릭리스너*/
        binding.CreateRoomActivityDateInput.setOnClickListener {
            dateCalendarDialog()
        }
        /*시간 텍스트박스에 포커스생겼을때*/
        binding.CreateRoomActivityTimeInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                timeCalendarDialog()

            } else {

            }
        }
        /*시간 텍스트박스 온클릭리스너*/
        binding.CreateRoomActivityTimeInput.setOnClickListener {
            timeCalendarDialog()
        }
//        binding.time.setOnClickListener {
//            timeCalendarDialog()
//        }
//        binding.date.setOnClickListener {
//            dateCalendarDialog()
//        }
        /*태그 등록 버튼 온클릭리스너*/
        binding.tagAddButton.setOnClickListener {
            if (binding.CreateRoomActivityKeyWordInput.length() != 0) {
                /*태그 레이아웃 추가*/
                binding.tagLayout.addTag(binding.CreateRoomActivityKeyWordInput.text.toString())
                /*태그 리스트에 키워드 추가*/
                tagArray.add(binding.CreateRoomActivityKeyWordInput.text.toString())
                /*태그 리스트 0초과 일 때 텍스트뷰 없애기*/
                if (tagArray.size != 0) {
                    binding.emptyKeyWordTextView.visibility = View.GONE
                }
                /*등록 완료 후 텍스트 입력창 초기화*/
                binding.CreateRoomActivityKeyWordInput.setText("")
                /*스크롤 내리기*/
                val scroll = Handler()
                scroll.post(Runnable {

                    binding.Scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    binding.CreateRoomActivityKeyWordInput.requestFocus()

                })


//                Toast.makeText(this, tagArray.toString(),Toast.LENGTH_SHORT).show()
            }
        }

        /*태그 온클릭 리스너*/
        binding.tagLayout.isEnableCross = true
        binding.tagLayout.setOnTagClickListener(object : TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String?) {

            }

            override fun onTagLongClick(position: Int, text: String?) {


            }


            override fun onSelectedTagDrag(position: Int, text: String?) {
                TODO("Not yet implemented")
            }

            override fun onTagCrossClick(position: Int) {
                /*태그 롱클릭시 제거*/
                binding.tagLayout.removeTag(position)
                /*태그 리스트에서 해당 포지션 제거*/
                tagArray.removeAt(position)
//                Toast.makeText(this@CreateRoomActivity, tagArray.toString(),Toast.LENGTH_SHORT).show()
                /*태그 리스트가 0일 때 텍스트뷰 보이기*/
                if (tagArray.size == 0) {
                    binding.emptyKeyWordTextView.visibility = View.VISIBLE
                }
            }
        })

        /*지도 이미지 버튼 클릭 리스너*/
        binding.CreateRoomMapSearchButton.setOnClickListener {
            val intent = Intent(this, CreateRoomMapSearchActivity::class.java)
            startActivityForResult(intent, SEARCHMAPRESULTCODE)

        }

        binding.minimumAgeTextView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val num = binding.minimumAgeTextView.text.toString().toInt()
                binding.maximumAgeTextView.setAdapter(setAdapter(MaximumAge(), num))
            }
        })

        binding.maximumAgeTextView.setOnItemClickListener { parent, view, position, id ->
            val num = binding.maximumAgeTextView.text.toString().toInt()
            binding.minimumAgeTextView.setAdapter(setAdapter(MinimumAge(), num))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                SEARCHMAPRESULTCODE -> {
                    x = data?.getDoubleExtra("x", 0.0)
                    y = data?.getDoubleExtra("y", 0.0)
                    placeName = data?.getStringExtra("shopName").toString()
                    address = data?.getStringExtra("address").toString()
                    roadAddress = data?.getStringExtra("roadAddress").toString()
                    Log.e("가게이름", placeName)
                    getMapImage(x, y, placeName)
                    binding.CreateRoomActivityMapView.setOnClickListener {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse("http://map.naver.com/?query=$address")
                        startActivity(i)
                    }

                }


            }
        }
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


    fun getMapImage(x: Double?, y: Double?, placeName: String?) {
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
                binding.CreateRoomActivityMapView.setImageBitmap(bitmap)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("StaticMap", "실패")
            }
        })


    }

    /* 나이제한 배열 생성 2021-05-19 기능을 인터페이스로 전환하여 사용 불필요
     private fun ageArrayList(): ArrayList<Int> {
     val list = ArrayList<Int>()

     for (i in 18..100) {
     list.add(i)

     }
     return list
     }*/

    /* 모집인원 배열생성 2021-05-19 기능을 인터페이스로 전환하여 사용 불필요
    private fun numOfPeopleArrayList(): ArrayList<Int> {
    val list = ArrayList<Int>()

    for (i in 0..3) {
    list.add(i)

    }
    return list
    }
     */
    @TargetApi(30)
    private fun Context.checkBackgroundLocationPermissionAPI30(backgroundLocationRequestCode: Int) {
        if (checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            return
        } else {
            AlertDialog.Builder(this)
                .setTitle("위치 사용권한")
                .setMessage("위치사용권한을 항상사용으로 변경해주세요.")
                .setPositiveButton("설정") { _, _ ->
//                     this request will take user to Application's Setting page
//                    requestPermissions(
//                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
//                        backgroundLocationRequestCode
//                    )
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)

                }
                .setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                    onBackPressed()
                }
                .setCancelable(false)
                .create()
                .show()
        }


    }

    private fun Context.checkSinglePermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun permissionCheck() {
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        ACCESS_FINE_LOCATION
                    )
                }
                builder.setNegativeButton("취소") { dialog, which ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        ACCESS_FINE_LOCATION
                    )
                } else {
                    // 다시 묻지 않음 클릭 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { dialog, which ->
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:$packageName")
                        )
                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") { dialog, which ->

                    }
                    builder.show()
                }
            }
        } else {
            // 권한이 있는 상태

        }
    }

    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}


