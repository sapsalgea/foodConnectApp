package com.example.abled_food_connect.works

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Location
import android.location.LocationManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.abled_food_connect.R
import com.example.abled_food_connect.retrofit.MapSearch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GpsWork(val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    lateinit var locatioNManager: LocationManager
    var currentLatitude: Double = 0.0
    var currentLongitude: Double = 0.0
    var perf = context.getSharedPreferences("pref_user_data", 0)
    val userIndex = perf.getInt("user_table_id", 0)
val TAG = "위치업뎃 워커"
    override suspend fun doWork(): Result {

         gps()
        return Result.success()
    }

    private fun gps() {

        locatioNManager = (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?)!!
        var userLocation: Location? = getLatLng()
        if (userLocation != null) {
            currentLatitude = userLocation.latitude
            currentLongitude = userLocation.longitude
            Log.d("CheckCurrentLocation", "현재 내 위치 값: ${currentLatitude}, ${currentLongitude}")
            serverGps(
                userIndex,
                currentLatitude.toString(),
                currentLongitude.toString()
            )
        }

    }

    private fun getLatLng(): Location? {
        val isGPSEnabled = locatioNManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        val isNetworkEnabled = locatioNManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        var currentLatLng: Location? = null
        var hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        var hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {

            if (isNetworkEnabled && !isGPSEnabled) {
                Log.e(TAG, "getLatLng: 네트워크 o GPS x", )
                val locatioNProvider = LocationManager.NETWORK_PROVIDER
                currentLatLng = locatioNManager.getLastKnownLocation(locatioNProvider)
            } else if (!isNetworkEnabled&&isGPSEnabled){
                Log.e(TAG, "getLatLng: 네트워크 x GPS o", )

                val locatioNProvider = LocationManager.GPS_PROVIDER
                currentLatLng = locatioNManager.getLastKnownLocation(locatioNProvider)
            }else if(isGPSEnabled){
                Log.e(TAG, "getLatLng: GPS o", )
                val locatioNProvider = LocationManager.GPS_PROVIDER
                currentLatLng = locatioNManager.getLastKnownLocation(locatioNProvider)
            }
            if (currentLatLng == null) {
                Log.e(TAG, "getLatLng: GPS null", )
                val locatioNProvider = LocationManager.GPS_PROVIDER
                currentLatLng = locatioNManager.getLastKnownLocation(locatioNProvider)
            }

        } else {

            currentLatLng = getLatLng()
        }
        return currentLatLng
    }

    private fun serverGps(userIndex: Int, x: String, y: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("ServerIP")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(MapSearch::class.java).update(userIndex, x, y).enqueue(object :
            Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {

                if (response.body() == "true") {
                    Log.e("업뎃 성공", "업뎃 성공")
                    val workRequest:OneTimeWorkRequest =
                        OneTimeWorkRequestBuilder<GpsWork>().setInitialDelay(
                            5,
                            TimeUnit.SECONDS
                        ).build()
                    WorkManager.getInstance(context).enqueueUniqueWork("gpsUpdate",ExistingWorkPolicy.KEEP,workRequest)


                } else {
                    Log.e("업뎃 안함", "업뎃 안함")
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {

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

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val id = "app"
        val title = "위치 정보"
        // This PendingIntent can be used to cancel the worker
//        val intent = WorkManager.getInstance(applicationContext)
//            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setTicker(title)
            .setContentText(progress)
            .setOngoing(true)
            .setVibrate(longArrayOf(0))
            // Add the cancel action to the notification which can
            // be used to cancel the worker
//            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(FOREGROUND_SERVICE_TYPE_LOCATION, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val name = "app"
        val descriptionText = "text"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel("app", name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

}