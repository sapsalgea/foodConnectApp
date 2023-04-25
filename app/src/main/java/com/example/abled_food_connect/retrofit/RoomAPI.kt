package com.example.abled_food_connect.retrofit


import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.works.ScheduleCheckWork
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface RoomAPI {

    //방 생성
    @FormUrlEncoded
    @POST("createRoom.php")
    fun createRoom(
        @Field("userIndexId") userIndexId: String,
        @Field("title") title: String,
        @Field("info") info: String,
        @Field("numOfPeople") numOfPeople: String,
        @Field("date") date: String,
        @Field("time") time: String,
        @Field("address") address: String,
        @Field("roadAddress") roadAddress: String,
        @Field("placeName") placeName: String,
        @Field("shopName") shopName: String,
        @Field("keyWords") keyWords: String,
        @Field("gender") gender: String,
        @Field("minimumAge") minimumAge: String,
        @Field("maximumAge") maximumAge: String,
        @Field("hostName") hostName: String,
        @Field("hostIndex") hostIndex:Int,
        @Field("map_x") map_x: String,
        @Field("map_y") map_y: String
    ): Call<API.createRoomHost>

    //메인프레그먼트 방리스트 로드
    @FormUrlEncoded
    @POST("MainFragmentLoading.php")
    fun loadingRoomGet(
        @Field("userId") userId: String

    ): Call<LoadingRoom>

    //메인프레그먼트에서 방정보보기화면일때 입장중인지 체크
    @FormUrlEncoded
    @POST("RoomJoinCheck.php")
    fun joinRoomCheck(
        @Field("roomId") roomId: String,
        @Field("nickName") nickName: String,
        @Field("hostName") hostName: String

    ): Call<JoinRoomCheck>

    //방 입장처리
    @FormUrlEncoded
    @POST("RoomJoin.php")
    fun joinRoom(
        @Field("roomId") roomId: String,
        @Field("nickName") nickName: String,
        @Field("userIndexId") userIndexId: String

    ): Call<API.joinRoomClass>


//    @retrofit2.http.Multipart
//    @POST("/upload")
//    open
//    fun uploadImage(@Part image: Part?): Call<Message?>?

    //날짜 구분선 유무 체크
    @FormUrlEncoded
    @POST("/groupChat/datelineCheck.php")
    fun timelineCheck(
        @Field("datetime") datetime: String?,
        @Field("roomId") roomId: String
    ): Call<member>


    //그룹채팅방 스크롤 올렸을때 과거 메시지 추가 로드
    @GET("/groupChat/chatPagenation.php")
    fun pagination(
        @Query("roomId") roomId: String,
        @Query("page") page: Int,
        @Query("userIndex") userIndex: Int
    ): Call<paginationData>

    //그룹 채팅방 메시지 읽음 처리후 메시지갱신 or 최초 메시지 로딩
    @GET("/groupChat/chatload.php")
    fun paginationRead(
        @Query("roomId") roomId: String,
        @Query("pageEnd") pageEnd: Int,
        @Query("userIndex") userIndex: Int
    ): Call<paginationData>

    // 그룹채팅 메시지 읽음 처리
    @FormUrlEncoded
    @POST("/groupChat/readMessage.php")
    fun readMessage(
        @Field("roomId") roomId: String,
        @Field("userIndex") userIndex: Int
    ): Call<String>

    //방 입장신청  보기
    @FormUrlEncoded
    @POST("/groupChat/joinSubscription.php")
    fun joinSubscription(
        @Field("roomId") roomId: String?,
        @Field("userIndexId") userIndexId: String?
    ): Call<SubscriptionData>

    //방 입장신청 체크
    @FormUrlEncoded
    @POST("/groupChat/subscriptionCheck.php")
    fun hostSubscriptionCheck(
        @Field("roomId") roomId: String?,

        ): Call<ChatRoomSubscriptionResult>

    //방 입장신청 목록갱신
    @FormUrlEncoded
    @POST("/groupChat/subscriptionUpdate.php")
    fun hostSubscriptionStatusUpdate(
        @Field("subNumber") subNumber: String?,
        @Field("status") status: String?

    ): Call<String>

    //그룹채팅 방입장되어 있는 유저 리스트 로드
    @FormUrlEncoded
    @POST("/groupChat/joinRoomMember.php")
    fun joinRoomMember(
        @Field("roomId") roomId: String
    ): Call<GroupChatUserList>

    //그룹채팅 방나가기
    @FormUrlEncoded
    @POST("/groupChat/exitRoom.php")
    fun exitRoom(
        @Field("roomId") roomId: String,
        @Field("userIndexId") userIndexId: String,
        @Field("userNickName") userNickName: String
    ): Call<String>
    @FormUrlEncoded
    @POST("/groupChat/kickRoom.php")
    fun kickRoom(
        @Field("roomId") roomId: String,
        @Field("userIndexId") userIndexId: String,
        @Field("userNickName") userNickName: String
    ): Call<String>
    //그룹채팅 방없애기
    @FormUrlEncoded
    @POST("/groupChat/deleteRoom.php")
    fun deleteRoom(
        @Field("roomId") roomId: String,
        @Field("userIndexId") userIndexId: String,
        @Field("userNickName") userNickName: String
    ): Call<String>
    @FormUrlEncoded
    @POST("/groupChat/changeHost.php")
    fun changeHost(
        @Field("roomId") roomId: String,
        @Field("userNickName") userNickName: String,
        @Field("userIndex")userIndex:Int
    ): Call<String>

    @FormUrlEncoded
    @POST("/groupChat/nowPeople.php")
    fun nowPeople(
        @Field("roomId") roomId: String
    ): Call<String>

    //채팅프레그먼트에서 그룹채팅목록 불러오기
    @FormUrlEncoded
    @POST("groupChat/LoadGroupChatList.php")
    fun loadGroupChatList(
        @Field("userIndexId") userIndexId: String,
        @Field("userNickName") userNickName: String


    ): Call<LoadingGroupChat>

    //위치보기가 가능한 시간인지 체크
    @FormUrlEncoded
    @POST("groupChat/mapActivityTimeCheck.php")
    fun roomStatusTime(
        @Field("roomId") roomId: String
    ): Call<Double>

    //그룹채팅 위치보기에서 유저위지 받아오기
    @FormUrlEncoded
    @POST("groupChat/mapActivityMember.php")
    fun memberLocation(
        @Field("roomId") roomId: Int
    ): Call<GroupChatLocationData>

    //서버에 이미지 보내기
    @Multipart
    @POST("groupChat/groupChatImageSend.php")
    fun groupChatImageSend(
        @Part imageFile: MultipartBody.Part,
        @Part("roomId") roomId: String,
        @Part("userIndex") userIndex: Int,
        @Part("userNickname") userNickname: String
    ): Call<ChatImageSendingData>

    //    @FormUrlEncoded
    @POST("DateCheck.php")
    fun dateCheck(): Call<Long>

    @FormUrlEncoded
    @POST("ScheduleCheck.php")
    fun scheduleCheck(
        @Field("userIndex") userIndex: Int

    ): Call<ScheduleCheckWork.ScheduleArray>

    @FormUrlEncoded
    @POST("tokenInsert.php")
    fun tokenInsert(
        @Field("userIndex") userIndex: Int,
        @Field("token") token: String

    ): Call<String>

    @FormUrlEncoded
    @POST("roomSearch.php")
    fun roomSearch(
        @Field("type") type: String,
        @Field("content") content: String
    ): Call<LoadingRoom>

    @FormUrlEncoded
    @POST("groupChat/LoadChatRoomInfo.php")
    fun groupChatRoomInfoLoad(
        @Field("roomId") userId: String

    ): Call<LoadingRoom>

    @FormUrlEncoded
    @POST("HostGroupDoneFCM.php")
    fun hostGroupDone(
        @Field("roomId") roomId: String

    ): Call<String>
    @FormUrlEncoded
    @POST("outRoomCheck.php")
    fun outRoomCheck(
        @Field("roomId") roomId: String
    ): Call<LoadingRoom>

    @FormUrlEncoded
    @POST("accountRoom.php")
    fun accountRoom(
        @Field("userIndex") userIndex: String
    ): Call<ArrayList<AccountSocketData>>

    @FormUrlEncoded
    @POST("loadRoomInfo.php")
    fun loadRoomInfo(
        @Field("roomId") roomId: String,
        @Field("userIndex") userIndex:Int
    ): Call<String>
}

