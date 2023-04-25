package com.example.abled_food_connect.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.abled_food_connect.*
import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.retrofit.API
import com.example.abled_food_connect.retrofit.RoomAPI
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

class MyPageFragment : Fragment() {


    lateinit var userProfileIv: ImageView
    lateinit var userProfileNicNameTv: TextView
    lateinit var noShowCountTv : TextView
    lateinit var userProfileIntroductionTv: TextView
    lateinit var gson: Gson
    lateinit var social_login_type: String
    lateinit var table_user_id: String
    lateinit var mSocket: Socket


    lateinit var tierBadgeImageIv: ImageView
    lateinit var tierTv: TextView
    lateinit var rankingPointTv: TextView

    lateinit var rankTv: TextView


    // Firebase Authentication 관리 클래스
    // firebase 인증을 위한 변수
    var auth: FirebaseAuth? = null

    // 구글 로그인 연동에 필요한 변수
    var googleSignInClient: GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001

    lateinit var callbackManager: CallbackManager

    //탈퇴 - DB 데이터 처리 관련 레트로핏 변수
    val userAccountDeleteRetrofit = Retrofit.Builder()
        .baseUrl("ServerIP")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val userAccountDeleteApi =
        userAccountDeleteRetrofit.create(API.userAccountDeleteInterface::class.java)
    val account_delete = userAccountDeleteApi.user_account_delete(MainActivity.user_table_id)


    //현재 탈퇴버튼을 눌렀는지 확인하는 변수
    //0이면 onResume에서 데이터를 갱신한다.
    //1이면 갱신하지 않는다.

    var now_Account_delete = 0

    companion object {
        const val TAG: String = "마이페이지 프래그먼트 로그"
        fun newInstance(): MyPageFragment {
            return MyPageFragment()
        }

        lateinit var progressBar: ProgressDialog

    }

    override fun onResume() {
        super.onResume()

        if (now_Account_delete == 0) {
            //작성자 프로필
            Glide.with(userProfileIv.context)
                .load(getString(R.string.http_request_base_url) + MainActivity.userThumbnailImage)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(userProfileIv)

            userProfileNicNameTv.text = MainActivity.loginUserNickname
            //유저 정보를 DB에서 가져온다.
            userProfileLoading(MainActivity.user_table_id)
        } else {
            now_Account_delete = 0
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "마이페이지 onCreate()")
        gson = Gson()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "마이페이지 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.mypage_fragments, container, false)


        userProfileIv = view.findViewById<ImageView>(R.id.userProfileIv)
        userProfileNicNameTv = view.findViewById<TextView>(R.id.userProfileNicNameTv)
        noShowCountTv = view.findViewById<TextView>(R.id.noShowCountTv)
        userProfileIntroductionTv = view.findViewById<TextView>(R.id.userProfileIntroductionTv)
        progressBar = ProgressDialog(requireContext())
        progressBar.setMessage("로딩중..")
        progressBar.setProgressStyle(ProgressDialog.BUTTON_POSITIVE)
        progressBar.show()



        tierBadgeImageIv = view.findViewById<ImageView>(R.id.tierBadgeImageIv)
        tierTv = view.findViewById<TextView>(R.id.tierTv)
        rankingPointTv = view.findViewById<TextView>(R.id.rankingPointTv)

        rankTv = view.findViewById<TextView>(R.id.rankTv)


        val toUserProfileModifyActivityBtn =
            view.findViewById<TextView>(R.id.toUserProfileModifyActivityBtn)


        toUserProfileModifyActivityBtn.setOnClickListener {
            val intent = Intent(requireContext(), UserProfileModifyActivity::class.java)
            startActivity(intent)
        }


        val toMoveWrittenReviewListActivityBtn =
            view.findViewById<LinearLayout>(R.id.toMoveWrittenReviewListActivityBtn)
        toMoveWrittenReviewListActivityBtn.setOnClickListener(View.OnClickListener {
            var toUserProfileClickedReviewGridListActivity: Intent =
                Intent(context, UserProfileClickedReviewGridListActivity::class.java)
            toUserProfileClickedReviewGridListActivity.putExtra(
                "writer_user_tb_id",
                MainActivity.user_table_id
            )
            startActivity(toUserProfileClickedReviewGridListActivity, null)
        })


        val toMoveReviewWritingActivityBtn =
            view.findViewById<LinearLayout>(R.id.toMoveReviewWritingActivityBtn)
        toMoveReviewWritingActivityBtn.setOnClickListener(View.OnClickListener {
            var toUnwrittenReviewListActivity: Intent =
                Intent(context, UnwrittenReviewListActivity::class.java)
            startActivity(toUnwrittenReviewListActivity, null)
        })


        val toMoveUserProfileBadgeListActivityBtn =
            view.findViewById<LinearLayout>(R.id.toMoveUserProfileBadgeListActivityBtn)
        toMoveUserProfileBadgeListActivityBtn.setOnClickListener(View.OnClickListener {
            var toUserProfileBadgeListActivity: Intent =
                Intent(context, UserProfileBadgeListActivity::class.java)
            toUserProfileBadgeListActivity.putExtra("user_tb_id", MainActivity.user_table_id)
            toUserProfileBadgeListActivity.putExtra("user_nicname", MainActivity.loginUserNickname)
            startActivity(toUserProfileBadgeListActivity, null)
        })


        val toMoveUserProfileEvaluationListActivity =
            view.findViewById<LinearLayout>(R.id.toMoveUserProfileEvaluationListActivity)
        toMoveUserProfileEvaluationListActivity.setOnClickListener(View.OnClickListener {
            var toUserProfileEvaluationListActivity: Intent =
                Intent(context, UserProfileEvaluationListActivity::class.java)
            toUserProfileEvaluationListActivity.putExtra("user_tb_id", MainActivity.user_table_id)
            toUserProfileEvaluationListActivity.putExtra(
                "user_nicname",
                MainActivity.loginUserNickname
            )
            startActivity(toUserProfileEvaluationListActivity, null)
        })


        val toMyPageUserScheduleActivityBtn =
            view.findViewById<LinearLayout>(R.id.toMyPageUserScheduleActivityBtn)

        toMyPageUserScheduleActivityBtn.setOnClickListener {
            val intent = Intent(requireContext(), MyPageUserScheduleActivity::class.java)
            startActivity(intent)
        }

        val logoutBtn = view.findViewById<LinearLayout>(R.id.logoutBtn)
        // 로그아웃
        logoutBtn.setOnClickListener {
            var builder = AlertDialog.Builder(logoutBtn.context)
            builder.setTitle("로그아웃")
            builder.setMessage("로그아웃 하시겠습니까?")


            // 버튼 클릭시에 무슨 작업을 할 것인가!
            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE ->


                            activity?.let {

                                logout()


                            }

                        DialogInterface.BUTTON_NEGATIVE ->
                            Log.d(TAG, "로그아웃 취소")
                    }
                }
            }

            builder.setPositiveButton("확인", listener)
            builder.setNegativeButton("취소", listener)


            builder.show()
        }


        var accountDeleteBtn = view.findViewById<LinearLayout>(R.id.accountDeleteBtn)
        // 로그아웃
        accountDeleteBtn.setOnClickListener {
            var builder = AlertDialog.Builder(logoutBtn.context)
            builder.setTitle("탈퇴")
            builder.setMessage("회원탈퇴를 하시겠습니까?")


            // 버튼 클릭시에 무슨 작업을 할 것인가!
            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE ->


                            activity?.let {

                                accountDelete()

                                //탈퇴버튼을 눌렀는지 확인하는 변수
                                now_Account_delete = 1


                            }

                        DialogInterface.BUTTON_NEGATIVE ->
                            Log.d(TAG, "회원탈퇴 취소")
                    }
                }
            }

            builder.setPositiveButton("확인", listener)
            builder.setNegativeButton("취소", listener)


            builder.show()
        }


        /*
        구글 로그인 구현
         */

        // firebaseauth를 사용하기 위한 인스턴스 get
        auth = FirebaseAuth.getInstance()

        // xml에서 구글 로그인 버튼 코드 가져오기


        // 구글 로그인을 위해 구성되어야 하는 코드 (Id, Email request)
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }


        callbackManager = CallbackManager.Factory.create()

        try {
            val info = requireContext().packageManager.getPackageInfo(
                "com.example.abled_food_connect;",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }




        return view

    }


    fun logout() {


        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.whenLogoutFcmtokenDeleteInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val fcm_delete = api.when_logout_fcmtoken_delete(MainActivity.user_table_id)


        fcm_delete.enqueue(object : Callback<whenLogoutFcmtokenDeleteData> {
            override fun onResponse(
                call: Call<whenLogoutFcmtokenDeleteData>,
                response: Response<whenLogoutFcmtokenDeleteData>
            ) {
                Log.d(MyPageFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
                Log.d(MyPageFragment.TAG, "리뷰 컨텐츠 : ${response.body().toString()}")

                var items: whenLogoutFcmtokenDeleteData? = response.body()

                if (items!!.success == true) {

                    //카카오 로그아웃

                    if (social_login_type == "KAKAO") {
                        UserApiClient.instance.logout { error ->
                            if (error != null) {
                                Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                            } else {
                                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                            }
                        }
                    } else if (social_login_type == "FACEBOOK") {
                        //페이스북 로그아웃
                        LoginManager.getInstance().logOut()
                        Log.d(TAG, "logout: facebook 로그아웃")
                    } else if (social_login_type == "NAVER") {
                        //네이버 로그아웃
                        OAuthLogin.getInstance().logout(context)
                        Log.d("TAG", OAuthLogin.getInstance().getState(context).toString())
                        OAuthLogin.getInstance().getState(context)
                    } else if (social_login_type == "GOOGLE") {

                        Log.d(TAG, "logout: 구글 로그아웃")

                        //구글 로그아웃
                        FirebaseAuth.getInstance().signOut()

                        // 구글 로그인을 위해 구성되어야 하는 코드 (Id, Email request)
                        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()


                        var googleSignInClient: GoogleSignInClient? = null

                        googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }

                        if (googleSignInClient != null) {

                            //실질적인 구글 클라이언트 로그인 기록이 초기화된다.
                            googleSignInClient.signOut()
                        }


                    }

                    Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_LONG).show()


                    val pref = activity?.getSharedPreferences("pref_user_data", 0)
                    val edit = pref?.edit()
                    if (edit != null) {
                        edit.putBoolean("login_check", false)
                        edit.apply()//저장완료
                    }

                    //로그인 엑티비티로 이동
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK);
                    } else {
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); }

                    requireActivity().finish()


                } else {

                    Toast.makeText(context, "로그아웃 할 수 없습니다.", Toast.LENGTH_SHORT).show()

                }


            }

            override fun onFailure(call: Call<whenLogoutFcmtokenDeleteData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                if (result.isSuccess) {

                    var account = result.signInAccount
                    firebaseAuthWithGoogle(account)
                }
            }
        } //if
    }


    /*
        구글 로그인 메서드
    */


    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    } // googleLogin


    //구글 소셜로그인 - 연동해제 및 탈퇴처리.
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val user = Firebase.auth.currentUser
                    val uid = user!!.uid
                    Log.d("사용자 식별자 id", uid)


                    //현재 접속중인 계정과 탈퇴를 위해 로그인한 구글계정이 동일한지 확인
                    if (table_user_id == uid) {


                        //일치하면, DB탈퇴관련 레트로핏을 실행시킨다.
                        account_delete.enqueue(object : Callback<userAccountDeleteData> {
                            override fun onResponse(
                                call: Call<userAccountDeleteData>,
                                response: Response<userAccountDeleteData>
                            ) {
                                Log.d(MyPageFragment.TAG, "데이터로드 : ${response.raw()}")
                                Log.d(MyPageFragment.TAG, "데이터로드 : ${response.body().toString()}")

                                if (response.body() != null) {
                                    val Item: userAccountDeleteData = response.body()!!


                                    if (Item.success == true) {
                                        //DB에서 탈퇴관련 데이터 정리 완료
                                        Log.d(MyPageFragment.TAG, "DB탈퇴처리완료")


                                        //구글 계정연동 해제
                                        auth!!.getCurrentUser()!!.delete()

                                        Toast.makeText(
                                            requireContext(),
                                            "회원탈퇴가 완료되었습니다.",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        //구글 로그아웃
                                        FirebaseAuth.getInstance().signOut()

                                        // 구글 로그인을 위해 구성되어야 하는 코드 (Id, Email request)
                                        var gso =
                                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                .requestIdToken(getString(R.string.default_web_client_id))
                                                .requestEmail()
                                                .build()


                                        var googleSignInClient: GoogleSignInClient? = null

                                        googleSignInClient =
                                            context?.let { GoogleSignIn.getClient(it, gso) }

                                        if (googleSignInClient != null) {

                                            //실질적인 구글 클라이언트 로그인 기록이 초기화된다.
                                            googleSignInClient.signOut()
                                        }


                                            loadRoomList()



                                        //구글연동해제 이후 로그인엑티비티로 이동
                                        val intent = Intent(context, MainActivity::class.java)
                                        startActivity(intent)



                                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK);
                                        } else {
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); }

                                        requireActivity().finish()


                                    } else {
                                        //DB에서 탈퇴관련 데이터 정리 실패
                                        Log.d(MyPageFragment.TAG, "DB탈퇴처리실패")
                                        Toast.makeText(
                                            context,
                                            "탈퇴에 실패했습니다. 관리자에게 연락부탁드립니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                }


                            }

                            override fun onFailure(
                                call: Call<userAccountDeleteData>,
                                t: Throwable
                            ) {
                                Log.d(MyPageFragment.TAG, "실패 : $t")
                            }
                        })


                    } else {
                        Toast.makeText(
                            requireContext(),
                            "현재 로그인 중인 계정과 동일한 계정이 아닙니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    }


                } else {
                    // 로그인 실패 시
                    Toast.makeText(requireContext(), "구글로그인실패", Toast.LENGTH_LONG).show()
                }
            }
    } //firebaseAuthWithGoogle


    /*탈퇴하기 - 각각의 소셜로그인마다 탈퇴 처리를 해야해서, 소스가 지저분합니다.
    * 탈퇴로직
    * 탈퇴버튼 클릭 -> 소셜로그인->
    * 본인이 현재 접속한 소셜로그인계정과 일치 -> DB계정 탈퇴처리 레트로핏 작동 -> 리스폰스 == true시 소셜로그인 연동 해제
    * 카카오, 페이스북, 구글, 네이버
    * */
    //소셜로그인 탈퇴시 소셜로그인 연동해제를 위해서 세션을 연결시겨야한다.
    //또한 본인이 현재 접속중인 계정과 일치여부를 확인하기 위함도 있다.
    //소셜로그인이 완료+현재 접속 중인 계정과 일치 시, 탈퇴처리가 이루어진다.

    fun accountDelete() {


        //카카오 로그아웃 및 연결해제


        if (social_login_type == "KAKAO") {

            Toast.makeText(
                context,
                "탈퇴를 위해 카카오 로그인이 필요합니다. 카카오 로그인 후, 탈퇴가 완료됩니다.",
                Toast.LENGTH_SHORT
            ).show()

            UserApiClient.instance.loginWithKakaoAccount(
                requireContext(),
                prompts = listOf(Prompt.LOGIN)
            ) { token, error ->
                if (error != null) {
                    Log.e(TAG, "로그인 실패", error)
                    Toast.makeText(requireContext(), "아이디 또는 비밀번호를 다시 확인해주세요.", Toast.LENGTH_LONG)
                        .show()
                } else if (token != null) {
                    Log.i(TAG, "로그인 성공 ${token.accessToken}")


                    //카카오톡 로그인 리퀘스트
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", error)
                        } else if (user != null) {

                            //현재 접속중인 계정과 탈퇴를 위해 로그인한 카카오계정이 동일한지 확인
                            if (user.id.toString() == table_user_id) {


                                //계정이 일치하면, DB탈퇴관련 레트로핏을 실행시킨다.
                                account_delete.enqueue(object : Callback<userAccountDeleteData> {
                                    override fun onResponse(
                                        call: Call<userAccountDeleteData>,
                                        response: Response<userAccountDeleteData>
                                    ) {
                                        Log.d(MyPageFragment.TAG, "데이터로드 : ${response.raw()}")
                                        Log.d(
                                            MyPageFragment.TAG,
                                            "데이터로드 : ${response.body().toString()}"
                                        )

                                        if (response.body() != null) {
                                            val Item: userAccountDeleteData = response.body()!!


                                            if (Item.success == true) {
                                                //DB에서 탈퇴관련 데이터 정리 완료
                                                Log.d(MyPageFragment.TAG, "DB탈퇴처리완료")


                                                //카카오 소셜로그인 연동해제
                                                UserApiClient.instance.unlink { error ->
                                                    if (error != null) {
                                                        Log.e(TAG, "카카오 회원 탈퇴 실패.", error)
                                                    } else {

                                                        UserApiClient.instance.logout { error ->
                                                            if (error != null) {
                                                                Log.e(
                                                                    TAG,
                                                                    "로그아웃 실패. SDK에서 토큰 삭제됨",
                                                                    error
                                                                )
                                                            } else {
                                                                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                                                            }
                                                        }

                                                            loadRoomList()



                                                        //연동해제 이후 로그인엑티비티로 이동
                                                        val intent = Intent(
                                                            context,
                                                            MainActivity::class.java
                                                        )
                                                        startActivity(intent)
                                                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        } else {
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); }

                                                        requireActivity().finish()

                                                    }
                                                }


                                            } else {
                                                //DB에서 탈퇴관련 데이터 정리 실패
                                                Log.d(MyPageFragment.TAG, "DB탈퇴처리실패")
                                                Toast.makeText(
                                                    context,
                                                    "탈퇴에 실패했습니다. 관리자에게 연락부탁드립니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }


                                        }


                                    }

                                    override fun onFailure(
                                        call: Call<userAccountDeleteData>,
                                        t: Throwable
                                    ) {
                                        Log.d(MyPageFragment.TAG, "실패 : $t")
                                    }
                                })

                            } else {
                                Toast.makeText(
                                    context,
                                    "현재 접속중인 계정과 일치하지 않습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                        }
                    }


                }
            }


        } else if (social_login_type == "FACEBOOK") {
            //페이스북 로그아웃 및 연결 해제


            LoginManager.getInstance().logOut()

            Log.d(TAG, "logout: facebook 로그아웃")

            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("public_profile", "email"))

            LoginManager.getInstance()
                //로그아웃시 웹뷰설정을 하지 않으면, 크롬과 같은 인터넷 브라우져로 로그인을하게 된다.
                //브라우져 로그인을 하게되면, 로그인 정보를 브라우져 캐시에 저장하고 있어 브라우져캐시를 지우지 않는한 페이스북 계정 입력창이 나오지 않는다.
                //만약 다른 페이스북 계정으로 로그인 하고 싶은 유저를 위하여 웹뷰온리 옵션을 추가해주었다.
//                .setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY)
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        Log.d("TAG", "Success Login")


                        //로그인 후, DB탈퇴관련 레트로핏을 실행시킨다.

                        account_delete.enqueue(object : Callback<userAccountDeleteData> {
                            override fun onResponse(
                                call: Call<userAccountDeleteData>,
                                response: Response<userAccountDeleteData>
                            ) {
                                Log.d(MyPageFragment.TAG, "데이터로드 : ${response.raw()}")
                                Log.d(MyPageFragment.TAG, "데이터로드 : ${response.body().toString()}")

                                if (response.body() != null) {
                                    val Item: userAccountDeleteData = response.body()!!


                                    if (Item.success == true) {
                                        //DB에서 탈퇴관련 데이터 정리 완료
                                        Log.d(MyPageFragment.TAG, "DB탈퇴처리완료")


                                        //페이스북 계정 연동해제 리퀘스트
                                        var request = GraphRequest(
                                            loginResult?.accessToken,
                                            "/me/permissions",
                                            null,
                                            HttpMethod.DELETE,
                                            GraphRequest.Callback() { response -> })
                                        request.executeAsync()

                                            loadRoomList()


                                        //연동해제 이후 로그인엑티비티로 이동
                                        val intent = Intent(context, MainActivity::class.java)
                                        startActivity(intent)
                                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK);
                                        } else {
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); }

                                        requireActivity().finish()


                                    } else {
                                        //DB에서 탈퇴관련 데이터 정리 실패
                                        Log.d(MyPageFragment.TAG, "DB탈퇴처리실패")
                                        Toast.makeText(
                                            context,
                                            "탈퇴에 실패했습니다. 관리자에게 연락부탁드립니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                }


                            }

                            override fun onFailure(
                                call: Call<userAccountDeleteData>,
                                t: Throwable
                            ) {
                                Log.d(MyPageFragment.TAG, "실패 : $t")
                            }
                        })


                    }

                    override fun onCancel() {
                        Toast.makeText(requireContext(), "Login Cancelled", Toast.LENGTH_LONG)
                            .show()
                    }

                    override fun onError(exception: FacebookException) {
                        Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG)
                            .show()
                    }
                })


        } else if (social_login_type == "NAVER") {


            //기존 네이버 로그인 세션을 제거한다.
            OAuthLogin.getInstance().logout(context)
            Toast.makeText(
                context,
                "탈퇴를 위해 네이버 아이디 로그인이 필요합니다. 네이버 로그인 후, 탈퇴가 완료됩니다.",
                Toast.LENGTH_SHORT
            ).show()

            //네이버 로그인 창을 띄운다.
            //로그인에 성공하면 계정연결 해제가 이루어진다.
            //네이버 연동해제는 mOAuthLoginHandler를 확인할 것.
            OAuthLogin.getInstance().startOauthLoginActivity(activity, mOAuthLoginHandler)


        } else if (social_login_type == "GOOGLE") {

            //구글 연결해제 과정


            Log.d(TAG, "logout: 구글 로그아웃")

            //구글 로그아웃
            FirebaseAuth.getInstance().signOut()

            // 구글 로그인을 위해 구성되어야 하는 코드 (Id, Email request)
            var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()


            var googleSignInClient: GoogleSignInClient? = null

            googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }

            if (googleSignInClient != null) {

                //실질적인 구글 클라이언트 로그인 기록이 초기화된다.
                googleSignInClient.signOut()
            }

            //구글 로그인 후, 현재 로그인한 계정과 일치하면 탈퇴처리를 진행한다.
            //구글 탈퇴는 firebaseAuthWithGoogle를 확인할 것
            googleLogin()
            Toast.makeText(
                context,
                "현재 계정과 동일한 구글 계정을 선택해주세요. 로그인한 계정과 동일할 경우 탈퇴처리가 완료됩니다.",
                Toast.LENGTH_LONG
            ).show()


        }


        val pref = activity?.getSharedPreferences("pref_user_data", 0)
        val edit = pref?.edit()
        if (edit != null) {
            edit.putBoolean("login_check", false)
            edit.apply()//저장완료
        }


    }


    //네이버 소셜로그인 - 연동해제 및 탈퇴처리.
    val mOAuthLoginHandler: OAuthLoginHandler = object : OAuthLoginHandler() {
        override fun run(success: Boolean) {
            if (success) {

                // android.os.NetworkOnMainThreadException 에러 저거
                // 메인 쓰레드에서 네트워크 처리를 해주려면, 다음 두 줄을 추가해주면 됩니다.
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)

                // 네이버 회원 프로필 조회
                var jObject = JSONObject(
                    OAuthLogin.getInstance().requestApi(
                        context, OAuthLogin.getInstance().getAccessToken(
                            requireContext()
                        ), "https://openapi.naver.com/v1/nid/me"
                    ).toString()
                )


                //유저정보를 담고있는 JSONObject
                val response = jObject.getString("response")

                jObject = JSONObject(response)


                //네이버 유저 고유아이디
                val id = jObject.getString("id")

                //현재 접속중인 계정과 탈퇴를 위해 로그인한 네이버계정이 동일한지 확인
                if (table_user_id == id) {


                    //계정이 일치하면, DB탈퇴관련 레트로핏을 실행시킨다.
                    account_delete.enqueue(object : retrofit2.Callback<userAccountDeleteData> {
                        override fun onResponse(
                            call: Call<userAccountDeleteData>,
                            response: Response<userAccountDeleteData>
                        ) {
                            Log.d(MyPageFragment.TAG, "데이터로드 : ${response.raw()}")
                            Log.d(MyPageFragment.TAG, "데이터로드 : ${response.body().toString()}")

                            if (response.body() != null) {
                                val Item: userAccountDeleteData = response.body()!!


                                if (Item.success == true) {
                                    //DB에서 탈퇴관련 데이터 정리 완료
                                    Log.d(MyPageFragment.TAG, "DB탈퇴처리완료")

                                    OAuthLogin.getInstance().logoutAndDeleteToken(context)
                                    Log.d(
                                        "TAG",
                                        OAuthLogin.getInstance().getState(context).toString()
                                    )
                                    OAuthLogin.getInstance().getState(context)

                                        loadRoomList()


                                    Toast.makeText(context, "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT)
                                        .show()
                                    //연동해제 이후 로그인엑티비티로 이동
                                    val intent = Intent(context, MainActivity::class.java)
                                    startActivity(intent)
                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK);
                                    } else {
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); }

                                    requireActivity().finish()


                                } else {
                                    //DB에서 탈퇴관련 데이터 정리 실패
                                    Log.d(MyPageFragment.TAG, "DB탈퇴처리실패")
                                    Toast.makeText(
                                        context,
                                        "탈퇴에 실패했습니다. 관리자에게 연락부탁드립니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                            }


                        }

                        override fun onFailure(call: Call<userAccountDeleteData>, t: Throwable) {
                            Log.d(MyPageFragment.TAG, "실패 : $t")
                        }
                    })


                } else {
                    Toast.makeText(context, "현재 접속 중인 계정과 일치하지 않습니다.", Toast.LENGTH_SHORT).show()

                    OAuthLogin.getInstance().logoutAndDeleteToken(context)
                    Log.d("TAG", OAuthLogin.getInstance().getState(context).toString())
                    OAuthLogin.getInstance().getState(context)
                }


            } else {
                Toast.makeText(requireContext(), "아이디 또는 비밀번호를 다시 확인해주세요.", Toast.LENGTH_LONG)
                    .show()
                Log.d(TAG, "네이버회원탈퇴실패")
            }
        }

    }


//    //탈퇴 - DB처리
//    fun userAccountDelete(){
//
//        account_delete.enqueue(object : Callback<userAccountDeleteData> {
//            override fun onResponse(
//                call: Call<userAccountDeleteData>,
//                response: Response<userAccountDeleteData>
//            ) {
//                Log.d(MyPageFragment.TAG, "데이터로드 : ${response.raw()}")
//                Log.d(MyPageFragment.TAG, "데이터로드 : ${response.body().toString()}")
//
//                if(response.body() != null) {
//                    val Item: userAccountDeleteData = response.body()!!
//
//
//                    if(Item.success == true){
//                        //DB에서 탈퇴관련 데이터 정리 완료
//                        Log.d(MyPageFragment.TAG, "DB탈퇴처리완료")
//                    }else{
//                        //DB에서 탈퇴관련 데이터 정리 실패
//                        Log.d(MyPageFragment.TAG, "DB탈퇴처리실패")
//                    }
//
//
//                }
//
//
//            }
//
//            override fun onFailure(call: Call<userAccountDeleteData>, t: Throwable) {
//                Log.d(MyPageFragment.TAG, "실패 : $t")
//            }
//        })
//    }


    fun userProfileLoading(user_tb_id: Int) {
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

                var items: UserProfileData? = response.body()


                social_login_type = items!!.social_login_type
                table_user_id = items!!.user_id
                Log.d("소셜로그인 타입은?", social_login_type)


                if (items!!.introduction == null || items!!.introduction.length < 1) {
                    userProfileIntroductionTv.text = "자기소개가 없습니다."
                } else {
                    userProfileIntroductionTv.text = items!!.introduction
                }




                if(items.no_show_count >=3){
                    noShowCountTv.text= "노쇼 : ${items.no_show_count}회"
                    noShowCountTv.background = resources.getDrawable(R.drawable.social_login_google_button)
                    noShowCountTv.setTextColor(Color.WHITE)
                }else {
                    noShowCountTv.text="노쇼 : ${items.no_show_count}회"
                }


                //랭킹관련

                tierTv.text = "${items.tier}"
                rankingPointTv.text = "${items.rank_point}PT"


                Glide.with(requireContext())
                    .load(getString(R.string.http_request_base_url) + items!!.tier_image)
                    .into(tierBadgeImageIv)

                rankTv.text = "(${items.rank}위)"

                progressBar.dismiss()

            }

            override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
                Log.d("유저정보가져오기실패?", "실패 : $t")
            }
        })
    }

    fun loadRoomList() {
        mSocket = IO.socket(requireContext().getString(R.string.chat_socket_url))
        mSocket.connect()
        val retrofit = Retrofit.Builder()
            .baseUrl(requireContext().getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        retrofit.create(RoomAPI::class.java).accountRoom(MainActivity.user_table_id.toString())
            .enqueue(object : Callback<ArrayList<AccountSocketData>> {
                override fun onResponse(
                    call: Call<ArrayList<AccountSocketData>>,
                    response: Response<ArrayList<AccountSocketData>>
                ) {
                    if (response.body() != null) {
                        val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                        val date = Date()
                        val strDate = sdf.format(date)
                        for (room in response.body()!!) {
                            mSocket.emit(
                                "outRoom", gson.toJson(
                                    MessageData(
                                        "EXITROOM",
                                        "EXITROOM",
                                        room.roomId,
                                        MainActivity.loginUserNickname, "SERVER",
                                        strDate, room.members, 0
                                        , room.hostName
                                    )
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<AccountSocketData>>, t: Throwable) {

                }
            })
    }

    fun accountSocket() {


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