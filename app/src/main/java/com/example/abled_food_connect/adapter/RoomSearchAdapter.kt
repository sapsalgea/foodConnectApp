package com.example.abled_food_connect.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.RoomInformationActivity
import com.example.abled_food_connect.data.JoinRoomCheck
import com.example.abled_food_connect.data.MainFragmentItemData
import com.example.abled_food_connect.fragments.MainFragment
import com.example.abled_food_connect.retrofit.RoomAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RoomSearchAdapter(val context: Context, private val list: ArrayList<MainFragmentItemData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    var unList = list
    var filList = ArrayList<MainFragmentItemData>()
    lateinit var mMainFragment: MainFragment

    constructor(
        context: Context,
        mainFragment: MainFragment,
        list: ArrayList<MainFragmentItemData>
    ) : this(context, list) {
        mMainFragment = mainFragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return CustomHolder(
            LayoutInflater.from(context).inflate(R.layout.main_page_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val maindata: MainFragmentItemData = filList[position]
        val testholder: CustomHolder = holder as CustomHolder
        testholder.roomStatus.text = maindata.title
        testholder.shopName.text = maindata.info
        testholder.roomStatus
        if (maindata.roomStatus!!>0&&maindata.nowNumOfPeople == maindata.numOfPeople) {
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_full)
            testholder.roomStatus.text = "FULL"
        } else
        if (maindata.roomStatus !!> 5) {
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_recruitment)
            testholder.roomStatus.text = "모집중"
        } else if (maindata.roomStatus !!> 0.9) {
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_imminent)
            val text: String = context.getString(R.string.room_status_imminent_time)
            testholder.roomStatus.text =
                String.format(text, Math.round(maindata.roomStatus!!).toInt())

        } else if (maindata.roomStatus !!< 0.9 && maindata.roomStatus!! > 0.0) {
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_deadline_imminent)
            testholder.roomStatus.text = "임박"

        } else if (maindata.roomStatus!! < 0) {
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_deadline)
            testholder.roomStatus.text = "마감"
        }
        if (maindata.gender.equals("male")) {
            testholder.gender.setImageResource(R.drawable.ic_male)
        } else if (maindata.gender == "female") {
            testholder.gender.setImageResource(R.drawable.ic_female)
        } else {
            testholder.gender.setImageResource(R.drawable.ic_maleandfemale)
        }

        if (maindata.maximumAge == maindata.minimumAge) {
            testholder.roomAge.text = maindata.maximumAge.toString()
        } else {
            val text: String = context.getString(R.string.limit_age_badge)
            testholder.roomAge.text = String.format(text, maindata.minimumAge, maindata.maximumAge)
        }
        if (maindata.joinMember!!.contains(MainActivity.user_table_id.toString())) {
            testholder.joinCheckImageView.visibility = View.VISIBLE
        } else {
            testholder.joinCheckImageView.visibility = View.INVISIBLE
        }
        testholder.shopName.text = maindata.placeName
        testholder.roomTitle.text = maindata.title
        testholder.roomNumberOfPeople.text =
            "${maindata.nowNumOfPeople.toString()}/${(maindata.numOfPeople).toString()}명"
        testholder.roomDateTime.text = maindata.date
        val splitAddress = maindata.address.toString().split(" ")
        val splitAddress2 = splitAddress[0].split(" ")
        var location: String = ""
        for (index in 0..2) {
            location += splitAddress[index] + ">"
        }
        location = location.substring(0, location.length - 1)
        testholder.roomLocation.text = location
        testholder.itemView.setOnClickListener { joinRoomCheckMethod(maindata, maindata.address!!) }


    }

    override fun getItemCount(): Int {

        return filList.size
    }


    class CustomHolder(view: View) : RecyclerView.ViewHolder(view) {
        var roomStatus: TextView = view.findViewById(R.id.tvRoomStatus)
        var shopName: TextView = view.findViewById(R.id.tvShopName)
        var gender: ImageView = view.findViewById(R.id.ivGender)
        var roomTitle: TextView = view.findViewById(R.id.tvRoomTitle)
        var roomDateTime: TextView = view.findViewById(R.id.tvRoomDateTime)
        var roomLocation: TextView = view.findViewById(R.id.tvRoomLocation)
        var roomNumberOfPeople: TextView = view.findViewById(R.id.tvRoomNumberOfPeople)
        var roomAge: TextView = view.findViewById(R.id.tvAge)
        var joinCheckImageView: ImageView = view.findViewById(R.id.joinCheckImageView)


    }

    fun joinRoomCheckMethod(mainData: MainFragmentItemData, addressParse: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)
        server.joinRoomCheck(mainData.roomId!!, MainActivity.loginUserNickname, mainData.hostName!!)
            .enqueue(object : Callback<JoinRoomCheck> {
                override fun onResponse(
                    call: Call<JoinRoomCheck>,
                    response: Response<JoinRoomCheck>
                ) {
                    val joinRoomCheck = response.body()
                    if (joinRoomCheck!!.isRoom){
                        if (joinRoomCheck!!.success) {
                            val intent = Intent(context, RoomInformationActivity::class.java)
                            intent.putExtra("roomId", mainData.roomId)
                            intent.putExtra("title", mainData.title)
                            intent.putExtra("info", mainData.info)
                            intent.putExtra("hostName", mainData.hostName)
                            intent.putExtra("address", addressParse)
                            intent.putExtra("date", mainData.date)
                            intent.putExtra("shopName", mainData.shopName)
                            intent.putExtra("roomStatus", mainData.roomStatus)
                            intent.putExtra("numOfPeople", mainData.numOfPeople)
                            intent.putExtra("keyWords", mainData.keyWords)
                            intent.putExtra("nowNumOfPeople", mainData.nowNumOfPeople)
                            intent.putExtra("mapX", mainData.mapX)
                            intent.putExtra("mapY", mainData.mapY)
                            intent.putExtra("roomGender", mainData.gender)
                            intent.putExtra("minimumAge", mainData.minimumAge)
                            intent.putExtra("maximumAge", mainData.maximumAge)
                            intent.putExtra("imageUrl", joinRoomCheck.imageUrl)
                            intent.putExtra("finish",mainData.finish)
                            intent.putExtra("join", "0")
                            context.startActivity(intent)
                        } else {
                            val intent = Intent(context, RoomInformationActivity::class.java)
                            intent.putExtra("roomId", mainData.roomId)
                            intent.putExtra("title", mainData.title)
                            intent.putExtra("info", mainData.info)
                            intent.putExtra("hostName", mainData.hostName)
                            intent.putExtra("address", addressParse)
                            intent.putExtra("date", mainData.date)
                            intent.putExtra("shopName", mainData.shopName)
                            intent.putExtra("roomStatus", mainData.roomStatus)
                            intent.putExtra("numOfPeople", mainData.numOfPeople)
                            intent.putExtra("keyWords", mainData.keyWords)
                            intent.putExtra("mapX", mainData.mapX)
                            intent.putExtra("mapY", mainData.mapY)
                            intent.putExtra("nowNumOfPeople", mainData.nowNumOfPeople)
                            intent.putExtra("minimumAge", mainData.minimumAge)
                            intent.putExtra("maximumAge", mainData.maximumAge)
                            intent.putExtra("roomGender", mainData.gender)
                            intent.putExtra("imageUrl", joinRoomCheck.imageUrl)
                            intent.putExtra("finish",mainData.finish)
                            intent.putExtra("join", "1")
                            context.startActivity(intent)

                        }
                    }else{
                        val dialog = AlertDialog.Builder(context)
                        dialog.setMessage("해당방을 찾을수 없습니다.")
                            .setPositiveButton("확인"
                            ) { dialog, which ->

                            }.setCancelable(false).create().show()
                    }}

                override fun onFailure(call: Call<JoinRoomCheck>, t: Throwable) {

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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterCheck = constraint.toString()
                if (filterCheck == "null") {
                    Log.e("어댑터필터", "안함")
                    filList = unList

                } else {
                    Log.e("어댑터필터", "${filterCheck}동작")
                    filList = ArrayList<MainFragmentItemData>()
                    for (index in unList) {

                        if (index.roomStatus!! >= 0.0) {
                            filList.add(index)
                        }

                    }

                    filList
                }
                val filterResult = FilterResults()
                filterResult.values = filList
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filList = results?.values as ArrayList<MainFragmentItemData>
                notifyDataSetChanged()

//                if (filList.size == 0) {
//                    mMainFragment.swipeRefresh.visibility = View.GONE
//                    mMainFragment.refreshTextView.visibility = View.VISIBLE
//                } else {
//                    mMainFragment.swipeRefresh.visibility = View.VISIBLE
//                    mMainFragment.refreshTextView.visibility = View.GONE
//
//                }
            }
        }
    }

}