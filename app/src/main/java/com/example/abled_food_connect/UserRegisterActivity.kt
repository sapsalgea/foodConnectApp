package com.example.abled_food_connect

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.abled_food_connect.databinding.ActivityUserRegisterBinding
import com.example.abled_food_connect.interfaces.retrofit_interface
import com.example.abled_food_connect.retrofit.API
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UserRegisterActivity : AppCompatActivity() {


    //사진첩에서 이미지 가져올때 사용하는 변수
    lateinit var imageView: ImageView
    lateinit var button: Button
    lateinit var job:Job
    private val pickImage = 100
    private var imageUri: Uri? = null


    //카메라로 사진찍고 이미지 가져올때 사용하는 변수
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath: String


    //이미지 Uri
    lateinit var userProfileImageUri: String
    lateinit var thumbnail_userProfileImageUri: String


    //닉네임 중복 체크했는지 확인하는 변수
    var nicName_dup_check_str = "NO"


    //유저정보 테이블에 저장할 값
    lateinit var user_id: String
    lateinit var social_login_type: String
    lateinit var nick_name: String
    var profile_get_check: String = "NOIMAGE"
    lateinit var birth_year: String
    lateinit var user_gender: String
    lateinit var phone_number: String


    /*
       코틀린 뷰 바인딩을 적용시켰습니다.
     */
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityUserRegisterBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    //파이어베이스 전화번호 인증

    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    var phone_auth_isFisished: String = "NO"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // 기존 setContentView 를 제거해주시고..
        //setContentView(R.layout.activity_user_register)

        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityUserRegisterBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        //
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.
        // 뷰 id도 파스칼케이스 + 카멜케이스의 네이밍규칙 적용으로 인해서 nicName_Et -> nicNameEt 로 자동 변환 되었습니다.


        user_id = intent.getStringExtra("user_id").toString()
        social_login_type = intent.getStringExtra("social_login_type").toString()

        Log.d("로그인에서 받아온 값 : 유저 아이디", user_id)
        Log.d("로그인에서 받아온 값 : 소셜 타입", social_login_type)

        imageView = binding.userProfileIv


        // 카메라 및 이미지 권한체크
        settingPermission()


        val cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // use the returned uri
                Log.d("TAG", "석세스")
                val uriContent = result.originalUri
                val uriFilePath = result.getUriFilePath(this) // optional usage


                //profile_get_check에 이미지를 가져왔다는 표시를 해준다
                //ok가 아니면 가입완료를 눌렀을 경우, 이미지요청 토스트 메시지가 출력된다.
                profile_get_check = "OK"

                //이미지를 가져와서 이미지 뷰에 보여준다.
                binding.userProfileIv.setImageURI(uriContent)


                //이미지 uri를 비트맵으로 변환한다.
                val bitmap =
                    MediaStore.Images.Media.getBitmap(
                        applicationContext.getContentResolver(),
                        uriContent
                    )

                //썸네일 크기 줄이기 비트맵 크기를 1/2로 줄인다.

                var width: Int = bitmap.width
                var height: Int = bitmap.height

                var resize_bitmap: Bitmap
                if (width > 100 || height > 100) {
                    resize_bitmap =
                        Bitmap.createScaledBitmap(bitmap!!, 300, 300 * height / width, true)

                } else {
                    resize_bitmap = Bitmap.createScaledBitmap(bitmap!!, width / 2, height / 2, true)
                }

                //썸네일 용량 줄이기 비트맵 화질을 낮춘다.
                resize_bitmap = compressBitmap(resize_bitmap)!!


//                Log.d("uri", resultUri.toString())


                if (uriFilePath != null) {
                    userProfileImageUri = uriFilePath
                    thumbnail_userProfileImageUri =
                        getRealPathFromURI(getImageUri(applicationContext, resize_bitmap)!!)!!
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

        //이미지뷰를 누르면 갤러리 또는 카메라를 실행시킨다.
        binding.userProfileIv.setOnClickListener {

            cropImage.launch(
                options {
                    setGuidelines(CropImageView.Guidelines.ON)
                }
            )

        }


        //닉네임 글자수 10자 제한
        binding.nicNameEt.setMaxLength(10)


        binding.nicNameCheckBtn.setOnClickListener(View.OnClickListener {

            binding.nicNameEt.setText(binding.nicNameEt.text.toString().replace(" ", ""))
            if (binding.nicNameEt.text.toString().length > 0) {
                nicName_duplicate_check(binding.nicNameEt.text.toString())
                Log.d("이름", binding.nicNameEt.text.toString())
            } else {
                Toast.makeText(applicationContext, "닉네임을 입력해주세요.", Toast.LENGTH_LONG).show()
            }

        })


        binding.nicNameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nicName_dup_check_str = "NO"
            }
        })


        //출생연도 선택버튼

        //기본값은 ""
        //값이 없는 경우, 가입 시 선택해주세요라는 토스트메시지가 생긴다..
        birth_year = ""

        binding.birthYearBtn.setOnClickListener {

            val dialog = AlertDialog.Builder(this).create()
            val edialog: LayoutInflater = LayoutInflater.from(this)
            val mView: View = edialog.inflate(R.layout.dialog_datepicker, null)

            val year: NumberPicker = mView.findViewById(R.id.yearpicker_datepicker)

            val cancel: Button = mView.findViewById(R.id.cancel_button_datepicker)
            val save: Button = mView.findViewById(R.id.save_button_datepicker)


            //  순환 안되게 막기
            year.wrapSelectorWheel = false


            //  editText 설정 해제
            year.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


            //  최소값 설정
            year.minValue = 1900
            //  최대값 설정
            year.maxValue = 2020

            year.value = 1990


            //  취소 버튼 클릭 시
            cancel.setOnClickListener {
                dialog.dismiss()
                dialog.cancel()
            }

            //  완료 버튼 클릭 시
            save.setOnClickListener {

                birth_year = (year.value).toString()
                binding.birthYearBtn.setText(birth_year + "년")
                Log.d("나와", (year.value).toString() + "년")
                // month_textview_statsfrag.text = (month.value).toString() + "월"

                dialog.dismiss()
                dialog.cancel()
            }
            dialog.setView(mView)
            dialog.create()
            dialog.show()

        }

        // 라디오 버튼 상태를 바꿨을때, 자동으로 나타나는 상태 표시(람다식)
        binding.genderSelectRg.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.genderManRb ->
                    user_gender = "MAN"
                R.id.genderWomanRb ->
                    user_gender = "WOMAN"

            }
            Log.d("무슨성별선택?", user_gender)
        }


        /*
        파이어베이스 인증
         */

        auth = FirebaseAuth.getInstance()






        binding.firebaseCodeSendBtn.setOnClickListener {
            login()
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                //Toast.makeText(applicationContext, "야호.", Toast.LENGTH_LONG).show()
//                startActivity(Intent(applicationContext, Home::class.java))
//                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, "정확한 전화번호를 입력해주세요.", Toast.LENGTH_LONG)
                    .show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG", "onCodeSent:$verificationId")
                if (verificationId != null) {
                    storedVerificationId = verificationId
                    resendToken = token
                    Toast.makeText(applicationContext, "코드를 발송했습니다.", Toast.LENGTH_SHORT).show()
                }

            }
        }


        binding.firebaseCodeCheckBtn.setOnClickListener {
            var otp = binding.firebaseCodeInputEt.text.toString().trim()
            if (!otp.isEmpty()) {
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp
                )
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "다시 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }


        binding.serviceTermsTextOpenBtn2.setOnClickListener {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("서비스 이용 약관")


            var v1 = layoutInflater.inflate(R.layout.user_register_terms, null)
            builder.setView(v1)

            var userRegisterTermsTv = v1.findViewById<TextView>(R.id.userRegisterTermsTv)

            userRegisterTermsTv.text = "${serviceTermsStr}"

            builder.setNegativeButton("확인", null)

            builder.show()
        }




        binding.serviceTermsTextOpenBtn3.setOnClickListener {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("개인정보 수집 및 이용 동의")


            var v1 = layoutInflater.inflate(R.layout.user_register_terms, null)
            builder.setView(v1)

            var userRegisterTermsTv = v1.findViewById<TextView>(R.id.userRegisterTermsTv)

            userRegisterTermsTv.text = "${personalInfoUsingTerm}"

            builder.setNegativeButton("확인", null)

            builder.show()
        }



        binding.serviceTermsTextOpenBtn4.setOnClickListener {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("위치정보 수집 및 이용 동의")


            var v1 = layoutInflater.inflate(R.layout.user_register_terms, null)
            builder.setView(v1)

            var userRegisterTermsTv = v1.findViewById<TextView>(R.id.userRegisterTermsTv)

            userRegisterTermsTv.text = "${locationInfoTerm}"

            builder.setNegativeButton("확인", null)

            builder.show()
        }

        binding.checkAllCheckBox.setOnClickListener {
            if (binding.checkAllCheckBox.isChecked == true) {
                binding.checkbox1.isChecked = true
                binding.checkbox2.isChecked = true
                binding.checkbox3.isChecked = true
                binding.checkbox4.isChecked = true
            } else {
                binding.checkbox1.isChecked = false
                binding.checkbox2.isChecked = false
                binding.checkbox3.isChecked = false
                binding.checkbox4.isChecked = false
            }
        }







        binding.userRegisterBtn.setOnClickListener {


            if (profile_get_check == "NOIMAGE") {
                Toast.makeText(this, "프로필 사진을 등록해주세요.", Toast.LENGTH_SHORT).show()
            } else if (nicName_dup_check_str == "NO") {
                Toast.makeText(this, "닉네임 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
            } else if (birth_year.equals("")) {
                Toast.makeText(this, "출생연도를 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else if (!binding.genderManRb.isChecked() && !binding.genderWomanRb.isChecked()) {
                Toast.makeText(this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else if (phone_auth_isFisished.equals("NO")) {
                Toast.makeText(this, "전화번호 인증을 완료해주세요.", Toast.LENGTH_SHORT).show()
            }

            //약관 동의 버튼 확인

            else if (binding.checkbox1.isChecked == false) {
                Toast.makeText(this, "만 18세 이상인지 체크해주세요", Toast.LENGTH_SHORT).show()
            } else if (binding.checkbox2.isChecked == false) {
                Toast.makeText(this, "서비스 이용 약관을 체크해주세요.", Toast.LENGTH_SHORT).show()
            } else if (binding.checkbox3.isChecked == false) {
                Toast.makeText(this, "개인정보 수집 및 이용 동의 약관을 체크해주세요.", Toast.LENGTH_SHORT).show()
            } else if (binding.checkbox4.isChecked == false) {
                Toast.makeText(this, "위치정보 수집 및 이용 동의 약관을 체크해주세요.", Toast.LENGTH_SHORT).show()
            } else {

                RegistUserInfo(userProfileImageUri, thumbnail_userProfileImageUri)
            }


        }


    }

    fun EditText.setMaxLength(maxLength: Int) {
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
    }

    // onDestroy
    override fun onDestroy() {
        super.onDestroy()
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mBinding = null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {

                    //profile_get_check에 글을 넣어준다
                    Log.d("TAG", "크롭이미지 리퀘스트코드")

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

        val path = MediaStore.Images.Media.insertImage(
            inContext?.getContentResolver(),
            inImage,
            "Title" + Calendar.getInstance().getTime(),
            null
        )

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


    fun RegistUserInfo(imageUri: String, thumbnail_ImageUri: String) {

//        //원본이미지
//        val uriPathHelper = UserProfileModifyActivity.URIPathHelper()
//        var filePath = imageUri?.let { uriPathHelper.getPath(this, it) }

        //creating a file
        val file = File(imageUri)

        //이미지의 확장자를 구한다.
        val extension = MimeTypeMap.getFileExtensionFromUrl(imageUri)


        var fileName = user_id + "." + extension
        var requestBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        var body: MultipartBody.Part =
            MultipartBody.Part.createFormData("uploaded_file", fileName, requestBody)


//        //썸네일이미지
//        val thumbnail_uriPathHelper = UserProfileModifyActivity.URIPathHelper()
//        var thumbnail_filePath = thumbnail_ImageUri?.let { thumbnail_uriPathHelper.getPath(this, it) }

        //creating a file
        val thumbnail_file = File(thumbnail_ImageUri)

        //이미지의 확장자를 구한다.
        val thumbnail_extension = MimeTypeMap.getFileExtensionFromUrl(thumbnail_ImageUri)


        //var thumbnail_fileName = MainActivity.user_table_id.toString()+"."+thumbnail_extension
        var thumbnail_fileName = user_id + ".jpeg"
        var thumbnail_requestBody: RequestBody =
            RequestBody.create("image/*".toMediaTypeOrNull(), thumbnail_file)
        var thumbnail_body: MultipartBody.Part = MultipartBody.Part.createFormData(
            "uploaded_file1",
            thumbnail_fileName,
            thumbnail_requestBody
        )


        //The gson builder
        var gson: Gson = GsonBuilder()
            .setLenient()
            .create()


        //creating retrofit object
        var retrofit =
            Retrofit.Builder()
                .baseUrl(getString(R.string.http_request_base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        //creating our api

        var server = retrofit.create(retrofit_interface::class.java)

        // 파일, 사용자 아이디, 파일이름

        nick_name = binding.nicNameEt.text.toString()
        //birth_year 데이터 피커에서 값을 받는다.
        //user_gender = binding.userGenderEt.text.toString()
        phone_number = binding.phoneNumberInputEt.text.toString()

        server.post_Porfile_Request(
            user_id,
            social_login_type,
            nick_name,
            birth_year,
            user_gender,
            phone_number,
            body,
            thumbnail_body
        ).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let { Log.d("레트로핏 결과1", it) }
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response?.isSuccessful) {
                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG)
                        .show();
                    Log.d("레트로핏 결과2", "" + response?.body().toString())
                    var nextIntent: Intent =
                        Intent(this@UserRegisterActivity, MainActivity::class.java)
                    startActivity(nextIntent)
                    finish()

                } else {
                    Toast.makeText(
                        getApplicationContext(),
                        "Some error occurred...",
                        Toast.LENGTH_LONG
                    ).show();
                    Log.d("레트로핏 실패결과", "" + response?.body().toString())
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
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
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

        fun getDataColumn(
            context: Context,
            uri: Uri?,
            selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor = uri?.let {
                    context.getContentResolver()
                        .query(it, projection, selection, selectionArgs, null)
                }
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


    fun settingPermission() {
        var permis = object : PermissionListener {
            //            어떠한 형식을 상속받는 익명 클래스의 객체를 생성하기 위해 다음과 같이 작성
            override fun onPermissionGranted() {
//                Toast.makeText(this@UserRegisterActivity, "권한 허가", Toast.LENGTH_SHORT)
//                    .show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@UserRegisterActivity, "앱 필수권한을 허용해주세요.", Toast.LENGTH_SHORT)
                    .show()
                ActivityCompat.finishAffinity(this@UserRegisterActivity) // 권한 거부시 앱 종료
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permis)
            .setRationaleMessage("카메라 사진 권한 필요")
            .setDeniedMessage("카메라 권한 요청 거부")
            .setPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            )
            .check()
    }

    fun startCapture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.abled_food_connect.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }


    /*
    파이어베이스 인증에 사용되는 메서드
     */


    @DelicateCoroutinesApi
    @SuppressLint("SetTextI18n")
    private fun login() {
        val mobileNumber = binding.phoneNumberInputEt
        var number = mobileNumber.text.toString().trim()

        if (!number.isEmpty()) {
            number = "+82" + number
            sendVerificationcode(number)
            binding.UserRegisterTimerLinear.visibility = View.VISIBLE
            binding.firebaseCodeCheckBtn.isEnabled = true
            binding.firebaseCodeCheckBtn.setBackgroundColor(Color.BLACK)
            if(this::job.isInitialized){
            if(job.isActive){
                job.cancel()
            }}
           GlobalScope.launch {  job = launch {
               for (i in 60 downTo 0) {

                   runOnUiThread {
                       binding.UserRegisterTimerTv.text = "남은 시간 : ${i}초"
                       if (i == 0 || phone_auth_isFisished == "YES") {
                           binding.UserRegisterTimerLinear.visibility = View.GONE
                           binding.firebaseCodeCheckBtn.isEnabled = false
                           binding.firebaseCodeCheckBtn.setBackgroundColor(Color.GRAY)

                       }
                   }
                   if (i == 0 || phone_auth_isFisished == "YES") {
                       job.cancel()
                                        }
                   delay(1000L)
               }
           } }



//                Handler().post {
//                    binding.UserRegisterTimerLinear.visibility = View.GONE
//                    binding.firebaseCodeSendBtn.isEnabled = false
//                }


        } else {
            Toast.makeText(this, "정확한 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationcode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "인증되었습니다.", Toast.LENGTH_SHORT).show()

                    binding.firebaseCodeCheckBtn.setEnabled(false)
                    binding.firebaseCodeCheckBtn.setBackgroundColor(Color.GRAY)

                    binding.firebaseCodeSendBtn.setEnabled(false)
                    binding.firebaseCodeSendBtn.setBackgroundColor(Color.GRAY)
                    binding.phoneNumberInputEt.setEnabled(false)
                    binding.firebaseCodeInputEt.setEnabled(false)

                    phone_auth_isFisished = "YES"

// ...
                } else {
// Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
// The verification code entered was invalid
                        Toast.makeText(this, "인증코드를 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }


    //닉네임 중복 확인

    fun nicName_duplicate_check(nick_name: String) {

        //The gson builder
        var gson: Gson = GsonBuilder()
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
        server.checkNicName(nick_name).enqueue(object :
            Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let { Log.d("레트로핏 결과1", it) }
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response?.isSuccessful) {
                    if (response?.body().toString() == "true") {
                        Toast.makeText(
                            getApplicationContext(),
                            "이미 사용중인 닉네임입니다.",
                            Toast.LENGTH_LONG
                        )
                            .show();

                    }

                    if (response?.body().toString() == "false") {
                        Toast.makeText(
                            getApplicationContext(),
                            "사용할 수 있는 닉네임입니다.",
                            Toast.LENGTH_LONG
                        )
                            .show();
                        nicName_dup_check_str = "YES"

                        binding.nicNameCheckBtn.setEnabled(false)
                        binding.nicNameCheckBtn.setBackgroundColor(Color.GRAY)
                        binding.nicNameEt.setEnabled(false)


                    }

                    Log.d("레트로핏 성공결과", "" + response?.body().toString())

                } else {
                    Toast.makeText(getApplicationContext(), "서버연결 실패.", Toast.LENGTH_LONG).show();
                    Log.d("레트로핏 실패결과", "" + response?.body().toString())
                    Log.d("레트로핏 실패결과", "" + call.request())

                }
            }
        })
    }


    val serviceTermsStr = "제 1 장 환영합니다!\n" +
            "제 1 조 (목적)\n" +
            "주식회사 푸드커넥트(이하 ‘회사’)가 제공하는 서비스를 이용해 주셔서 감사합니다. 회사는 여러분이 다양한 인터넷과 모바일 서비스를 좀 더 편리하게 이용할 수 있도록 회사 또는 관계사의 개별 서비스에 모두 접속 가능한 통합로그인계정 체계를 만들고 그에 적용되는 '푸드커넥트계정 약관(이하 '본 약관')을 마련하였습니다. 본 약관은 여러분이 푸드커넥트계정 서비스를 이용하는 데 필요한 권리, 의무 및 책임사항, 이용조건 및 절차 등 기본적인 사항을 규정하고 있으므로 조금만 시간을 내서 주의 깊게 읽어주시기 바랍니다.\n" +
            "제 2 조 (약관의 효력 및 변경)\n" +
            "① 본 약관의 내용은 푸드커넥트계정 \n" +
            "개별 서비스의 화면에 게시하거나 기타의 방법으로 공지하고, 본 약관에 동의한 여러분 모두에게 그 효력이 발생합니다.\n" +
            "② 회사는 필요한 경우 관련법령을 위배하지 않는 범위 내에서 본 약관을 변경할 수 있습니다. 본 약관이 변경되는 경우 회사는 변경사항을 시행일자 15일 전부터 여러분에게 서비스 공지사항에서 공지 또는 통지하는 것을 원칙으로 하며, 피치 못하게 여러분에게 불리한 내용으로 변경할 경우에는 그 시행일자 30일 전부터 푸드커넥트계정에 등록된 이메일 주소로 이메일(이메일주소가 없는 경우 서비스 내 전자쪽지 발송, 서비스 내 알림 메시지를 띄우는 등의 별도의 전자적 수단) 발송 또는 여러분이 등록한 휴대폰번호로 푸드커넥트톡 메시지 또는 문자메시지 발송하는 방법 등으로 개별적으로 알려 드리겠습니다.\n" +
            "③ 회사가 전항에 따라 공지 또는 통지를 하면서 공지 또는 통지일로부터 개정약관 시행일 7일 후까지 거부의사를 표시하지 아니하면 승인한 것으로 본다는 뜻을 명확하게 고지하였음에도 여러분의 의사표시가 없는 경우에는 변경된 약관을 승인한 것으로 봅니다. 여러분이 개정약관에 동의하지 않을 경우 여러분은 이용계약을 해지할 수 있습니다.\n" +
            "제 3 조 (약관 외 준칙)\n" +
            "본 약관에 규정되지 않은 사항에 대해서는 관련법령 또는 회사가 정한 개별 서비스의 이용약관, 운영정책 및 규칙 등(이하 ‘세부지침’)의 규정에 따릅니다.\n" +
            "제 4 조 (용어의 정의)\n" +
            "① 본 약관에서 사용하는 용어의 정의는 다음과 같습니다.\n" +
            "1. 푸드커넥트계정: 회사 또는 관계사가 제공하는 개별 서비스를 하나의 로그인계정과 비밀번호로 회원 인증, 회원정보 변경, 회원 가입 및 탈퇴 등을 관리할 수 있도록 회사가 정한 로그인계정 정책을 말합니다.\n" +
            "2. 회원: 푸드커넥트계정이 적용된 개별 서비스 또는 푸드커넥트계정 웹사이트에서 본 약관에 동의하고, 푸드커넥트계정을 이용하는 자를 말합니다.\n" +
            "3. 관계사: 회사와 제휴 관계를 맺고 푸드커넥트계정을 공동 제공하기로 합의한 법인을 말합니다. 개별 관계사는 푸드커넥트 기업사이트에서 확인할 수 있고 추후 추가/변동될 수 있으며 관계사가 추가/변동될 때에는 푸드커넥트 기업사이트에 변경 사항을 게시합니다.\n" +
            "4. 개별 서비스: 푸드커넥트계정을 이용하여 접속 가능한 회사 또는 관계사가 제공하는 서비스를 말합니다. 개별 서비스는 추후 추가/변동될 수 있으며 서비스가 추가/변동될 때에는 푸드커넥트 기업사이트에 변경 사항을 게시합니다.\n" +
            "5. 푸드커넥트계정 웹사이트: 회원이 온라인을 통해 푸드커넥트계정 정보를 조회 및 수정할 수 있는 인터넷 사이트를 말합니다.\n" +
            "6. 푸드커넥트계정 정보 : 푸드커넥트계정을 이용하기 위해 회사가 정한 필수 내지 선택 입력 정보로서 푸드커넥트계정 웹사이트 또는 개별 서비스 내 푸드커넥트계정 설정 화면을 통해 정보 확인, 변경 처리 등을 관리할 수 있는 회원정보 항목을 말합니다.\n" +
            "8.인증서 : 인증서라 함은 회사가 인증서비스를 통하여 발급하는 전자서명생성정보가 회원에게 유일하게 속한다는 사실 등을 확인하고 이를 증명하는 전자적 정보를 말합니다.\n" +
            "9.전자서명 : 서명자의 신원을 확인하고 서명자가 해당 전자문서에 서명하였다는 사실을 나타내는데 이용하기 위하여 전자문서에 첨부되거나 논리적으로 결합된 전자적 형태의 정보를 말합니다.\n" +
            "10.전자서명생성정보 : 전자서명을 생성하기 위하여 이용하는 전자적 정보를 말합니다.\n" +
            "11.인증회원 : 회사로부터 전자서명생성정보를 인증 받은 회원을 말합니다.\n" +
            "12.이용기관 : 인증회원의 전자서명 및 인증서를 바탕으로 한 거래 등을 위하여 인증서비스를 이용하려는 제3자를 말합니다.\n" +
            "제 2 장 푸드커넥트계정 이용계약\n" +
            "제 5 조 (계약의 성립)\n" +
            "① 푸드커넥트계정 이용 신청은 개별 서비스 또는 푸드커넥트계정 웹사이트 회원가입 화면에서 여러분이 푸드커넥트계정 정보에 일정 정보를 입력하는 방식으로 이루어집니다.\n" +
            "② 푸드커넥트계정 이용계약은 여러분이 본 약관의 내용에 동의한 후 본 조 제1항에서 정한 이용신청을 하면 회사가 입력된 일정 정보를 인증한 후 가입을 승낙함으로써 체결됩니다.\n" +
            "제 6 조 (푸드커넥트계정 이용의 제한)\n" +
            "① 제5조에 따른 가입 신청자에게 회사는 원칙적으로 푸드커넥트계정의 이용을 승낙합니다. 다만, 회사는 아래 각 호의 경우에는 그 사유가 해소될 때까지 승낙을 유보하거나 승낙하지 않을 수 있습니다. 특히, 여러분이 만 14세 미만인 경우에는 부모님 등 법정대리인의 동의가 있는 경우에만 푸드커넥트계정을 생성할 수 있습니다.\n" +
            "1. 회사가 본 약관 또는 세부지침에 의해 여러분의 푸드커넥트계정을 삭제하였던 경우\n" +
            "2. 여러분이 다른 사람의 명의나 이메일 주소 등 개인정보를 이용하여 푸드커넥트계정을 생성하려 한 경우\n" +
            "3. 푸드커넥트계정 생성 시 필요한 정보를 입력하지 않거나 허위의 정보를 입력한 경우\n" +
            "4. 제공 서비스 설비 용량에 현실적인 여유가 없는 경우\n" +
            "5. 서비스 제공을 위한 기술적인 부분에 문제가 있다고 판단되는 경우\n" +
            "6. 기타 회사가 재정적, 기술적으로 필요하다고 인정하는 경우\n" +
            "7. 회사로부터 회원자격정지 조치 등을 받은 회원이 그 조치기간에 이용계약을 임의로 해지하고 재이용을 신청하는 경우\n" +
            "8. 기타 관련법령에 위배되거나 세부지침 등 회사가 정한 기준에 반하는 경우\n" +
            "② 만약, 여러분이 위 조건에 위반하여 푸드커넥트계정을 생성한 것으로 판명된 때에는 회사는 즉시 여러분의 푸드커넥트계정 이용을 정지시키거나 푸드커넥트계정을 삭제하는 등 적절한 제한을 할 수 있습니다.\n" +
            "제 3 장 푸드커넥트계정 이용\n" +
            "제 7 조 (푸드커넥트계정 제공)\n" +
            "① 회사가 개별 서비스와 연동하여 푸드커넥트계정에서 제공하는 서비스(이하 “푸드커넥트계정 서비스” 또는 “서비스”) 내용은 아래와 같습니다.\n" +
            "1. 통합로그인 : 푸드커넥트계정이 적용된 개별 서비스에서 하나의 푸드커넥트계정과 비밀번호로 로그인할 수 있는 통합 회원 인증 서비스를 이용할 수 있습니다.\n" +
            "2. SSO(Single Sign On): 웹브라우저나 특정 모바일 기기에서 푸드커넥트계정 1회 로그인으로 여러분이 이용 중인 개별 서비스간 추가 로그인 없이 자동 접속 서비스를 이용할 수 있습니다.\n" +
            "3. 푸드커넥트계정 정보 통합 관리 : 개별 서비스 이용을 위해 푸드커넥트계정 정보를 통합 관리합니다. 또한, 여러분이 이용하고자 하는 개별 서비스의 유형에 따라 전문기관을 통한 실명확인 및 본인인증을 요청할 수 있고, 이를 푸드커넥트계정 정보로 저장합니다.\n" +
            "4. 기타 회사가 제공하는 서비스\n" +
            "② 회사는 더 나은 푸드커넥트계정 서비스의 제공을 위하여 여러분에게 서비스의 이용과 관련된 각종 고지, 관리 메시지 및 기타 광고를 비롯한 다양한 정보를 서비스화면 내에 표시하거나 여러분의 이메일로 전송할 수 있습니다. 광고성 정보 전송의 경우에는 사전에 수신에 동의한 경우에만 전송합니다.\n" +
            "제 8 조 (푸드커넥트계정 서비스의 변경 및 종료)\n" +
            "① 회사는 푸드커넥트계정 서비스를 365일, 24시간 쉬지 않고 제공하기 위하여 최선의 노력을 다합니다. 다만, 아래 각 호의 경우 푸드커넥트계정 서비스의 전부 또는 일부를 제한하거나 중지할 수 있습니다.\n" +
            "1. 푸드커넥트계정 서비스용 설비의 유지·보수 등을 위한 정기 또는 임시 점검의 경우\n" +
            "2. 정전, 제반 설비의 장애 또는 이용량의 폭주 등으로 정상적인 푸드커넥트계정 이용에 지장이 있는 경우\n" +
            "3. 관계사와의 계약 종료, 정부의 명령/규제 등 회사의 제반 사정으로 푸드커넥트계정 서비스를 유지할 수 없는 경우\n" +
            "4. 기타 천재지변, 국가비상사태 등 불가항력적 사유가 있는 경우\n" +
            "② 전항에 의한 푸드커넥트계정 서비스 중단의 경우에는 미리 제12조에서 정한 방법으로 여러분에게 통지 내지 공지하겠습니다. 다만, 회사로서도 예측할 수 없거나 통제할 수 없는 사유(회사의 과실이 없는 디스크 내지 서버 장애, 시스템 다운 등)로 인해 사전 통지 내지 공지가 불가능한 경우에는 그러하지 아니합니다. 이러한 경우에도 회사가 상황을 파악하는 즉시 최대한 빠른 시일 내에 서비스를 복구하도록 노력하겠습니다.\n" +
            "③ 여러분에게 중대한 영향을 미치는 서비스의 변경 사항이나 종료는 푸드커넥트계정에 등록된 이메일 주소로 이메일(이메일주소가 없는 경우 서비스 내 전자쪽지 발송, 서비스 내 알림 메시지를 띄우는 등의 별도의 전자적 수단) 발송 또는 여러분이 등록한 휴대폰번호로 푸드커넥트톡 메시지 또는 문자메시지 발송하는 방법 등으로 개별적으로 알려 드리겠습니다.\n" +
            "제 9 조 (푸드커넥트계정 관리)\n" +
            "① 푸드커넥트계정은 여러분 본인만 이용할 수 있으며, 다른 사람이 여러분의 푸드커넥트계정을 이용하도록 허락할 수 없습니다. 그리고 여러분은 다른 사람이 여러분의 푸드커넥트계정을 무단으로 사용할 수 없도록 직접 비밀번호를 관리하여야 합니다. 회사는 다른 사람이 여러분의 푸드커넥트계정을 무단으로 사용하는 것을 막기 위하여 비밀번호 입력 및 추가적인 본인 확인 절차를 거치도록 할 수 있습니다. 만약 무단 사용이 발견된다면, 고객센터를 통하여 회사에게 알려주시기 바라며, 회사는 무단 사용을 막기 위한 방법을 여러분에게 안내하도록 하겠습니다.\n" +
            "② 여러분은 푸드커넥트계정 웹사이트 또는 개별 서비스 내 푸드커넥트계정 설정 화면을 통하여 여러분의 푸드커넥트계정 정보를 열람하고 수정할 수 있습니다. 다만, 푸드커넥트계정 서비스의 제공 및 관리를 위해 필요한 푸드커넥트계정, 전화번호, 단말기 식별번호, 기타 본인확인정보 등 일부 정보는 수정이 불가능할 수 있으며, 수정하는 경우에는 추가적인 본인 확인 절차가 필요할 수 있습니다. 여러분이 이용 신청 시 알려주신 내용에 변동이 있을 때, 직접 수정하시거나 이메일, 고객센터를 통하여 회사에 알려 주시기 바랍니다.\n" +
            "③ 여러분이 푸드커넥트계정 정보를 적시에 수정하지 않아 발생하는 문제에 대하여 회사는 책임을 부담하지 아니합니다.\n" +
            "제 4 장 계약당사자의 의무\n" +
            "제 12 조 (회원의 의무)\n" +
            "① 여러분이 푸드커넥트계정 서비스를 이용할 때 아래 각 호의 행위는 하여서는 안 됩니다.\n" +
            "1. 이용 신청 또는 변경 시 허위 사실을 기재하거나, 다른 회원의 푸드커넥트계정 및 비밀번호를 도용, 부정하게 사용하거나, 다른 사람의 명의를 사용하거나 명의자의 허락 없이 문자메시지(SMS) 인증 등을 수행하는 행위\n" +
            "2. 타인의 명예를 손상시키거나 불이익을 주는 행위\n" +
            "3. 게시판 등에 음란물을 게재하거나 음란사이트를 연결(링크)하는 행위\n" +
            "4. 회사 또는 제3자의 저작권 등 기타 권리를 침해하는 행위\n" +
            "5. 공공질서 및 미풍양속에 위반되는 내용의 정보, 문장, 도형, 음성 등을 타인에게 유포하는 행위\n" +
            "6. 푸드커넥트계정 서비스와 관련된 설비의 오동작이나 정보 등의 파괴 및 혼란을 유발시키는 컴퓨터 바이러스 감염 자료를 등록 또는 유포하는 행위\n" +
            "7. 푸드커넥트계정 서비스의 운영을 고의로 방해하거나 안정적 운영을 방해할 수 있는 정보 및 수신자의 명시적인 수신거부의사에 반하여 광고성 정보 또는 스팸메일(Spam Mail)을 전송하는 행위\n" +
            "8. 회사의 동의 없이 서비스 또는 이에 포함된 소프트웨어의 일부를 복사, 수정, 배포, 판매, 양도, 대여, 담보제공하거나 타인에게 그 이용을 허락하는 행위와 소프트웨어를 역설계하거나 소스 코드의 추출을 시도하는 등 서비스를 복제, 분해 또는 모방하거나 기타 변형하는 행위\n" +
            "9. 타인으로 가장하는 행위 및 타인과의 관계를 허위로 명시하는 행위\n" +
            "10. 다른 회원의 개인정보를 수집, 저장, 공개하는 행위\n" +
            "11. 자기 또는 타인에게 재산상의 이익을 주거나 타인에게 손해를 가할 목적으로 허위의 정보를 유통시키는 행위\n" +
            "12. 윤락행위를 알선하거나 음행을 매개하는 내용의 정보를 유통시키는 행위\n" +
            "13. 수치심이나 혐오감 또는 공포심을 일으키는 말이나 음향, 글이나 화상 또는 영상을 계속하여 상대방에게 도달하게 하여 상대방의 일상적 생활을 방해하는 행위\n" +
            "14. 관련 법령에 의하여 그 전송 또는 게시가 금지되는 정보(컴퓨터 프로그램 포함)의 전송 또는 게시 행위\n" +
            "15. 회사 또는 관계사의 직원이나 운영자를 가장하거나 사칭하여 또는 타인의 명의를 도용하여 글을 게시하거나 E-mail, 푸드커넥트톡 메시지 등을 발송하는 행위\n" +
            "16. 컴퓨터 소프트웨어, 하드웨어, 전기통신 장비의 정상적인 가동을 방해, 파괴할 목적으로 고안된 소프트웨어 바이러스, 기타 다른 컴퓨터 코드, 파일, 프로그램을 포함하고 있는 자료를 게시하거나 E-mail, 푸드커넥트톡 메시지 등으로 발송하는 행위\n" +
            "17. 기타 불법한 행위\n" +
            "② 여러분은 서비스의 이용권한, 기타 이용계약상 지위를 타인에게 양도·증여할 수 없으며, 담보로 제공할 수 없습니다.\n" +
            "③ 혹시라도 여러분이 관련 법령, 회사의 모든 약관 또는 정책을 준수하지 않는다면, 회사는 여러분의 위반행위 등을 조사할 수 있고, 여러분의 서비스 이용을 잠시 또는 계속하여 중단하거나, 재가입에 제한을 둘 수도 있습니다.\n" +
            "④ 본 조에서 정한 사항 및 그 밖에 푸드커넥트계정 서비스의 이용에 관한 자세한 사항은 푸드커넥트 운영정책 등을 참고해 주시기 바랍니다.\n" +
            "제 13 조 (개인정보의 보호)\n" +
            "여러분의 개인정보의 안전한 처리는 회사에게 있어 가장 중요한 일 중 하나입니다. 여러분의 개인정보는 서비스의 원활한 제공을 위하여 여러분이 동의한 목적과 범위 내에서만 이용됩니다. 법령에 의하거나 여러분이 별도로 동의하지 아니하는 한 회사가 여러분의 개인정보를 제3자에게 제공하는 일은 결코 없으므로, 안심하셔도 좋습니다. 회사가 여러분의 개인정보를 안전하게 처리하기 위하여 기울이는 노력이나 기타 자세한 사항은 푸드커넥트 개인정보처리방침을 참고하여 주십시오.\n" +
            "제 14 조 (회원에 대한 통지 및 공지)\n" +
            "회사는 여러분과의 의견 교환을 소중하게 생각합니다. 여러분은 언제든지 고객센터에 방문하여 의견을 개진할 수 있습니다. 서비스 이용자 전체에 대한 공지는 칠(7)일 이상 서비스 공지사항란에 게시함으로써 효력이 발생합니다. 여러분에게 중대한 영향을 미치는 사항의 경우에는 푸드커넥트계정에 등록된 이메일 주소로 이메일(이메일주소가 없는 경우 서비스 내 전자쪽지 발송, 서비스 내 알림 메시지를 띄우는 등의 별도의 전자적 수단) 발송 또는 여러분이 등록한 휴대폰번호로 푸드커넥트톡 메시지 또는 문자메시지 발송하는 방법 등으로 개별적으로 알려 드리겠습니다.\n" +
            "제 5 장 이용계약 해지 등\n" +
            "제 15 조 (이용계약 해지)\n" +
            "① 여러분이 푸드커넥트계정 이용을 더 이상 원치 않는 때에는 언제든지 서비스 내 제공되는 메뉴를 이용하여 이용계약의 해지 신청을 할 수 있으며, 회사는 법령이 정하는 바에 따라 신속히 처리하겠습니다.\n" +
            "② 회사는 법령에서 정하는 기간 동안 여러분이 푸드커넥트계정 서비스를 이용하기 위해 푸드커넥트계정 로그인 혹은 접속한 기록이 없는 경우 여러분이 등록한 이메일주소, 휴대폰번호로 이메일, 문자메시지 또는 푸드커넥트톡 메시지를 보내는 등 기타 유효한 수단으로 통지 후 여러분의 푸드커넥트계정 정보를 파기하거나 분리 보관할 수 있으며, 이로 인해 푸드커넥트계정 서비스 이용을 위한 필수적인 정보가 부족할 경우 이용계약이 해지될 수도 있습니다. 이와 관련된 보다 자세한 사항은 푸드커넥트 운영정책의 푸드커넥트 서비스 휴면 정책 부분을 꼭 확인하시기 바랍니다.\n" +
            "③ 이용계약이 해지되면 법령 및 개인정보 처리방침에 따라 여러분의 정보를 보유하는 경우를 제외하고는 여러분의 푸드커넥트계정 정보 및 푸드커넥트계정으로 이용하였던 개별 서비스 데이터는 삭제됩니다. 다만, 여러분이 개별 서비스 내에서 작성한 게시물 등 모든 데이터의 삭제와 관련한 사항은 개별 서비스의 약관에 따릅니다.\n" +
            "④ 이용계약이 해지된 경우라도 여러분은 다시 회사에 대하여 이용계약의 체결을 신청할 수 있습니다.\n" +
            "제 16 조 (손해배상)\n" +
            "① 회사는 법령상 허용되는 한도 내에서 서비스와 관련하여 본 약관에 명시되지 않은 어떠한 구체적인 사항에 대한 약정이나 보증을 하지 않습니다. 또한, 회사는 CP(Contents Provider)가 제공하거나 회원이 작성하는 등의 방법으로 서비스에 게재된 정보, 자료, 사실의 신뢰도, 정확성 등에 대해서는 보증을 하지 않으며, 회사의 과실 없이 발생된 여러분의 손해에 대하여는 책임을 부담하지 아니합니다.\n" +
            "② 회사는 회사의 과실로 인하여 여러분이 손해를 입게 될 경우 본 약관 및 관련 법령에 따라 여러분의 손해를 배상하겠습니다. 다만 회사는 회사의 과실 없이 발생된 아래와 같은 손해에 대해서는 책임을 부담하지 않습니다. 또한 회사는 법률상 허용되는 한도 내에서 간접 손해, 특별 손해, 결과적 손해, 징계적 손해, 및 징벌적 손해에 대한 책임을 부담하지 않습니다.\n" +
            "1. 천재지변 또는 이에 준하는 불가항력의 상태에서 발생한 손해\n" +
            "2. 여러분의 귀책사유로 서비스 이용에 장애가 발생한 경우\n" +
            "3. 서비스에 접속 또는 이용과정에서 발생하는 개인적인 손해\n" +
            "4. 제3자가 불법적으로 회사의 서버에 접속하거나 서버를 이용함으로써 발생하는 손해\n" +
            "5. 제3자가 회사 서버에 대한 전송 또는 회사 서버로부터의 전송을 방해함으로써 발생하는 손해\n" +
            "6. 제3자가 악성 프로그램을 전송 또는 유포함으로써 발생하는 손해\n" +
            "7. 전송된 데이터의 생략, 누락, 파괴 등으로 발생한 손해, 명예훼손 등 제3자가 서비스를 이용하는 과정에서 발생된 손해\n" +
            "8. 기타 회사의 고의 또는 과실이 없는 사유로 인해 발생한 손해\n" +
            "제 17 조 (분쟁의 해결)\n" +
            "본 약관 또는 서비스는 대한민국법령에 의하여 규정되고 이행됩니다. 서비스 이용과 관련하여 회사와 여러분 간에 분쟁이 발생하면 이의 해결을 위해 성실히 협의할 것입니다. 그럼에도 불구하고 해결되지 않으면 민사소송법상의 관할법원에 소를 제기할 수 있습니다.\n" +
            "•\t공고일자 : 2021년 07월 01일\n" +
            "•\t시행일자 : 2021년 07월 15일\n" +
            "\n"

    val personalInfoUsingTerm = "푸드커넥트는 개인정보를 안전하게 취급하는데 최선을 다합니다.\n" +
            "아래에 동의하시면 소셜로그인 계정의 프로필정보를 푸드커넥트 서비스에서 편리하게 이용하실 수 있습니다.\n" +
            "\n" +
            "\n" +
            "이용자 식별 및 회원관리, 프로필정보 연동\n" +
            "이메일(아이디(*1)), 비밀번호, 이름(닉네임), 프로필사진\n" +
            "\n" +
            "인구통계학적 특성과 이용자의 관심, 기호, 성향의 추정을 통한 맞춤형 콘텐츠 추천 및 마케팅에 활용(*2)\n" +
            "서비스 부정 이용 방지\n" +
            "\n" +
            "이메일, 비밀번호, 이름(닉네임), 프로필사진, 친구 목록, 연락처, 서비스 이용 내역, 서비스 내 구매 및 결제 내역\n" +
            "\n" +
            "서비스 이용과정에서 기기정보, IP주소, 쿠키, 서비스 이용기록이 자동으로 수집될 수 있습니다.\n" +
            "위 동의를 거부할 권리가 있으며, 동의를 거부하실 경우 서비스 이용이 제한됩니다.\n" +
            "푸드커넥트가 제공하는 이메일 서비스를 이용하는 경우 아이디를 수집합니다.\n" +
            "맞춤형 콘텐츠 추천 및 마케팅 활용 목적으로 이용자 정보와 ‘쿠키’ 또는 ‘광고식별자’ 기반으로 수집된 행태정보를 활용할 수 있습니다. \n"


    val locationInfoTerm = "제1조 목적\n" +
            "본 약관은 푸드커넥트(이하 “회사”)가 제공하는 위치기반서비스에 대해 회사와 위치기반서비스를 이용하는 개인위치정보주체(이하 “이용자”)간의 권리·의무 및 책임사항, 기타 필요한 사항 규정을 목적으로 합니다.\n" +
            "제2조 이용약관의 효력 및 변경\n" +
            "1. 본 약관은 이용자가 본 약관에 동의하고 회사가 정한 절차에 따라 위치기반서비스의 이용자로 등록됨으로써 효력이 발생합니다.\n" +
            "2. 회사는 법률이나 위치기반서비스의 변경사항을 반영하기 위한 목적 등으로 약관을 수정할 수 있습니다.\n" +
            "3. 약관이 변경되는 경우 회사는 변경사항을 최소 7일 전에 회사의 홈페이지 등 기타 공지사항 페이지를 통해 게시합니다.\n" +
            "4. 단, 개정되는 내용이 이용자 권리의 중대한 변경이 발생하는 경우에는 30일 전에 게시하도록 하겠습니다.\n" +
            "제3조 약관 외 준칙\n" +
            "이 약관에 명시되지 않은 사항에 대해서는 위치 정보의 보호 및 이용 등에 관한 법률, 전기통신사업법, 정보통신망 이용 촉진및 보호 등에 관한 법률 등 관계법령 및 회사가 정한 지침 등의 규정에 따릅니다.\n" +
            "제4조 서비스의 내용\n" +
            "회사는 직접 수집하거나 위치정보사업자로부터 수집한 이용자의 현재 위치 또는 현재 위치가 포함된 지역을 이용하여 아래와 같은 위치기반서비스를 제공합니다.\n" +
            "1. 위치정보를 활용한 정보 검색결과 및 콘텐츠를 제공하거나 추천\n" +
            "2. 생활편의를 위한 위치 공유, 위치/지역에 따른 알림, 경로 안내\n" +
            "3. 위치기반의 콘텐츠 분류를 위한 콘텐츠 태깅(Geotagging)\n" +
            "4. 위치기반의 맞춤형 광고\n" +
            "제5조 서비스 이용요금\n" +
            "회사가 제공하는 위치기반서비스는 무료입니다.\n" +
            "단, 무선 서비스 이용 시 발생하는 데이터 통신료는 별도이며, 이용자가 가입한 각 이동통신사의 정책에 따릅니다.\n" +
            "제6조 서비스 이용의 제한·중지\n" +
            "1. 회사는 위치기반서비스사업자의 정책변경 등과 같이 회사의 제반사정 또는 법률상의 이유로 위치기반서비스를 유지할 수 없는 경우 위치기반서비스의 전부 또는 일부를 제한·변경·중지할 수 있습니다.\n" +
            "2. 단, 위 항에 의한 위치기반서비스 중단의 경우 회사는 사전에 회사 홈페이지 등 기타 공지사항 페이지를 통해 공지하거나 이용자에게 통지합니다.\n" +
            "제7조 개인위치정보주체의 권리\n" +
            "1. 이용자는 언제든지 개인위치정보의 수집·이용·제공에 대한 동의 전부 또는 일부를 유보할 수 있습니다.\n" +
            "2. 이용자는 언제든지 개인위치정보의 수집·이용·제공에 대한 동의 전부 또는 일부를 철회할 수 있습니다. 이 경우 회사는 지체 없이 철회된 범위의 개인위치정보 및 위치정보 수집·이용·제공사실 확인자료를 파기합니다.\n" +
            "3. 이용자는 개인위치정보의 수집·이용·제공의 일시적인 중지를 요구할 수 있으며, 이 경우 회사는 이를 거절할 수 없고 이를 충족하는 기술적 수단을 마련합니다.\n" +
            "4. 이용자는 회사에 대하여 아래 자료에 대한 열람 또는 고지를 요구할 수 있으며, 해당 자료에 오류가 있는 경우에는 정정을 요구할 수 있습니다. 이 경우 정당한 사유 없이 요구를 거절하지 않습니다.\n" +
            "1) 이용자에 대한 위치정보 수집·이용·제공사실 확인자료\n" +
            "2) 이용자의 개인위치정보가 위치정보의 보호 및 이용 등에 관한 법률 또는 다른 법령의 규정에 의하여 제3자에게 제공된 이유 및 내용\n" +
            "5. 이용자는 권리행사를 위해 본 약관 제14조의 연락처를 이용하여 회사에 요청할 수 있습니다.\n" +
            "제8조 개인위치정보의 이용 또는 제공\n" +
            "1. 회사는 개인위치정보를 이용하여 위치기반서비스를 제공하는 경우 본 약관에 고지하고 동의를 받습니다.\n" +
            "2. 회사는 이용자의 동의 없이 개인위치정보를 제3자에게 제공하지 않으며, 제3자에게 제공하는 경우에는 제공받는 자 및 제공목적을 사전에 이용자에게 고지하고 동의를 받습니다.\n" +
            "3. 회사는 개인위치정보를 이용자가 지정하는 제3자에게 제공하는 경우 개인위치정보를 수집한 통신단말장치로 매회 이용자에게 제공받는 자, 제공일시 및 제공목적을 즉시 통지합니다.\n" +
            "4. 단, 아래의 경우 이용자가 미리 특정하여 지정한 통신단말장치 또는 전자우편주소, 온라인게시 등으로 통지합니다.\n" +
            "1) 개인위치정보를 수집한 당해 통신단말장치가 문자, 음성 또는 영상의 수신기능을 갖추지 아니한 경우\n" +
            "2) 이용자의 개인위치정보를 수집한 통신단말장치 외의 통신단말장치 또는 전자우편주소, 온라인게시 등으로 통보할 것을 미리 요청한 경우\n" +
            "5. 회사는 위치정보의 보호 및 이용 등에 관한 법률 제16조 제2항에 근거하여 개인위치정보 수집·이용·제공사실 확인자료를 자동으로 기록·보존하며, 해당 자료는 6개월간 보관합니다.\n" +
            "제9조 법정대리인의 권리\n" +
            "회사는 14세 미만의 이용자에 대해서는 개인위치정보를 이용한 위치기반서비스 제공 및 개인위치정보의 제3자 제공에 대한 동의를 이용자 및 이용자의 법정대리인으로부터 동의를 받아야 합니다. 이 경우 법정대리인은 본 약관 제7조에 의한 이용자의 권리를 모두 가집니다.\n" +
            "제10조 8세 이하의 아동 동의 보호의무자의 권리\n" +
            "1. 회사는 아래의 경우에 해당하는 자(이하 “8세 이하의 아동 등”)의 위치정보의 보호 및 이용 등에 관한 법률 제26조2항에 해당하는 자(이하 “보호의무자”)가 8세 이하의 아동 등의 생명 또는 신체보호를 위하여 개인위치정보의 이용 또는 제공에 동의하는 경우에는 본인의 동의가 있는 것으로 봅니다.\n" +
            "1) 8세 이하의 아동\n" +
            "2) 피성년후견인\n" +
            "3) 장애인복지법 제2조제2항제2호에 따른 정신적 장애를 가진 사람으로서 장애인고용촉진 및 직업재활법 제2조제2호에 따른 중증장애인에 해당하는 사람(장애인복지법 제32조에 따라 장애인 등록을 한 사람만 해당한다)\n" +
            "2. 8세 이하의 아동 등의 생명 또는 신체의 보호를 위하여 개인위치정보의 이용 또는 제공에 동의를 하고자 하는 보호의무자는 서면동의서에 보호의무자임을 증명하는 서면을 첨부하여 회사에 제출하여야 합니다.\n" +
            "3. 보호의무자는 8세 이하의 아동 등의 개인위치정보 이용 또는 제공에 동의하는 경우 본 약관 제7조에 의한 이용자의 권리를 모두 가집니다.\n" +
            "제11조 손해배상\n" +
            "회사의 위치정보의 보호 및 이용 등에 관한 법률 제15조 및 26조의 규정을 위반한 행위로 인해 손해를 입은 경우 이용자는 회사에 손해배상을 청구할 수 있습니다. 회사는 고의, 과실이 없음을 입증하지 못하는 경우 책임을 면할 수 없습니다.\n" +
            "제12조 면책\n" +
            "1. 회사는 다음 각 호의 경우로 위치기반서비스를 제공할 수 없는 경우 이로 인하여 이용자에게 발생한 손해에 대해서는 책임을 부담하지 않습니다.\n" +
            "1) 천재지변 또는 이에 준하는 불가항력의 상태가 있는 경우\n" +
            "2) 위치기반서비스 제공을 위하여 회사와 서비스 제휴계약을 체결한 제3자의 고의적인 서비스 방해가 있는 경우\n" +
            "3) 이용자의 귀책사유로 위치기반서비스 이용에 장애가 있는 경우\n" +
            "4) 제1호 내지 제3호를 제외한 기타 회사의 고의·과실이 없는 사유로 인한 경우\n" +
            "2. 회사는 위치기반서비스 및 위치기반서비스에 게재된 정보, 자료, 사실의 신뢰도, 정확성 등에 대해서는 보증을 하지 않으며 이로 인해 발생한 이용자의 손해에 대하여는 책임을 부담하지 아니합니다.\n" +
            "제13조 분쟁의 조정 및 기타\n" +
            "1. 회사는 위치정보와 관련된 분쟁의 해결을 위해 이용자와 성실히 협의합니다.\n" +
            "2. 전항의 협의에서 분쟁이 해결되지 않은 경우, 회사와 이용자는 위치정보의 보호 및 이용 등에 관한 법률 제28조의 규정에 의해 방송통신위원회에 재정을 신청하거나, 개인정보보호법 제43조의 규정에 의해 개인정보 분쟁조정위원회에 조정을 신청할 수 있습니다.\n"


}