package com.example.abled_food_connect.retrofit


import android.content.Context
import com.example.abled_food_connect.data.NewActionAlarmCheckData
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.*
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class API {
    interface foodCoonnectCreateRoom {
        @POST("createRoom.php")
        fun createRoom(
            @Query("title") title:String,
            @Query("info") info:String,
            @Query("numOfPeople")numOfPeople:Int,
            @Query("date") date:String,
            @Query("address") address:String,
            @Query("shopName") shopName:String,
            @Query("keyWords") keyWords:String,
            @Query("gender") gender:String,
            @Query("minimumAge") minimumAge:Int,
            @Query("maximumAge") maximumAge:Int
        ):Call<String>
    }
    //회원가입시 닉네임이 중복되는지 확인한다.
    interface nicNameCheck{
        @FormUrlEncoded
        @POST("user_info/nicname_duplicate_check.php")
        fun checkNicName(
            @Field("nick_name") nick_name:String ):Call<String>
    }


    //프로필 수정 - 원본이미지와 압축된 이미지를 보낸다.
    interface UserProfileModifyStringChange_interface {
        @Multipart
        @POST("user_info/user_profile_just_String_modify.php")
        fun user_profile_just_String_modify_Request(
            @Part("user_tb_id") user_tb_id: Int,
            @Part("nic_name") nic_name: String,
            @Part("introduction") introduction: String
        ): Call<String>

    }


    //프로필 수정 - 원본이미지와 압축된 이미지, 아이디, 자기소개를 보낸다.
    interface UserProfileEvaluationListInterface {
        @Multipart
        @POST("review/user_profile_evaluation_list.php")
        fun user_profile_evaluation_list_Request(
            @Part("user_tb_id") user_tb_id: Int
        ): Call<UserProfileEvaluationListData>

    }


    //프로필 수정 - 원본이미지와 압축된 이미지, 아이디, 자기소개를 보낸다.
    interface UserProfileModifyImageChange_interface {
        @Multipart
        @POST("user_info/user_profile_modify.php")
        fun user_profile_modify_Request(
            @Part imageFile : MultipartBody.Part,
            @Part thumbnail_imageFile : MultipartBody.Part,
            @Part("user_tb_id") user_tb_id: Int,
            @Part("nic_name") nic_name: String,
            @Part("introduction") introduction: String
        ): Call<String>

    }


    interface roomTbDbInfoGetInterface {


        @Multipart
        @POST("review/room_tb_db_info_get.php")
        fun room_tb_db_info_get(
            @Part("room_id") room_id: Int
        ): Call<RoomTbDbInfoData>

    }




    interface reviewWriting{
        // 리뷰작성하기 보내기
        @Multipart
        @POST("review/review_writing.php")
        fun review_Writing_Request(
            //@Part itemphoto: ArrayList<MultipartBody.Part>,
            @Part itemphoto: List<MultipartBody.Part>,
            @Part("room_tb_id") room_tb_id: Int,
            @Part("writer_user_tb_id") writer_user_tb_id: Int,
            @Part("writer_uid") writer_uid: String,
            @Part("writer_nicname") writer_nicname: String,
            @Part("restaurant_address") restaurant_address: String,
            @Part("restaurant_name") restaurant_name: String,
            @Part("reporting_date") reporting_date: String,
            @Part("appointment_day") appointment_day: String,
            @Part("appointment_time") appointment_time: String,
            @Part("review_description") review_description: String,
            @Part("rating_star_taste") rating_star_taste: Int,
            @Part("rating_star_service") rating_star_service: Int,
            @Part("rating_star_clean") rating_star_clean: Int,
            @Part("rating_star_interior") rating_star_interior: Int): Call<ReviewWritingData>
    }


    //리뷰에서 코멘트 버튼을 눌렀을때, 리뷰내역 가져오기

    interface reviewCommentReviewContentGet {

        @Multipart
        @POST("review/review_comment_activity_review_content_get.php")
        fun review_comment_review_content_get_interface(
            @Part("review_id") review_id: Int,
            //사용자 id는 좋아요 정보를 조회할때 사용한다.
            @Part("user_tb_id") user_tb_id: Int
        ): Call<ReviewDetailViewRvData>

    }


    interface reviewLikeBtnClick{
        // 좋아요 버튼 클릭
        @Multipart
        @POST("review/review_like_btn_click.php")
        fun review_Like_Btn_Click(
            @Part("sendTargetUserTable_id") sendTargetUserTable_id: Int,
            @Part("like_click_user_nicname") like_click_user_nicname: String,
            @Part("which_text_choose") which_text_choose: String,
            @Part("what_click_review_tb_id") what_click_review_tb_id: Int,
            @Part("my_user_tb_id") my_user_tb_id: Int,
            @Part("my_user_tb_user_id") my_user_tb_user_id: String
            ): Call<ReviewLikeBtnClickData>
    }


    interface reviewParentPageCommentListGet{
        // 부모 페이지 에서 댓글 목록 가져오기
        @Multipart
        @POST("review/review_parent_page_comment_list_get.php")
        fun reviewParentPageCommentListCalling(
            @Part("review_id") review_id: Int
        ): Call<ReviewParentPageCommentGetData>
    }

    interface reviewParentPageCommentWriting{
        // 부모페이지에서 댓글 작성
        @Multipart
        @POST("review/review_parent_page_comment_writing.php")
        fun reviewParentPageCommentWritingSend(
            @Part("which_text_choose") which_text_choose: String,
            @Part("comment_writer_nicname") comment_writer_nicname: String,
            @Part("review_id") review_id: Int,
            @Part("writing_user_id") writing_user_id: Int,
            @Part("comment_content") comment: String,
            @Part("comment_class") comment_class: Int,
            @Part("sendTargetUserTable_id") sendTargetUserTable_id: Int,
            @Part("sendTargetUserNicName") sendTargetUserNicName: String,
            @Part("groupNum") groupNum: Int
        ): Call<ReviewParentPageCommentGetData>
    }



    interface reviewChildPageCommentListGet{
        // 댓글 목록 가져오기
        @Multipart
        @POST("review/review_child_page_comment_list_get.php")
        fun reviewChildPageCommentListGetCalling(
            @Part("review_id") review_id: Int,
            @Part("groupNum") groupNum: Int
        ): Call<ReviewChildPageCommentGetData>
    }


    interface reviewChildPageCommentWriting{
        // 자식 댓글(코멘트)작성
        @Multipart
        @POST("review/review_child_page_comment_writing.php")
        fun review_child_page_comment_send_btn_click(
            @Part("reviewWritingUserId") reviewWritingUserId: Int,
            @Part("which_text_choose") which_text_choose: String,
            @Part("comment_writer_nicname") comment_writer_nicname: String,
            @Part("review_id") review_id: Int,
            @Part("writing_user_id") writing_user_id: Int,
            @Part("comment_content") comment: String,
            @Part("comment_class") comment_class: Int,
            @Part("sendTargetUserTable_id") sendTargetUserTable_id: Int,
            @Part("sendTargetUserNicName") sendTargetUserNicName: String,
            @Part("groupNum") groupNum: Int
        ): Call<ReviewChildPageCommentGetData>
    }


    interface reviewDetailViewLikeAndCommentCountCheck{
        // 리뷰 목록에서 댓글을 작성했다가 다시 뒤로 왔을때, 리뷰개수나 댓글개수가 변화가 있을 경우 올라가야함.
        @Multipart
        @POST("review/review_detail_view_like_and_commentcount_check.php")
        fun reviewDetailViewLikeAndCommentCountCheckGetCalling(
            @Part("review_tb_id") review_id: Int,
            @Part("user_tb_id") user_tb_id: Int
        ): Call<ReviewDetailViewLikeAndCommentCountCheckData>
    }


    interface rankingLatestThreeGetInterface {


        @Multipart
        @POST("ranking/ranking_latest_three_get.php")
        fun ranking_latest_three_get(
            @Part("user_tb_id") user_tb_id: Int
        ): Call<userProfileRankingLatestThreeData>

    }




    interface reviewPageCommentWriting{
        // 리뷰보다가 댓글(코멘트)작성
        @Multipart
        @POST("review/review_comment_writing.php")
        fun review_comment_send_btn_click(
            @Part("review_id") review_id: Int,
            @Part("writing_user_id") writing_user_id: Int,
            @Part("comment_content") comment: String,
            @Part("comment_class") comment_class: Int,
            @Part("sendTargetUserTable_id") sendTargetUserTable_id: Int,
            @Part("sendTargetUserNicName") sendTargetUserNicName: String,
            @Part("groupNum") groupNum: Int
        ): Call<ReviewChildPageCommentGetData>
    }


    interface ReviewSearchRvInterface {

        @Multipart
        @POST("review/review_search_rv_list_get.php")
        fun review_search_rv_using_get(
            @Part("option_number") option_number: Int,
            @Part("search_keyword") search_keyword: String,
            @Part("userId") userId: Int
        ): Call<ReviewDetailViewRvData>

    }





    interface chatImageSend_interface {
        // 프로필 이미지 보내기
        @Multipart
        @POST("chat/DirectMessageImageSending.php")
        fun chat_image_send_interface_Request(
            @Part imageFile : MultipartBody.Part
        ): Call<ChatImageSendingData>

    }


    interface getChattingList_Interface{
        // UserProfileActivity에서 유저정보 가져오기
        @Multipart
        @POST("chat/direct_message_list_get.php")
        fun direct_message_list_get(
            @Part("roomName") roomName: String,
            @Part("dm_log_tb_id") dm_log_tb_id: Int
        ): Call<DirectMessageNodeServerSendData>
    }



    interface dmRoomJoinCheck_Interface{
        // UserProfileActivity에서 유저정보 가져오기
        @Multipart
        @POST("chat/dm_room_join_check.php")
        fun dm_room_join_check(
            @Part("dm_room_name") dm_room_name: String,
            @Part("my_user_tb_id") my_user_tb_id: Int,
            @Part("your_user_tb_id") your_user_tb_id: Int
        ): Call<String>
    }

    interface DMChattingListGet_Interface{
        @Multipart
        @POST("chat/dm_chatting_list_get.php")
        fun dm_room_join_check(
            @Part("my_user_tb_id") my_user_tb_id: Int
        ): Call<ChattingFragmentDmRvData>
    }



    interface UserProfileDataInterface{
        // UserProfileActivity에서 유저정보 가져오기
        @Multipart
        @POST("user_info/user_profile_data_get.php")
        fun user_profile_data_get(
            @Part("user_tb_id") user_tb_id: Int
        ): Call<UserProfileData>
    }


    interface UserProfileJoinHistoryRvInterface {
        @Multipart
        @POST("schedule/user_profile_join_history_rv_list_get.php")
        fun user_profile_join_history_rv_list_get(
            @Part("user_tb_nicname") user_tb_nicname: String
        ): Call<UserProfileJoinHistoryRvData>
    }


    interface deleteReviewCheckInterface {

        @Multipart
        @POST("review/review_delete_check.php")
        fun review_delete_check(
            @Part("review_tb_id") review_tb_id: Int
        ): Call<ReviewDeleteCheckData>

    }



    interface UserProfileClickedReviewGridListRvInterface {

        @Multipart
        @POST("review/user_profile_clicked_review_grid_rv_list_get.php")
        fun user_profile_clicked_review_grid_list_get(
            @Part("clicked_user_tb_id") clicked_user_tb_id: String,
            @Part("my_user_tb_id") my_user_tb_id: Int
        ): Call<ReviewDetailViewRvData>

    }


    interface deletedReviewCheckUserProfileClickedReviewGridListRvInterface {

        @Multipart
        @POST("review/deleted_Review_Check_user_profile_clicked_review_grid_rv_list_get.php")
        fun deleted_Review_Check_user_profile_clicked_review_grid_rv_list_get(
            @Part("review_id") review_id: Int
        ): Call<String>

    }


    interface MyPageUserScheduleRvInterface {

        @Multipart
        @POST("schedule/my_page_user_schedule_get.php")
        fun my_page_user_schedule_rv_get(
            @Part("user_tb_nicname") user_tb_nicname: String,
            @Part("date_reverse") date_reverse: Int //데이트 리버스는 일정리스트에서 예정된 일정의 날짜는 미래의 날짜를 asc, 완료된 일정은 과거읜 날을 desc
        ): Call<MyPageUserScheduleData>

    }



    interface UnWrittenReviewListRvInterface {

        @Multipart
        @POST("schedule/un_written_review_list_get.php")
        fun un_written_review_list_get(
            @Part("user_tb_nicname") user_tb_nicname: String
        ): Call<MyPageUserScheduleData>

    }

    interface UserProfileBadgeListDataGetInterface {
        @Multipart
        @POST("user_info/user_profile_badge_list_data_get.php")
        fun user_profile_badge_list_data_get(
            @Part("user_tb_id") user_tb_id: Int,
            @Part("user_tb_nicname") user_tb_nicname: String
        ): Call<UserProfileBadgeListData>
    }


    interface MeetingUserEvaluationWritingBeforeNoShowCheck{
        // 좋아요 버튼 클릭
        @Multipart
        @POST("schedule/meeting_user_evaluation_writing_before_no_show_check.php")
        fun meeting_user_evaluation_writing_before_no_show_check(
            @Part("room_id") room_id: Int,
            @Part("user_tb_id") user_tb_id: Int
        ): Call<MeetingUserEvaluationWritingBeforeNoShowCheckData>
    }




    interface MeetingEvaluationUserListRvInterface {

        @Multipart
        @POST("schedule/meeting_evaluation_user_list.php")
        fun meeting_evaluation_user_list_get(
            @Part("room_id") room_id: Int
        ): Call<MeetingEvaluationUserListRvData>

    }

    interface MeetingUserEvaluationWritingInterface {

        @Multipart
        @POST("schedule/meeting_user_evaluation_writing.php")
        fun meeting_user_evaluation_writing(
            @Part("meeting_user_evaluation_Json") meeting_user_evaluation_Json: String,
            @Part("my_user_tb_id") my_user_tb_id: Int,
            @Part("my_user_nicname") my_user_nicname: String
        ): Call<MeetingUserEvaluationWritingData>

    }


    interface rankingFragmentRvSeasonPointListGetInterface {

        @Multipart
        @POST("ranking/season_point_list_get.php")
        fun season_point_list_get(
            @Part("my_nicname") my_nicname: String

        ): Call<RankingFragmentRvData>

    }


    interface rankingFragmentRvSeasonTierListGetInterface {

        @Multipart
        @POST("ranking/season_tier_list_get.php")
        fun season_tier_list_get(
            @Part("what_tier") what_tier: String

        ): Call<RankingFragmentRvData>

    }


    interface rankingRvPagingToDownInterface {

        @Multipart
        @POST("ranking/ranking_paging_to_down.php")
        fun paging_to_down_get(
            @Part("lastArrayNumber") lastArrayNumber: Int,
            @Part("what_tier") what_tier: String

        ): Call<RankingFragmentRvData>

    }

    interface rankingNicNameSearchInterface {

        @Multipart
        @POST("ranking/ranking_nicname_search.php")
        fun anking_nicname_search_get(
            @Part("searchNicName") searchNicName: String

        ): Call<RankingFragmentRvData>

    }


    interface userProfileRankingGetInterface {
        @Multipart
        @POST("user_info/user_profile_ranking_get.php")
        fun user_profile_ranking_get(
            @Part("user_tb_id") user_tb_id: Int
        ): Call<userProfileRankingLatestThreeData>
    }


    interface clickedCommentDataLoadingInterface{
        // 리뷰삭제
        @Multipart
        @POST("review/clicked_comment_data_loading.php")
        fun clicked_comment_data_loading(
            @Part("review_id") review_id: Int,
            @Part("comment_tb_id") comment_tb_id: Int


        ): Call<ReviewParentPageCommentGetData>
    }


    interface reviewDeleteBtn{
        // 리뷰삭제
        @Multipart
        @POST("review/review_delete_btn_click.php")
        fun review_delete_btn_click(
            @Part("my_user_tb_id") my_user_tb_id: Int,
            @Part("what_click_review_tb_id") what_click_review_tb_id: Int,
            @Part("room_id") room_id: Int

        ): Call<ReviewDeleteData>
    }



    interface commentDeleteBtn{
        // 댓글삭제
        @Multipart
        @POST("review/comment_delete_btn_click.php")
        fun comment_delete_btn_click(
            @Part("my_user_tb_id") my_user_tb_id: Int,
            @Part("what_click_comment_id") what_click_comment_id: Int,
            @Part("review_id") review_id: Int,
            @Part("parentOrChild") parentOrChild: Int,
            @Part("groupNum") groupNum: Int

        ): Call<CommentDeleteData>
    }



    //일정리스트에서 방제목을 클릭하면, 방정보를 가져온다.
    //방정보 엑티비티로 가기 위해서는 방정보에 필요한 데이터를 가져와서 넘겨줘야한다.
    interface roomInfoGetInterface {
        @Multipart
        @POST("schedule/room_info_get.php")
        fun room_info_get(
            @Part("room_id") room_id: Int,
            @Part("hostName") hostName: String
        ): Call<ScheduleClickRoomInfoData>

    }



    interface whenLogoutFcmtokenDeleteInterface {
        @Multipart
        @POST("user_info/when_logout_fcmtoken_delete.php")
        fun when_logout_fcmtoken_delete(
            @Part("user_tb_id") user_tb_id: Int
        ): Call<whenLogoutFcmtokenDeleteData>
    }


    interface userAccountDeleteInterface {
        @Multipart
        @POST("user_info/user_account_delete.php")
        fun user_account_delete(
            @Part("user_tb_id") user_tb_id: Int
        ): Call<userAccountDeleteData>
    }

    interface ActionAlarmListGetInterface{
        // 좋아요 버튼 클릭
        @Multipart
        @POST("schedule/action_alarm_list_get.php")
        fun action_alarm_list_get(
            @Part("user_tb_id") user_tb_id: Int,
        ): Call<ActionAlarmListData>
    }


    interface ActionAlarmActivityEnterCheckInterface{
        // 좋아요 버튼 클릭
        @Multipart
        @POST("schedule/action_alarm_activity_enter_check.php")
        fun action_alarm_activity_enter_check(
            @Part("user_tb_id") user_tb_id: Int,
        ): Call<ActionAlarmActivityEnterCheckData>
    }

    interface NewActionAlarmCheckInterface{
        // 좋아요 버튼 클릭
        @Multipart
        @POST("schedule/new_action_alarm_check.php")
        fun new_action_alarm_check(
            @Part("user_tb_id") user_tb_id: Int,
        ): Call<NewActionAlarmCheckData>
    }


    interface RankingExplanationActivityEnterCheckInterface {

        @Multipart
        @POST("user_info/ranking_explanation_activity_enter_check.php")
        fun ranking_explanation_activity_enter_check(
            @Part("my_user_tb_id") my_user_tb_id: Int,
        ): Call<String>

    }



    fun joinRoom(context:Context,roomId:String,nickName:String,userIndexId:String){

        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)
        server.joinRoom(roomId,nickName,userIndexId).enqueue(object: Callback<joinRoomClass>{
            override fun onResponse(call: Call<joinRoomClass>, response: Response<joinRoomClass>) {
                val success : joinRoomClass = response.body()!!
                if (success.success){

                }else{

                }
            }

            override fun onFailure(call: Call<joinRoomClass>, t: Throwable) {

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
    data class joinRoomClass(
        @SerializedName("success")
        val success: Boolean
    )
    data class createRoomHost(
        @SerializedName("success")
        val success :Boolean,
        @SerializedName("room")
        val roomId:MainFragmentItemData
    )
}