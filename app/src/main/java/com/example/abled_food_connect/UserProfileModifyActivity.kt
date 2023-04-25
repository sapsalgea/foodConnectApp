package com.example.abled_food_connect

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.abled_food_connect.data.UserProfileData
import com.example.abled_food_connect.databinding.ActivityUserProfileModifyBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class UserProfileModifyActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityUserProfileModifyBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    lateinit var nicNameModifyEt : EditText
    lateinit var  duplicationCheckBtn : Button

    lateinit var  nicnameModifyOkBtn : Button
    lateinit var  nicnameModifyCancelBtn : Button

    lateinit var change_ok_alert_tv : TextView

    var is_nicName_dup_check : Boolean = false


    var imageChangeCheck : Boolean = false


    //원본파일 uri를 담고 있는 변수
    lateinit var resultUri : Uri

    //원본이미지가 리사이즈된 비트맵파일 이저장되는 변수
    lateinit var resize_bitmap : Bitmap



    //이미지 Uri
    lateinit var userProfileImageUri : String
    lateinit var thumbnail_userProfileImageUri : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_user_profile_modify)


        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityUserProfileModifyBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.


        setSupportActionBar(binding.userProfileModifyToolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.userProfileModifyToolbar.title = "프로필 수정"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //권한요청
        settingPermission()

        //유저정보를 서버에서 가져온다.

        userProfileLoading(MainActivity.user_table_id)

        Log.d("아이디", MainActivity.user_table_id.toString())




        val cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // use the returned uri
                Log.d("TAG", "석세스")
                val uriContent = result.originalUri
                val uriFilePath = result.getUriFilePath(this) // optional usage



                //profile_get_check에 이미지를 가져왔다는 표시를 해준다
                imageChangeCheck = true





                //이미지를 가져와서 이미지 뷰에 보여준다.
                binding.userProfileIv.setImageURI(uriContent)


                //이미지 uri를 비트맵으로 변환한다.
                val bitmap =
                    MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), uriContent)

                //썸네일 크기 줄이기 비트맵 크기를 1/2로 줄인다.

                var width : Int =bitmap.width
                var height : Int = bitmap.height

                var resize_bitmap : Bitmap
                if(width>100||height>100){
                    resize_bitmap = Bitmap.createScaledBitmap(bitmap!!, 300, 300*height/width, true)

                }else{
                    resize_bitmap = Bitmap.createScaledBitmap(bitmap!!, width/2, height/2, true)
                }

                //썸네일 용량 줄이기 비트맵 화질을 낮춘다.
                resize_bitmap = compressBitmap(resize_bitmap)!!





//                Log.d("uri", resultUri.toString())



                if (uriFilePath != null) {
                    userProfileImageUri = uriFilePath
                    thumbnail_userProfileImageUri =  getRealPathFromURI(getImageUri(applicationContext,resize_bitmap)!!)!!
                    //thumbnail_userProfileImageUri =

                    Log.d("uriContent", uriContent.toString())
                    Log.d("uriFilePath", uriFilePath)
                    Log.d("uri", userProfileImageUri)
                    Log.d("uri", thumbnail_userProfileImageUri)
                }




            } else {
                // an error occurred
                val exception = result.error
            }
        }





        binding.userProfileIv.setOnClickListener(View.OnClickListener {
            cropImage.launch(
                options {
                    setGuidelines(CropImageView.Guidelines.ON)
                }
            )
        })

        binding.nicNameChangeBtn.setOnClickListener(View.OnClickListener {

            is_nicName_dup_check = false

            // Dialog만들기
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.user_profile_modify_activity_change_nicname, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("닉네임 변경")


            val  mAlertDialog = mBuilder.show()


            nicNameModifyEt = mDialogView.findViewById<EditText>(R.id.nicNameModifyEt)

            nicNameModifyEt.setText(binding.userProfileNicNameTv.text.toString())

            duplicationCheckBtn = mDialogView.findViewById<Button>(R.id.duplicationCheckBtn)

            nicnameModifyOkBtn = mDialogView.findViewById<Button>(R.id.nicnameModifyOkBtn)
            nicnameModifyCancelBtn = mDialogView.findViewById<Button>(R.id.nicnameModifyCancelBtn)


            change_ok_alert_tv = mDialogView.findViewById<Button>(R.id.change_ok_alert_tv)
            change_ok_alert_tv.setText("닉네임 중복확인이 필요합니다.")


            duplicationCheckBtn.setOnClickListener {

                nicNameModifyEt.setText(nicNameModifyEt.text.toString().replace(" ",""))

                if(nicNameModifyEt.text.toString().length>0){

                    if(MainActivity.loginUserNickname == nicNameModifyEt.text.toString()) {
                        Toast.makeText(this, "현재 사용중인 닉네임과 동일합니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        nicName_duplicate_check(nicNameModifyEt.text.toString())

                    }

                }else{

                    Toast.makeText(applicationContext, "닉네임을 입력해주세요.", Toast.LENGTH_LONG).show()

                }


            }

            nicnameModifyOkBtn.setOnClickListener {
                if(is_nicName_dup_check == true) {
                    binding.userProfileNicNameTv.setText(nicNameModifyEt.text.toString())
                    mAlertDialog.dismiss()
                }else{
                    Toast.makeText(this, "닉네임 중복확인이 필요합니다.", Toast.LENGTH_SHORT).show()
                }

            }


            nicnameModifyCancelBtn.setOnClickListener {
                Toast.makeText(this, "닉네임 변경을 취소하셨습니다.", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }

        })


        binding.profileModifyOkBtn.setOnClickListener(View.OnClickListener {

            if(imageChangeCheck==false){
                justStringUpload()
            }
            else{
                //ImageUpload(resultUri!!,getImageUri(applicationContext,resize_bitmap)!!)
                ImageUpload(userProfileImageUri,thumbnail_userProfileImageUri)
            }


        })





    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode) {

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {


//                    imageChangeCheck = true
//
//                    //이미지를 가져와서 이미지 뷰에 보여준다.
//                    val result = CropImage.getActivityResult(data)
//                    resultUri = result.uri
//                    binding.userProfileIv.setImageURI(Uri.parse(resultUri.toString()))
//
//
//                    //이미지 uri를 비트맵으로 변환한다.
//                    val bitmap =
//                        MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), resultUri)
//
//                    //썸네일 크기 줄이기 비트맵 크기를 1/2로 줄인다.
//
//                    var width : Int =bitmap.width
//                    var height : Int = bitmap.height
//
//
//                    if(width>100||height>100){
//                        resize_bitmap = Bitmap.createScaledBitmap(bitmap!!, 300, 300*height/width, true)
//
//                    }else{
//                        resize_bitmap = Bitmap.createScaledBitmap(bitmap!!, width/2, height/2, true)
//                    }
//
//                    //썸네일 용량 줄이기 비트맵 화질을 낮춘다.
//                    resize_bitmap = compressBitmap(resize_bitmap)!!
//
//
//
//
//
//                    Log.d("uri", resultUri.toString())
//                    Log.d("uri", getImageUri(applicationContext,bitmap).toString())
//                    Log.d("uri", getImageUri(applicationContext,resize_bitmap).toString())



                }
            }
        }
    }

    //비트맵 이미지를 압축시칸다.
    private fun compressBitmap(bitmap: Bitmap): Bitmap? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream)
        val byteArray = stream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    //비트맵 이미지 uri를 가져온다.
    fun getImageUri(inContext: Context?, inImage: Bitmap?): Uri? {

        val path = MediaStore.Images.Media.insertImage(inContext?.getContentResolver(), inImage, "Title"+ Calendar.getInstance().getTime(), null)

        Log.d("uri 나와라", path)
        return Uri.parse(path)
    }


    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }



    //이미지 전송 없이, 닉네임 또는 자기소개만 변경하는 경우
    fun justStringUpload(){

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

        var server = retrofit.create(API.UserProfileModifyStringChange_interface::class.java)

        var userModifyNicName = binding.userProfileNicNameTv.text.toString()
        var userModifyIntroduction = binding.CreateRoomActivityRoomInformationInput.text.toString()

        server.user_profile_just_String_modify_Request(MainActivity.user_table_id,userModifyNicName,userModifyIntroduction).enqueue(object:
            Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let { Log.d("레트로핏 결과1", it) }
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response?.isSuccessful) {
                    Log.d("이미지를 업로드했습니다",""+response?.body().toString())

                    var items : String? =  response.body()


                    //companion object의 닉네임 변수 변경한다.
                    MainActivity.loginUserNickname = userModifyNicName

                    //쉐어드 닉네임 변경한다.
                    val pref = getSharedPreferences("pref_user_data",0)
                    val edit = pref.edit()
                    if (edit != null) {
                        edit.putString("loginUserNickname", userModifyNicName)
                        edit.apply()//저장완료
                    }

                    //이전화면으로 돌아간다.
                    finish()




                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                    Log.d("이미지업로드실패",""+response?.body().toString())
                }
            }
        })
    }



    //서버로 이미지와 함께 정보를 변경할때
    fun ImageUpload(imageUri:String,thumbnail_ImageUri:String){

        //원본이미지
//        val uriPathHelper = URIPathHelper()
//        var filePath = imageUri?.let { uriPathHelper.getPath(this, it) }

        //creating a file
        val file = File(imageUri)

        //이미지의 확장자를 구한다.
        val extension = MimeTypeMap.getFileExtensionFromUrl(imageUri.toString())
        val sdf = SimpleDateFormat("yyyyMMddHHmmss")
        val date = sdf.format(Date())

        var fileName = MainActivity.loginUserId+date+"."+extension
        var requestBody : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(),file)
        var body : MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file",fileName,requestBody)


        //썸네일이미지
//        val thumbnail_uriPathHelper = URIPathHelper()
//        var thumbnail_filePath = thumbnail_ImageUri?.let { thumbnail_uriPathHelper.getPath(this, it) }

        //creating a file
        val thumbnail_file = File(thumbnail_ImageUri)

        //썸네일 이미지의 확장자는 압축할때 jpeg형식으로 압축하므로 jpeg이다.

        var thumbnail_fileName = MainActivity.loginUserId+date+".jpeg"
        var thumbnail_requestBody : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(),thumbnail_file)
        var thumbnail_body : MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file1",thumbnail_fileName,thumbnail_requestBody)



        //The gson builder
        var gson : Gson =  GsonBuilder()
            .setLenient()
            .create()


        //creating retrofit object
        var retrofit =
            Retrofit.Builder()
                .baseUrl(getString(R.string.http_request_base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(createOkHttpClient())
                .build()

        //creating our api

        var server = retrofit.create(API.UserProfileModifyImageChange_interface::class.java)

        var userModifyNicName = binding.userProfileNicNameTv.text.toString()
        var userModifyIntroduction = binding.CreateRoomActivityRoomInformationInput.text.toString()

        server.user_profile_modify_Request(body,thumbnail_body,MainActivity.user_table_id,userModifyNicName,userModifyIntroduction).enqueue(object:
            Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let { Log.d("레트로핏 결과1", it) }
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response?.isSuccessful) {
                    Log.e("이미지를 업로드했습니다",""+response?.body().toString())

                    var items : String? =  response.body()


                    //companion object의 닉네임 변수 변경한다.
                    MainActivity.loginUserNickname = userModifyNicName
                    MainActivity.userThumbnailImage = "images/profile_image/$fileName"


                    //쉐어드 닉네임 변경한다.
                    val pref = getSharedPreferences("pref_user_data",0)
                    val edit = pref.edit()
                    if (edit != null) {
                        edit.putString("loginUserNickname", userModifyNicName)
                        edit.putString("userThumbnailImage","images/profile_image/$fileName")

                        edit.apply()//저장완료
                    }

                    //이전화면으로 돌아간다.
                    finish()




                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                    Log.d("이미지업로드실패",""+response?.body().toString())
                }
            }
        })
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


    //권한 요청
    fun settingPermission(){
        var permis = object  : PermissionListener {
            //            어떠한 형식을 상속받는 익명 클래스의 객체를 생성하기 위해 다음과 같이 작성
            override fun onPermissionGranted() {
                Log.d("TAG", "권한 허가됨")
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@UserProfileModifyActivity, "권한 거부", Toast.LENGTH_SHORT)
                    .show()
                ActivityCompat.finishAffinity(this@UserProfileModifyActivity) // 권한 거부시 앱 종료
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


    //닉네임 중복 확인

    fun nicName_duplicate_check(nick_name:String){

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

        var server = retrofit.create(API.nicNameCheck::class.java)

        // 파일, 사용자 아이디, 파일이름
        server.checkNicName(nick_name).enqueue(object:
            Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let { Log.d("레트로핏 결과1", it) }
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response?.isSuccessful) {
                    if(response?.body().toString()=="true") {
                        Toast.makeText(getApplicationContext(), "이미 사용중인 닉네임입니다.", Toast.LENGTH_LONG)
                            .show();

                    }

                    if(response?.body().toString()=="false") {
                        Toast.makeText(getApplicationContext(), "사용할 수 있는 닉네임입니다.", Toast.LENGTH_LONG)
                            .show();

                        is_nicName_dup_check = true

                        duplicationCheckBtn.setEnabled(false)
                        duplicationCheckBtn.setBackgroundColor(Color.GRAY)
                        nicNameModifyEt.setEnabled(false)
                        change_ok_alert_tv.setText("사용할 수 있는 닉네임입니다.")



                    }

                    Log.d("레트로핏 성공결과",""+response?.body().toString())

                } else {
                    Toast.makeText(getApplicationContext(), "서버연결 실패.", Toast.LENGTH_LONG).show();
                    Log.d("레트로핏 실패결과",""+response?.body().toString())
                    Log.d("레트로핏 실패결과",""+call.request())

                }
            }
        })
    }


    fun userProfileLoading(user_tb_id:Int){
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
                Log.d(ReviewFragment.TAG, "프로필 정보 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "프로필 정보 : ${response.body().toString()}")

                var items : UserProfileData? =  response.body()



                //작성자 프로필
                Glide.with(applicationContext)
                    .load(getString(R.string.http_request_base_url)+items!!.profile_image)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.userProfileIv)

                binding.userProfileNicNameTv.text = MainActivity.loginUserNickname

                binding.CreateRoomActivityRoomInformationInput.setText(items!!.introduction)




            }

            override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
                Log.d("유저정보가져오기실패?", "실패 : $t")
            }
        })
    }


    override fun onBackPressed() {



        var builder = AlertDialog.Builder(this)
        builder.setTitle("프로필을 변경하시겠습니까?")
        builder.setMessage("확인버튼을 누르시면 프로필 정보가 변경됩니다.")

        // 버튼 클릭시에 무슨 작업을 할 것인가!
        var listener = object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (p1) {
                    DialogInterface.BUTTON_POSITIVE ->


                        if(imageChangeCheck==false){
                            justStringUpload()
                        }
                        else{
                            ImageUpload(userProfileImageUri,thumbnail_userProfileImageUri)
                        }



                    DialogInterface.BUTTON_NEGATIVE ->
                    {
                        finish()
                    }
                }
            }
        }

        builder.setPositiveButton("확인", listener)
        builder.setNegativeButton("취소", listener)


        builder.show()

    }


    //상단바 백버튼을 클릭한 경우
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {


                var builder = AlertDialog.Builder(this)
                builder.setTitle("프로필을 변경하시겠습니까?")
                builder.setMessage("확인버튼을 누르시면 프로필 정보가 변경됩니다.")

                // 버튼 클릭시에 무슨 작업을 할 것인가!
                var listener = object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when (p1) {
                            DialogInterface.BUTTON_POSITIVE ->


                                if(imageChangeCheck==false){
                                    justStringUpload()
                                }
                                else{
                                    ImageUpload(userProfileImageUri,thumbnail_userProfileImageUri)
                                }



                            DialogInterface.BUTTON_NEGATIVE ->
                                finish()
                        }
                    }
                }

                builder.setPositiveButton("확인", listener)
                builder.setNegativeButton("취소", listener)


                builder.show()


            }
        }
        return super.onOptionsItemSelected(item)
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