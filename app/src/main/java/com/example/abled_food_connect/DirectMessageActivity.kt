package com.example.abled_food_connect

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.abled_food_connect.adapter.DirectMessageRvAdapter
import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.databinding.ActivityDirectMessageBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URISyntaxException

class DirectMessageActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityDirectMessageBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    //NodeJS 통신을 위한 전역변수 설정
    lateinit var mySocket : Socket
    private lateinit var roomName : String
    var messageText : String = ""


    //내정보


    private var MyUserTableId : Int = MainActivity.user_table_id
    private var MyUserNicName : String = MainActivity.loginUserNickname
    private var MyProfileImage : String = MainActivity.userThumbnailImage



    //상대정보
    //프로필 클릭한 상대의 유저 테이블 아이디
    //방 이름 및 DB 저장시 사용한다.
    private var clicked_user_tb_id : Int = 0
    private lateinit var clicked_user_NicName : String
    private lateinit var clicked_user_ProfileImage : String


    //gson
    var gson = GsonBuilder().create()


    //채팅 리사이클러뷰
    lateinit var directMessageRvAdapter: DirectMessageRvAdapter
    var direct_message_data_Arraylist = ArrayList<DirectMessageRvData>()


    //이전 메시지 테이블 번호
    var before_Dm_tb_id = 0
    //최상단 메시지 테이블 번호
    var top_DM_tb_id = 0


    //리사이클러뷰 맨 아래를 보고 있는가?
    var isRvScrollBottom = false



    //대화상대가 탈퇴한 유저인지 체크하는 변수
    //0이면 탈퇴안한유저, 1이면 탈퇴한 유저.
    var is_account_delete = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct_message)


        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityDirectMessageBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.
        // 뷰 id도 파스칼케이스 + 카멜케이스의 네이밍규칙 적용으로 인해서 tv_message -> tvMessage 로 자동 변환 되었습니다.

        //키보드가 화면 안가리게함
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        // 권한체크 시작(카메라, 저장소접근)
        settingPermission()



        //어떤 상대를 클릭했는지 인텐트로 받아온다.
        clicked_user_tb_id = intent.getIntExtra("writer_user_tb_id",0)
        clicked_user_NicName = intent.getStringExtra("clicked_user_NicName")!!
        clicked_user_ProfileImage = intent.getStringExtra("clicked_user_ProfileImage")!!
        is_account_delete = intent.getIntExtra("is_account_delete",0)


        if(is_account_delete == 1){
            binding.imageSendBtn.isEnabled = false
            binding.editText.isEnabled =false
            binding.editText.hint = "대화가 불가능한 사용자입니다."
            binding.messageSendBtn.isEnabled =false
        }
        Log.d("상대의 id는?", clicked_user_tb_id.toString())


        //툴바
        setSupportActionBar(binding.directMessageToolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.directMessageToolbar.title = "${clicked_user_NicName}님과의 대화"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //내 프로필 이미지 가져오기
        //userProfileLoading(MainActivity.user_table_id)

        (binding.directMessageChatListRv.layoutManager as LinearLayoutManager).stackFromEnd = true



        //방이름은 '유저테이블아이디+"and"+유저테이블아이디' 형식이다.
        //테이블 아이디 순서는 작은 것이 앞쪽에 오도록 한다.

        if(clicked_user_tb_id < MyUserTableId  ){
            roomName = clicked_user_tb_id.toString()+"and"+ MyUserTableId.toString()
        }else{
            roomName = MyUserTableId.toString() +"and"+ clicked_user_tb_id.toString()
        }

        //DB 방정보에 유저가 등록되어있는지 확인.
        //미 등록시 방에 유저를 등록시킨다.
        DM_Room_Join_Checking(roomName,MyUserTableId, clicked_user_tb_id)


        //채팅내역을 가져온다.
        chattingListLoading(roomName)


        try {
            mySocket = IO.socket("ServerIP:3000")
            //소켓이 접속할 uri을 설정한다.
        }catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        mySocket.connect() // 소켓연결


        //on은 서버로부터 받는 동작 수행.
        //emit은 보내는 동작수행
        mySocket.on(Socket.EVENT_CONNECT, onConnect)
        // 첫 연결이 되면 onConnect 메서드가 실행된다
        // onConnect는 닉네임과 방번호를 서버로 전달한다.


        //서버로부터 메시지를 받는다.
        //새로운 유저가 접속했다는 알림을 받을 경우, 내가 보낸 메시지 중 안 읽은 메시지가 있었다면 읽음처리를 해준다.
        // "new_user_coming"을 키값으로 사용한다.

        mySocket.on("new_user_coming", newUserComing)

        //서버로부터 메시지를 받는다. 채팅 메시지를 받았을 경우 "message_from_server"을 키값으로 사용한다.
        mySocket.on("message_from_server", fromServerMessage_Get)







        binding.messageSendBtn.setOnClickListener {

            if(binding.editText.text.toString().length>0) {
                //내가 보내는 것이다.
                //dm_log_tb_id,sendtime,message_check는 서버에서 처리할 것이다.
                var g1 = DirectMessageNodeServerSendDataItem(
                    0,
                    roomName,
                    MyUserTableId,
                    clicked_user_tb_id,
                    binding.editText.text.toString(),
                    "Text",
                    "",
                    ""
                )
                var msg_json = gson.toJson(g1)


                mySocket.emit("message_from_client", msg_json)
                binding.editText.setText("")


//            Log.d("어레이뭐있냐", direct_message_data_Arraylist.toString())
//            runOnUiThread( {
//                directMessageRvAdapter =  DirectMessageRvAdapter(direct_message_data_Arraylist)
//                directMessageRvAdapter.notifyDataSetChanged()
//                binding.directMessageChatListRv.adapter = directMessageRvAdapter
//                binding.directMessageChatListRv.scrollToPosition(direct_message_data_Arraylist.size-1);
//            });

            }
        }

        //갤러리 또는 카메라를 실행시킨다.
        val cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // use the returned uri
                val uriContent = result.originalUri
                val uriFilePath = result.getUriFilePath(this) // optional usage
                if (uriFilePath != null) {
                    ImageUpload(uriFilePath)
                }
            } else {
                // an error occurred
                val exception = result.error
            }
        }

        binding.imageSendBtn.setOnClickListener {
            //갤러리 또는 카메라를 실행시킨다.
            cropImage.launch(
                options {
                    setGuidelines(CropImageView.Guidelines.ON)
                }
            )
        }

        //스낵바 클릭이벤트



        binding.snackbarLinearLayout.setOnClickListener {
            //스낵바를 클릭하면 리사이클러뷰 최하단으로 이동
            binding.directMessageChatListRv.layoutManager!!.scrollToPosition(direct_message_data_Arraylist.size-1)

        }


        // 스크롤 리스너
        binding.directMessageChatListRv.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)




                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1


                Log.d("보는 위치", "${lastVisibleItemPosition}")
                // 스크롤이 일정위치까지 올라가면 올리면 페이징 데이터를 가져온다.
                if (!binding.directMessageChatListRv.canScrollVertically(-1)) {
                    rvTopPaging(roomName,direct_message_data_Arraylist.get(0).dm_log_tb_id)
                }


                // 스크롤이 끝에 도달했는지 확인
                if (!binding.directMessageChatListRv.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount) {
                    Log.d("TAG", "리사이클러뷰 최하단")
                    binding.snackbarLinearLayout.visibility = View.GONE

                    isRvScrollBottom = true

                }else{
                    isRvScrollBottom = false
                }
            }
        })
    }




    val onConnect: Emitter.Listener = Emitter.Listener {
        // onConnect는 닉네임과 방번호를 서버로 전달한다.
        //서버 측에서는 이 username을 whoIsON Array 에 추가를 할 것입니다.


        mySocket.emit("login", roomName+","+MyUserTableId.toString()+","+clicked_user_tb_id.toString())
        //mySocket.emit("user_room_name", roomName)
        Log.d("Tag", "Socket is connected with ${MyUserTableId.toString()+roomName}")
    }

    val newUserComing : Emitter.Listener = Emitter.Listener {
        //서버에서 도착한 메시지 받기.
        Log.d("유저가 접속했습니다.",  it[0].toString())


        for(i in 0..direct_message_data_Arraylist.size-1){
            if(direct_message_data_Arraylist.get(i).user_tb_id == MainActivity.user_table_id && direct_message_data_Arraylist.get(i).message_check == ""){
                Log.d("읽지않은 메시지가 있습니다.", "나와라")
                direct_message_data_Arraylist.get(i).message_check ="읽음"


            }
        }

        runOnUiThread( {
            binding.directMessageChatListRv.adapter?.notifyDataSetChanged()
        });


        //NewUserchattingListLoading(roomName)


    }





    val fromServerMessage_Get: Emitter.Listener = Emitter.Listener {
        //서버에서 도착한 메시지 받기.
        Log.d("Tag",  it[0].toString())

        val data = it[0] as JSONObject
        Log.d("Tag", data.toString())

        var dm_log_tb_id = data.getInt("dm_log_tb_id")
        var room_name = data.getString("room_name")
        var from_user_tb_id = data.getInt("from_user_tb_id")
        var to_user_tb_id = data.getInt("to_user_tb_id")
        var content = data.getString("content")
        var text_or_image_or_dateline = data.getString("text_or_image_or_dateline")
        var send_time = data.getString("send_time")
        var message_check = data.getString("message_check")

        val send_time_split = send_time.split(" ")
        val show_time_split = send_time_split[1].split(":")

        var toShowTimeStr = show_time_split[0]+":"+show_time_split[1]

        if(from_user_tb_id == MyUserTableId){


            direct_message_data_Arraylist.add(DirectMessageRvData(dm_log_tb_id,room_name,MyUserTableId,MyUserNicName,"ServerIP"+MyProfileImage,content,text_or_image_or_dateline,send_time,toShowTimeStr,message_check))


        }else{
            direct_message_data_Arraylist.add(DirectMessageRvData(dm_log_tb_id,room_name,clicked_user_tb_id,clicked_user_NicName,"ServerIP"+clicked_user_ProfileImage,content,text_or_image_or_dateline,send_time,toShowTimeStr,message_check))

        }








        runOnUiThread( {

            directMessageRvAdapter.notifyDataSetChanged()

            if(from_user_tb_id != MyUserTableId){
                //스낵바에 메시지 출력하기


                Glide.with(binding.snackbarProfileIv.context)
                    .load(getString(R.string.http_request_base_url)+clicked_user_ProfileImage)
                    .circleCrop()
                    .into(binding.snackbarProfileIv)

                binding.snackberNicNameTv.text = clicked_user_NicName

                if(text_or_image_or_dateline == "Text"){
                    binding.snackberContentTv.text = content
                }else if(text_or_image_or_dateline == "Image"){
                    binding.snackberContentTv.text = "이미지를 받았습니다."
                }

                binding.snackbarLinearLayout.visibility = View.VISIBLE


            }

            if(isRvScrollBottom == true){

                //리사이클러뷰 최하단에 있으면 새로운 메시지가 왔을 때, 자동으로 다음 메시지 보이게한다.
                binding.directMessageChatListRv.scrollToPosition(direct_message_data_Arraylist.size-1)
            }

            //만약 내가 보낸 내용이면 최하단으로 이동한다.
            if(from_user_tb_id == MyUserTableId){

                //리사이클러뷰 최하단에 있으면 새로운 메시지가 왔을 때, 자동으로 다음 메시지 보이게한다.
                binding.directMessageChatListRv.scrollToPosition(direct_message_data_Arraylist.size-1)
            }




        });
    }



    //서버로 이미지 업로드

    fun ImageUpload(uriFilePath:String){

//        val uriPathHelper = UserRegisterActivity.URIPathHelper()
//        var filePath = imageUri?.let { uriPathHelper.getPath(this, it) }

        //creating a file
        val file = File(uriFilePath)

        //이미지의 확장자를 구한다.
        val extension = MimeTypeMap.getFileExtensionFromUrl(uriFilePath.toString())


        var fileName = MainActivity.user_table_id.toString()+"."+extension
        var requestBody : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(),file)
        var body : MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file",fileName,requestBody)

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

        var server = retrofit.create(API.chatImageSend_interface::class.java)


        server.chat_image_send_interface_Request(body).enqueue(object: Callback<ChatImageSendingData> {
            override fun onFailure(call: Call<ChatImageSendingData>, t: Throwable) {
                t.message?.let { Log.d("레트로핏 결과1", it) }
            }

            override fun onResponse(call: Call<ChatImageSendingData>, response: Response<ChatImageSendingData>) {
                if (response?.isSuccessful) {
                    Log.d("이미지를 업로드했습니다",""+response?.body().toString())

                    var items : ChatImageSendingData? =  response.body()

                    items!!.ImageName

                    //내가 보내는 것이다.
                    //dm_log_tb_id,sendtime,message_check는 서버에서 처리할 것이다.
                    var g1 = DirectMessageNodeServerSendDataItem(0,roomName,MyUserTableId,clicked_user_tb_id,items!!.ImageName,"Image","","")
                    var msg_json = gson.toJson(g1)

                    mySocket.emit("message_from_client", msg_json)


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



    override fun onResume() {
        super.onResume()


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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
        mySocket.disconnect()
    }


    fun DM_Room_Join_Checking(dm_room_name:String, my_user_tb_id:Int , your_user_tb_id:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.dmRoomJoinCheck_Interface::class.java)

        //db에 방이 있는지 확인한다.
        val dm_room_join_check_get = api.dm_room_join_check(dm_room_name,my_user_tb_id,your_user_tb_id)


        dm_room_join_check_get.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.body().toString()}")

                var items : String? =  response.body()



            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }




    fun chattingListLoading(roomName: String){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.getChattingList_Interface::class.java)

        //채팅리스트를 불러온다
        val chatting_list_get = api.direct_message_list_get(roomName,-1)


        chatting_list_get.enqueue(object : Callback<DirectMessageNodeServerSendData> {
            override fun onResponse(
                call: Call<DirectMessageNodeServerSendData>,
                response: Response<DirectMessageNodeServerSendData>
            ) {
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.body().toString()}")

                var items : DirectMessageNodeServerSendData? =  response.body()

                var directMessageNodeServerSendDataItem = items!!.chattingList as ArrayList<DirectMessageNodeServerSendDataItem>

                for(i : Int in 0..directMessageNodeServerSendDataItem.size-1){

                    var dm_log_tb_id = directMessageNodeServerSendDataItem.get(i).dm_log_tb_id
                    var room_name = directMessageNodeServerSendDataItem.get(i).room_name
                    var from_user_tb_id = directMessageNodeServerSendDataItem.get(i).from_user_tb_id
                    var to_user_tb_id = directMessageNodeServerSendDataItem.get(i).to_user_tb_id
                    var content = directMessageNodeServerSendDataItem.get(i).content
                    var text_or_image_or_dateline = directMessageNodeServerSendDataItem.get(i).text_or_image_or_dateline
                    var send_time = directMessageNodeServerSendDataItem.get(i).send_time
                    var message_check = directMessageNodeServerSendDataItem.get(i).message_check

                    val send_time_split = send_time.split(" ")
                    val show_time_split = send_time_split[1].split(":")

                    var toShowTimeStr = show_time_split[0]+":"+show_time_split[1]

                    if(from_user_tb_id == MyUserTableId){
                        direct_message_data_Arraylist.add(DirectMessageRvData(dm_log_tb_id,room_name,MyUserTableId,MyUserNicName,"ServerIP"+MyProfileImage,content,text_or_image_or_dateline,send_time,toShowTimeStr,message_check))
                    }else{
                        direct_message_data_Arraylist.add(DirectMessageRvData(dm_log_tb_id,room_name,clicked_user_tb_id,clicked_user_NicName,"ServerIP"+clicked_user_ProfileImage,content,text_or_image_or_dateline,send_time,toShowTimeStr,message_check))
                    }
                }




                directMessageRvAdapter =  DirectMessageRvAdapter(direct_message_data_Arraylist)
                directMessageRvAdapter.notifyDataSetChanged()
                binding.directMessageChatListRv.adapter = directMessageRvAdapter
                binding.directMessageChatListRv.scrollToPosition(direct_message_data_Arraylist.size-1);




            }

            override fun onFailure(call: Call<DirectMessageNodeServerSendData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }


    fun rvTopPaging(roomName: String,dm_log_tb_id : Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.getChattingList_Interface::class.java)

        //채팅리스트를 불러온다
        val chatting_list_get = api.direct_message_list_get(roomName,dm_log_tb_id)


        chatting_list_get.enqueue(object : Callback<DirectMessageNodeServerSendData> {
            override fun onResponse(
                call: Call<DirectMessageNodeServerSendData>,
                response: Response<DirectMessageNodeServerSendData>
            ) {
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.body().toString()}")

                var items : DirectMessageNodeServerSendData? =  response.body()

                var directMessageNodeServerSendDataItem = items!!.chattingList as ArrayList<DirectMessageNodeServerSendDataItem>

                var insetCount = 0
                if(directMessageNodeServerSendDataItem.size>0){

                    for(i : Int in 0..directMessageNodeServerSendDataItem.size-1){

                        Log.d(ReviewFragment.TAG, "나와 : ${directMessageNodeServerSendDataItem.get(i)}")
                        var dm_log_tb_id = directMessageNodeServerSendDataItem.get(i).dm_log_tb_id
                        var room_name = directMessageNodeServerSendDataItem.get(i).room_name
                        var from_user_tb_id = directMessageNodeServerSendDataItem.get(i).from_user_tb_id
                        var to_user_tb_id = directMessageNodeServerSendDataItem.get(i).to_user_tb_id
                        var content = directMessageNodeServerSendDataItem.get(i).content
                        var text_or_image_or_dateline = directMessageNodeServerSendDataItem.get(i).text_or_image_or_dateline
                        var send_time = directMessageNodeServerSendDataItem.get(i).send_time
                        var message_check = directMessageNodeServerSendDataItem.get(i).message_check

                        val send_time_split = send_time.split(" ")
                        val show_time_split = send_time_split[1].split(":")

                        var toShowTimeStr = show_time_split[0]+":"+show_time_split[1]

                        if(from_user_tb_id == MyUserTableId){
                            direct_message_data_Arraylist.add(0,DirectMessageRvData(dm_log_tb_id,room_name,MyUserTableId,MyUserNicName,"ServerIP"+MyProfileImage,content,text_or_image_or_dateline,send_time,toShowTimeStr,message_check))
                        }else{
                            direct_message_data_Arraylist.add(0,DirectMessageRvData(dm_log_tb_id,room_name,clicked_user_tb_id,clicked_user_NicName,"ServerIP"+clicked_user_ProfileImage,content,text_or_image_or_dateline,send_time,toShowTimeStr,message_check))
                        }

                        insetCount++
                    }





                    runOnUiThread({
                        binding.directMessageChatListRv.adapter?.notifyItemRangeInserted(0, insetCount)

                    })

                }





            }

            override fun onFailure(call: Call<DirectMessageNodeServerSendData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }



//    fun NewUserchattingListLoading(roomName: String){
//        val retrofit = Retrofit.Builder()
//            .baseUrl(getString(R.string.http_request_base_url))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val api = retrofit.create(API.getChattingList_Interface::class.java)
//
//        //채팅리스트를 불러온다
//        val chatting_list_get = api.direct_message_list_get(roomName)
//
//
//        chatting_list_get.enqueue(object : Callback<DirectMessageNodeServerSendData> {
//            override fun onResponse(
//                call: Call<DirectMessageNodeServerSendData>,
//                response: Response<DirectMessageNodeServerSendData>
//            ) {
//                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
//                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.body().toString()}")
//
//                var items : DirectMessageNodeServerSendData? =  response.body()
//
//                var directMessageNodeServerSendDataItem = items!!.chattingList as ArrayList<DirectMessageNodeServerSendDataItem>
//
//
//
//                direct_message_data_Arraylist.clear()
//
//                for(i : Int in 0..directMessageNodeServerSendDataItem.size-1){
//
//                    Log.d(ReviewFragment.TAG, "나와 : ${directMessageNodeServerSendDataItem.get(i)}")
//                    var dm_log_tb_id = directMessageNodeServerSendDataItem.get(i).dm_log_tb_id
//                    var room_name = directMessageNodeServerSendDataItem.get(i).room_name
//                    var from_user_tb_id = directMessageNodeServerSendDataItem.get(i).from_user_tb_id
//                    var to_user_tb_id = directMessageNodeServerSendDataItem.get(i).to_user_tb_id
//                    var content = directMessageNodeServerSendDataItem.get(i).content
//                    var text_or_image_or_dateline = directMessageNodeServerSendDataItem.get(i).text_or_image_or_dateline
//                    var send_time = directMessageNodeServerSendDataItem.get(i).send_time
//                    var message_check = directMessageNodeServerSendDataItem.get(i).message_check
//
//                    val send_time_split = send_time.split(" ")
//                    val show_time_split = send_time_split[1].split(":")
//
//                    var toShowTimeStr = show_time_split[0]+":"+show_time_split[1]
//
//                    if(from_user_tb_id == MyUserTableId){
//                        direct_message_data_Arraylist.add(DirectMessageRvData(room_name,MyUserTableId,MyUserNicName,"ServerIP"+MyProfileImage,content,text_or_image_or_dateline,send_time,toShowTimeStr,message_check))
//                    }else{
//                        direct_message_data_Arraylist.add(DirectMessageRvData(room_name,clicked_user_tb_id,clicked_user_NicName,"ServerIP"+clicked_user_ProfileImage,content,text_or_image_or_dateline,send_time,toShowTimeStr,message_check))
//                    }
//                }
//
//
//
//
//
//                runOnUiThread({
//
//                    binding.directMessageChatListRv.adapter?.notifyDataSetChanged()
//
//                })
//
//
//
//
//
//
//
//            }
//
//            override fun onFailure(call: Call<DirectMessageNodeServerSendData>, t: Throwable) {
//                Log.d(ReviewFragment.TAG, "실패 : $t")
//            }
//        })
//    }











    fun userProfileLoading(user_tb_id:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.UserProfileDataInterface::class.java)

        //상대방의 프로필 정보를 가져온다.
        val user_profile_data_get = api.user_profile_data_get(user_tb_id)


        user_profile_data_get.enqueue(object : Callback<UserProfileData> {
            override fun onResponse(
                call: Call<UserProfileData>,
                response: Response<UserProfileData>
            ) {
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.body().toString()}")

                var items : UserProfileData? =  response.body()


                //로그인한 유저의 프로필 이미지 가져오기

                //MyProfileImage = items!!.profile_image


            }

            override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }


    //권한 요청
    fun settingPermission(){
        var permis = object  : PermissionListener {
            //            어떠한 형식을 상속받는 익명 클래스의 객체를 생성하기 위해 다음과 같이 작성
            override fun onPermissionGranted() {
                Log.d("TAG", "권한 허가됨")
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@DirectMessageActivity, "권한 거부", Toast.LENGTH_SHORT)
                    .show()
                ActivityCompat.finishAffinity(this@DirectMessageActivity) // 권한 거부시 앱 종료
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
}