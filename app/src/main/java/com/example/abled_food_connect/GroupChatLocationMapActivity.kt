package com.example.abled_food_connect

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.load
import com.example.abled_food_connect.data.GroupChatLocationData
import com.example.abled_food_connect.data.MainFragmentItemData
import com.example.abled_food_connect.databinding.ActivityGroupChatLocationMapBinding
import com.example.abled_food_connect.retrofit.RoomAPI
import com.example.abled_food_connect.works.GpsWork
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@SuppressLint("ResourceType")
class GroupChatLocationMapActivity : AppCompatActivity(), OnMapReadyCallback {
    val binding by lazy { ActivityGroupChatLocationMapBinding.inflate(layoutInflater) }
    val rotate: Animation by lazy { AnimationUtils.loadAnimation(this, R.animator.rotate) }
    lateinit var mapFragment: MapFragment
    lateinit var markerList: ArrayList<Marker>
    var roomId: String = ""
    var isRotate = false
    val PERMISSIONS_REQUEST_CODE = 100
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        roomId = intent.getStringExtra("roomId").toString()
        markerList = ArrayList()

        val fm = supportFragmentManager
        mapFragment = fm.findFragmentById(R.id.GroupLocationMapView) as MapFragment?
            ?: MapFragment.newInstance()
                .also { fm.beginTransaction().add(R.id.GroupLocationMapView, it).commit() }

        mapFragment.getMapAsync(this)
        setSupportActionBar(binding.GroupLocationToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.GroupLocationRefresh.setOnClickListener {
            binding.GroupLocationRefresh.startAnimation(
                rotate
            )
            for (marker in markerList) {
                marker.map = null
            }
            markerList.clear()
            loadMemberLocation()
        }

    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 29) {
            checkBackgroundLocationPermissionAPI30(PERMISSIONS_REQUEST_CODE)
        }
        if (checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
            val workRequest: OneTimeWorkRequest =
                OneTimeWorkRequestBuilder<GpsWork>().build()
            WorkManager.getInstance(this).enqueueUniqueWork(
                roomId,
                ExistingWorkPolicy.REPLACE, workRequest)
        }
    }

    override fun onResume() {

        super.onResume()
        loadMemberLocation()

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
    }

    fun loadMemberLocation() {
        val retrofit =
            Retrofit.Builder()
                .baseUrl(getString(R.string.http_request_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()

        val server = retrofit.create(RoomAPI::class.java).memberLocation(roomId.toInt())
            .enqueue(object : Callback<GroupChatLocationData> {
                override fun onResponse(
                    call: Call<GroupChatLocationData>,
                    response: Response<GroupChatLocationData>
                ) {
                    if (response.body()!!.success) {

                        mapFragment.getMapAsync {
                            for (marker in markerList) {
                                marker.map = null
                            }
                            markerList.clear()
                            var builder = LatLngBounds.Builder()
                            val bitmap =
                                Marker.DEFAULT_ICON.getBitmap(this@GroupChatLocationMapActivity)
                            val inflater = LayoutInflater.from(this@GroupChatLocationMapActivity)
                            val roomInfo: MainFragmentItemData = response.body()!!.roomInfo

                            var roomMarker = Marker()
                            roomMarker.position = LatLng(roomInfo.mapY!!, roomInfo.mapX!!)
                            builder.include(roomMarker.position)
                            roomMarker.icon = MarkerIcons.RED
                            roomMarker.map = it
                            var roomInfoWindow = InfoWindow()
                            roomInfoWindow.adapter = object :
                                InfoWindow.DefaultTextAdapter(this@GroupChatLocationMapActivity) {
                                override fun getText(p0: InfoWindow): CharSequence {
                                    return roomInfo.placeName!!
                                }
                            }
                            roomInfoWindow.open(roomMarker)
                            markerList.add(roomMarker)

                            for (item in response.body()!!.members) {
                                builder.include(LatLng(item.x, item.y))
                                val view =
                                    inflater.inflate(R.layout.group_location_item, null, true)
                                val markerImage = view.findViewById<ImageView>(R.id.markerDot)
                                markerImage.setImageBitmap(bitmap)
                                val profileImage =
                                    view.findViewById<CircleImageView>(R.id.markerCircle)
                                if (item.userNickname == MainActivity.loginUserNickname) {

                                    var nickname =
                                        view.findViewById<TextView>(R.id.groupLocationUserNick)
                                    nickname.setBackgroundResource(R.drawable.social_login_naver_button)
                                    nickname.setTextColor(Color.parseColor("#FFFFFF"))
                                    nickname.text = "나"
                                } else {
                                    var nickname =
                                        view.findViewById<TextView>(R.id.groupLocationUserNick)
                                    nickname.text = item.userNickname
                                }
                                profileImage.load(getString(R.string.http_request_base_url) + item.userThumbnail)
                                val marker = Marker()
                                marker.position = LatLng(item.x, item.y)
                                marker.icon = OverlayImage.fromView(view)
                                marker.map = it
                                markerList.add(marker)
                            }
                            val buildMap: LatLngBounds = builder.build()
                            it.moveCamera(
                                CameraUpdate.fitBounds(buildMap, 300)
                                    .animate(CameraAnimation.Easing)
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<GroupChatLocationData>, t: Throwable) {

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

    override fun onMapReady(p0: NaverMap) {

    }

    @TargetApi(30)
    private fun Context.checkBackgroundLocationPermissionAPI30(backgroundLocationRequestCode: Int) {
        if (checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            return
        } else {
            AlertDialog.Builder(this)
                .setTitle("위치 사용권한")
                .setMessage("위치사용권한을 항상사용을로 변경해주세요.")
                .setPositiveButton("설정") { _, _ ->
                    // this request will take user to Application's Setting page
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        backgroundLocationRequestCode
                    )
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                    onBackPressed()
                }
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
}