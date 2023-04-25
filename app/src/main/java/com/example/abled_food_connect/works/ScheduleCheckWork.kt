package com.example.abled_food_connect.works

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.*
import com.example.abled_food_connect.R
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class ScheduleCheckWork(val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    private val getShared: SharedPreferences =
        context.getSharedPreferences("pref_user_data", Context.MODE_PRIVATE)
    private val userIndex = getShared.getInt("user_table_id", 0)
    override fun doWork(): Result {
        scheduleCheck(userIndex)
        return Result.success()
    }


    @SuppressLint("RestrictedApi")
    private fun doWorkWithGpsWork(context: Context, delay: Long, roomTag: String) {

        when (true) {

            (delay > 0) -> {


                val data = Data.Builder().put("roomId", roomTag).build()
                Log.d("CheckGpsWorker", "worker 시작함수 진입")
                val workRequest: OneTimeWorkRequest =
                    OneTimeWorkRequestBuilder<GpsWork>().setInputData(data).setInitialDelay(
                        delay,
                        TimeUnit.SECONDS
                    ).build()

                /*
                    ExistingPeriodicWorkPolicy.KEEP     :  워크매니저가 실행중이 아니면 새로 실행하고, 실행중이면 아무작업도 하지 않는다.
                    ExistingPeriodicWorkPolicy.REPLACE  :  워크매니저를 무조건 다시 실행한다.
                 */
                WorkManager.getInstance(context).enqueueUniqueWork(
                    roomTag,
                    ExistingWorkPolicy.REPLACE, workRequest
                )
                Log.d("GPSdd", "방번호 : ${roomTag} 실행여부 : ${check(roomTag)}")
            }

            delay>=-3600&&0>=delay->{
                Log.d("CheckGpsWorker", "worker 바로시작함수 진입")
                val data = Data.Builder().put("roomId", roomTag).build()
                val workRequest: OneTimeWorkRequest =
                    OneTimeWorkRequestBuilder<GpsWork>().setInputData(data).build()
                WorkManager.getInstance(context).enqueueUniqueWork(
                    roomTag,
                    ExistingWorkPolicy.REPLACE, workRequest
                )
            }
            else -> {


            }
        }


    }

    private fun scheduleCheck(userIndex: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java).scheduleCheck(userIndex)
            .enqueue(object : Callback<ScheduleArray> {
                override fun onResponse(
                    call: Call<ScheduleArray>,
                    response: Response<ScheduleArray>
                ) {


                    if (response.body() != null) {
                        if (response.body()!!.list.size > 0) {
                            for (item in response.body()!!.list) {

                                Log.e(
                                    "스케줄체크워크",
                                    "onResponse: ${item.string} 남은시간${(item.time.toDouble() / 60)} 분",
                                )
                                doWorkWithGpsWork(context, item.time, item.string)


                            }
                        }
                    }


                }

                override fun onFailure(
                    call: Call<ScheduleArray>,
                    t: Throwable
                ) {

                }
            })


    }

    fun check(tag: String): Boolean {
        val instance = WorkManager.getInstance(context)
        var status: ListenableFuture<MutableList<WorkInfo>> =
            instance.getWorkInfosForUniqueWork(tag)
        return try {
            var running = false;
            var list: MutableList<WorkInfo> = status.get()
            for (workInfo in list) {
                val state = workInfo.state
                running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
            }
            running
        } catch (e: ExecutionException) {
            false
        } catch (e: InterruptedException) {
            false
        }
    }

    private fun createOkHttpClient(): OkHttpClient {
        //Log.d ("TAG","OkhttpClient");
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }

    inner class RoomsSchedule(
        @SerializedName("roomId") val string: String,
        @SerializedName("time") val time: Long
    )

    inner class ScheduleArray(@SerializedName("list") val list: ArrayList<RoomsSchedule>)
}