package com.example.abled_food_connect

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.abled_food_connect.retrofit.API
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.abled_food_connect.data.ReviewWritingData
import com.example.abled_food_connect.data.RoomTbDbInfoData
import com.example.abled_food_connect.databinding.*
import com.example.abled_food_connect.fragments.ReviewFragment
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType


class ReviewWriting : AppCompatActivity() {



    /*
       코틀린 뷰 바인딩을 적용시켰습니다.
     */
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityReviewWritingBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!



    //다중이미지 선택 변수
    private var selectedUriList: List<Uri>? = null
    private lateinit var imgAddBtn : Button
    private lateinit var img_change_btn : ImageButton


    //별점 변수

    var tasteStarPoint : Int = 0
    var serviceStarPoint : Int = 0
    var cleanStarPoint : Int = 0
    var interiorStarPoint : Int = 0


    //리뷰 내용
    lateinit var review_description : String


    //서버로 보낼 이미지 어래이
    var itemphoto = ArrayList<MultipartBody.Part>()


    lateinit var myPath:String


    //어떤 방의 리뷰를 작성하는가 - 방번호변수
    var room_id = 0

    //가게명 및 가게주소
    lateinit var restaurantName : String
    lateinit var restaurantAddressStr : String

    lateinit var appointment_day :String
    lateinit var appointment_time :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // 기존 setContentView 를 제거해주시고..
        //setContentView(R.layout.activity_review_writing)

        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityReviewWritingBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.

        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.
        // 뷰 id도 파스칼케이스 + 카멜케이스의 네이밍규칙 적용으로 인해서 nicName_Et -> nicNameEt 로 자동 변환 되었습니다.



        setSupportActionBar(binding.Toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.Toolbar.title = "리뷰작성"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        room_id = intent.getIntExtra("room_id",0)
        Log.d("TAG", room_id.toString())
        roomTbDbGet(room_id)


        /*
        다중이미지 선택 기능
         */

        //카메라 권한
        settingPermission()


        imgAddBtn = binding.imgAddBtn

        img_change_btn = binding.imgChangeBtn






        imgAddBtn.setOnClickListener(View.OnClickListener {
            TedImagePicker.with(this)
                .mediaType(MediaType.IMAGE)
                .cameraTileBackground(R.color.purple_200)
                .title("이미지 선택")
                .max(3,"최대 3장까지 선택할 수 있습니다.")
                //.scrollIndicatorDateFormat("YYYYMMDD")
                //.buttonGravity(ButtonGravity.BOTTOM)
                //.buttonBackground(R.drawable.btn_sample_done_button)
                //.buttonTextColor(R.color.sample_yellow)
                .errorListener { message -> Log.d("ted", "message: $message") }
                .selectedUri(selectedUriList)
                .startMultiImage { list: List<Uri> -> showMultiImage(list) }
        })


        img_change_btn.setOnClickListener(View.OnClickListener {
            TedImagePicker.with(this)
                .mediaType(MediaType.IMAGE)
                .cameraTileBackground(R.color.purple_200)
                .title("이미지 선택")
                .max(3,"최대 3장까지 선택할 수 있습니다.")
                //.scrollIndicatorDateFormat("YYYYMMDD")
                //.buttonGravity(ButtonGravity.BOTTOM)
                //.buttonBackground(R.drawable.btn_sample_done_button)
                //.buttonTextColor(R.color.sample_yellow)
                .errorListener { message -> Log.d("ted", "message: $message") }
                .selectedUri(selectedUriList)
                .startMultiImage { list: List<Uri> -> showMultiImage(list) }
        })




//        binding.ratingStarTaste.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
//            tasteStarPoint = rating.toInt()
//            Log.d("레이팅스타 맛", "${tasteStarPoint}점")
//        }
//
//        binding.ratingStarService.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
//            serviceStarPoint = rating.toInt()
//            Log.d("레이팅스타 서비스", "${serviceStarPoint}점")
//        }
//
//        binding.ratingStarClean.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
//            cleanStarPoint= rating.toInt()
//            Log.d("레이팅스타 위생", "${cleanStarPoint}점")
//        }
//
//        binding.ratingStarInterior.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
//            interiorStarPoint= rating.toInt()
//            Log.d("레이팅스타 인테리어", "${interiorStarPoint}점")
//        }




        binding.writingFinishBtn.setOnClickListener(View.OnClickListener {

            Log.d("사이즈뭐야", selectedUriList?.size.toString())

            if(selectedUriList?.size  == null||selectedUriList?.size  == 0){
                Toast.makeText(this@ReviewWriting, "리뷰 이미지를 선택해주세요", Toast.LENGTH_SHORT).show()
            }

            //별점기능 제거
//            else if(tasteStarPoint == 0){
//                Toast.makeText(this@ReviewWriting, "별점을 선택해주세요", Toast.LENGTH_SHORT).show()
//            }else if(serviceStarPoint == 0){
//                Toast.makeText(this@ReviewWriting, "별점을 선택해주세요", Toast.LENGTH_SHORT).show()
//            }else if(cleanStarPoint == 0){
//                Toast.makeText(this@ReviewWriting, "별점을 선택해주세요", Toast.LENGTH_SHORT).show()
//            }else if(interiorStarPoint == 0){
//                Toast.makeText(this@ReviewWriting, "별점을 선택해주세요", Toast.LENGTH_SHORT).show()
//            }

            else if(binding.reveiwDescriptionEt.getText().toString().replace(" ", "").equals("")){
                Toast.makeText(this@ReviewWriting, "리뷰를 작성해주세요", Toast.LENGTH_SHORT).show()
            }
            else {

                for (i in selectedUriList?.indices!!) {

                    var currentImageUri: Uri? = selectedUriList?.get(i)

                    val uriPathHelper = URIPathHelper()
                    myPath = currentImageUri?.let { uriPathHelper.getPath(this, it) }.toString()

                    val file = File(myPath)


                    //이미지의 확장자를 구한다.
                    val extension = MimeTypeMap.getFileExtensionFromUrl(currentImageUri.toString())


                    var requestBody: RequestBody =
                        RequestBody.create("image/*".toMediaTypeOrNull(), file);
                    var body: MultipartBody.Part =
                        MultipartBody.Part.createFormData(
                            "itemphoto${i}",
                            "itemphoto${i}.${extension}",
                            requestBody
                        )
                    itemphoto.add(body)

                }

                review_description = binding.reveiwDescriptionEt.text.toString()


                UserReviewWriting();
            }
        })






    }


    // 다중이미지 선택 메서드

    private fun showMultiImage(uriList: List<Uri>) {
        this.selectedUriList = uriList
        Log.d("ted", "uriList: $uriList")
        binding.containerSelectedPhotos.visibility = View.VISIBLE

        binding.containerSelectedPhotos.removeAllViews()



        val viewSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100F, resources.displayMetrics)
                .toInt()
        uriList.forEach {
            val itemImageBinding = ReviewWritingItemImageBinding.inflate(LayoutInflater.from(this))
            Glide.with(this)
                .load(it)
                .apply(RequestOptions().centerCrop())
                .into(itemImageBinding.ivMedia)
            itemImageBinding.root.layoutParams = FrameLayout.LayoutParams(viewSize, viewSize)

            val createMarginBinding = ReviewWritingCreateMarginBinding.inflate(LayoutInflater.from(this))

            binding.containerSelectedPhotos.addView(itemImageBinding.root)
            binding.containerSelectedPhotos.addView(createMarginBinding.root)

        }

        if(uriList.size<3){
            imgAddBtn.setText("사진 "+uriList.size+"/3")
            imgAddBtn.visibility = View.VISIBLE
            img_change_btn.visibility = View.GONE
        }else{
            imgAddBtn.visibility = View.GONE
            img_change_btn.visibility = View.VISIBLE
        }

    }

    fun settingPermission(){
        var permis = object  : PermissionListener {
            //            어떠한 형식을 상속받는 익명 클래스의 객체를 생성하기 위해 다음과 같이 작성
            override fun onPermissionGranted() {
//                Toast.makeText(this@ReviewWriting, "권한 허가", Toast.LENGTH_SHORT)
//                    .show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@ReviewWriting, "카메라 권한 설정에 동의해주세요", Toast.LENGTH_SHORT)
                    .show()
                ActivityCompat.finishAffinity(this@ReviewWriting) // 권한 거부시 앱 종료
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permis)
            .setRationaleMessage("카메라 사진 권한 필요")
            .setDeniedMessage("카메라 권한 요청 거부")
            .setPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
            .check()
    }


    class URIPathHelper {

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun getPath(context: Context, uri: Uri): String? {
            val isKitKatorAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // DocumentProvider
            if (isKitKatorAbove && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }

                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return null
        }

        fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor = uri?.let { context.getContentResolver().query(it, projection, selection, selectionArgs,null) }
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index: Int = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } finally {
                if (cursor != null) cursor.close()
            }
            return null
        }

        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }
    }





    fun UserReviewWriting(){

        //The gson builder
        var gson : Gson =  GsonBuilder()
            .setLenient()
            .create()


        //creating retrofit object
        var retrofit =
            Retrofit.Builder()
                .baseUrl(getString(R.string.http_request_base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        //creating our api

        var server = retrofit.create(API.reviewWriting::class.java)

        // 파일, 사용자 아이디, 파일이름

//            nick_name = binding.nicNameEt.text.toString()
//            //birth_year 데이터 피커에서 값을 받는다.
//            //user_gender = binding.userGenderEt.text.toString()
//            phone_number = binding.phoneNumberInputEt.text.toString()

        server.review_Writing_Request(itemphoto,room_id,MainActivity.user_table_id,MainActivity.loginUserId,MainActivity.loginUserNickname,restaurantAddressStr,restaurantName,"2021-05-19 14:57:42",appointment_day,appointment_time,review_description,tasteStarPoint,serviceStarPoint,cleanStarPoint,interiorStarPoint).enqueue(object:
            Callback<ReviewWritingData> {
            override fun onFailure(call: Call<ReviewWritingData>, t: Throwable) {
                t.message?.let { Log.d("레트로핏 결과1", it) }
            }

            override fun onResponse(call: Call<ReviewWritingData>, response: Response<ReviewWritingData>) {
                if (response?.isSuccessful) {
                    val Item: ReviewWritingData = response.body()!!

                    var toMoveUserProfileClickedReviewVerticalListActivityIntent : Intent = Intent(this@ReviewWriting, UserProfileClickedReviewVerticalListActivity::class.java)
                    var review_id = Item.review_id
                    toMoveUserProfileClickedReviewVerticalListActivityIntent.putExtra("review_id", review_id)
                    startActivity(toMoveUserProfileClickedReviewVerticalListActivityIntent)
                    Toast.makeText(applicationContext, "리뷰완료!\n"+"랭킹포인트 ${Item.get_season_point}점을 획득하셨습니다.\n"+"현재 시즌 랭킹포인트 : ${Item.now_season_total_rangking_point}", Toast.LENGTH_LONG).show()
                    finish()
                    Log.d("레트로핏 결과2",""+response?.body().toString())


                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                    Log.d("레트로핏 실패결과",""+response?.body().toString())
                }
            }
        })
    }




    fun roomTbDbGet(room_id:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.roomTbDbInfoGetInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val room_tb_data_get = api.room_tb_db_info_get(room_id)


        room_tb_data_get.enqueue(object : Callback<RoomTbDbInfoData> {
            override fun onResponse(
                call: Call<RoomTbDbInfoData>,
                response: Response<RoomTbDbInfoData>
            ) {
                Log.d("룸정보", "룸정보 : ${response.raw()}")
                Log.d("룸정보", "룸정보 : ${response.body().toString()}")

                var items : RoomTbDbInfoData? =  response.body()
                Log.d("룸정보", "룸정보 : ${items}")



                var addressSplit = items!!.roomInfoList.get(0).restaurant_roadaddress.split(" ")
                restaurantAddressStr = addressSplit[0]+">"+addressSplit[1]

                restaurantName = items!!.roomInfoList.get(0).restaurant_name



                binding.restaurantAddressInfoTv.text = restaurantAddressStr+">"+restaurantName



                appointment_day = items!!.roomInfoList.get(0).appointment_day

                appointment_time = items!!.roomInfoList.get(0).appointment_time


            }

            override fun onFailure(call: Call<RoomTbDbInfoData>, t: Throwable) {
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