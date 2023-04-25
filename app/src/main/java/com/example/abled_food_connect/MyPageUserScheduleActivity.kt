package com.example.abled_food_connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.adapter.MyScheduleCalendarRvAdapter
import com.example.abled_food_connect.adapter.MyScheduleTodayScheduleListRvAdapter
import com.example.abled_food_connect.data.MyPageUserScheduleData
import com.example.abled_food_connect.data.MyPageUserScheduleDataItem
import com.example.abled_food_connect.data.MyScheduleCalendarData
import com.example.abled_food_connect.databinding.ActivityMyPageUserScheduleBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

class MyPageUserScheduleActivity : AppCompatActivity() {

    var scheduleArrayList = ArrayList<MyPageUserScheduleDataItem>()

    var todayClickScheduleArrayList = ArrayList<MyPageUserScheduleDataItem>()


    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityMyPageUserScheduleBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    //달력리사이클러뷰

    lateinit var calendarRv : RecyclerView

    var caldateList = ArrayList<MyScheduleCalendarData>()



    //시간에 사용하는 변수
    val todaycal = Calendar.getInstance()
    var year = todaycal.get(Calendar.YEAR)
    var month = (todaycal.get(Calendar.MONTH))
    var day = todaycal.get(Calendar.DATE)

    //오늘날짜를 저장하는 변수

    var today_year = todaycal.get(Calendar.YEAR)
    var today_month = (todaycal.get(Calendar.MONTH))
    var today_day = todaycal.get(Calendar.DATE)


    val cal = Calendar.getInstance()




    //날짜를 클릭했을때 나타나는 정보창 리사이클러뷰

    lateinit var todayScheduleListRv : RecyclerView



    var isFisrtOpen = 0
    var selectPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_user_schedule)


        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityMyPageUserScheduleBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.





        setSupportActionBar(binding.userScheduleToolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.userScheduleToolbar.title = "일정"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)






        calendarRv = binding.calendarRv

        todayScheduleListRv =  binding.todayScheduleListRv

        //최초 생성시 오늘 연/월이 출력된다.
        binding.calendarYearAndMonthTv.setText("${year}년 ${month+1}월")
        Log.d("오늘의 날짜", "$year-$month-$day")


        calendarRv.layoutManager = GridLayoutManager(this,7)
        calendarRv.setHasFixedSize(true)


        todayScheduleListRv.layoutManager = LinearLayoutManager(this)
        todayScheduleListRv.setHasFixedSize(true)



        binding.beforeMonthBtn.setOnClickListener(View.OnClickListener {

            month = month - 1
            if(month <0){
                month = 11
                year = year -1
            }


            binding.calendarYearAndMonthTv.setText("${year}년 ${month+1}월")


            cal.set(year,(month),1);

            var maxday = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            Log.d("마지막날", "$maxday")

            var dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            Log.d("요일", "$dayOfWeek")


            caldateList.clear()
            for(i in 1..dayOfWeek-1){

                caldateList.add(MyScheduleCalendarData(year,month,0,-1,-1))
            }

            for(i in 1..maxday){


                var dateStr = "${year}-${String.format("%02d", month+1)}-${String.format("%02d", i)}"



                var checkNum = 0
                var meeting_result = 1



                Log.d("TAG", dateStr)
                //Log.d("TAG", scheduleArrayList.get(k).appointment_day)
                for(k in 0..scheduleArrayList.size-1){
                    if(dateStr ==  scheduleArrayList.get(k).appointment_day){
                        Log.d("TAG", "같다")
                        Log.d("TAG", "dateStr")
                        Log.d("TAG", "scheduleArrayList.get(k).appointment_day")
                        if(scheduleArrayList.get(k).meeting_result == 0){
                            meeting_result =0
                        }
                        if(scheduleArrayList.get(k).meeting_result == 2){
                            meeting_result =2
                        }
                        checkNum = 1
                        break
                    }

                }


                var evaluation_is_required = 1

                for(k in 0..scheduleArrayList.size-1){
                    if(dateStr ==  scheduleArrayList.get(k).appointment_day){

                        Log.d("TAG", "scheduleArrayList.get(k).appointment_day")
                        if(meeting_result == 1 && (scheduleArrayList.get(k).review_result == 0 || scheduleArrayList.get(k).user_evaluation == 0) ){
                            evaluation_is_required =0
                            Log.d("evaluation_is_required", evaluation_is_required.toString())
                            break
                        }
                    }

                }

                if(checkNum == 1){
                    if(meeting_result == 0){
                        caldateList.add(MyScheduleCalendarData(year,month,i,0,-1))
                    }else if(meeting_result == 1 || meeting_result == 2){

                        caldateList.add(MyScheduleCalendarData(year,month,i,1,evaluation_is_required))
                    }
                }

                if(checkNum ==0){
                    caldateList.add(MyScheduleCalendarData(year,month,i,-1,-1))
                }

                if(checkNum ==1){
                    checkNum = 0
                }

            }

            for(i in caldateList.size-1 .. 40){
                caldateList.add(MyScheduleCalendarData(year,month,0,-1,-1))
            }



            var mAdapter =  MyScheduleCalendarRvAdapter(caldateList)
            mAdapter.setItemClickListener(object: MyScheduleCalendarRvAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {


                    selectPosition = position

                    // 클릭 시 이벤트 작성
                    if(caldateList.get(position).rvDay != 0){



                        var dateStr = "${year}-${String.format("%02d", caldateList.get(position).rvMonth+1)}-${String.format("%02d", caldateList.get(position).rvDay)}"

                        todayClickScheduleArrayList.clear()
                        for(k in 0..scheduleArrayList.size-1){
                            if(scheduleArrayList.get(k).appointment_day == dateStr){

                                todayClickScheduleArrayList.add(scheduleArrayList.get(k))

                            }
                        }

                        var TodayScheduleListAdapter =  MyScheduleTodayScheduleListRvAdapter(todayClickScheduleArrayList)
                        TodayScheduleListAdapter.notifyDataSetChanged()
                        todayScheduleListRv.adapter = TodayScheduleListAdapter

                        Log.d("TAG", "오늘"+todayClickScheduleArrayList.toString())

//                                //리사이클러뷰 구분선
//                                val dividerItemDecoration =
//                                    DividerItemDecoration(todayScheduleListRv.context, LinearLayoutManager(applicationContext).orientation)
//                                todayScheduleListRv.addItemDecoration(dividerItemDecoration)

                        if(todayClickScheduleArrayList.size>0){
                            binding.noScheduleTv.visibility = View.GONE
                        }else{
                            binding.noScheduleTv.visibility = View.VISIBLE
                        }

                    }

                }
            })
            mAdapter.notifyDataSetChanged()
            calendarRv.adapter = mAdapter



            //오늘날짜 클릭(리사이클러뷰 뷰가 그려지고 나서 수행해야함, 그려지기 전에 클릭이벤트를 실행하면 null값이 나옴)


            var todayposition = 0
            if(today_year ==year && today_month ==month){
                for(a in 0..caldateList.size-1){
                    if(today_day == caldateList.get(a).rvDay){
                        todayposition = a
                        break
                    }
                }
                calendarRv.viewTreeObserver.addOnGlobalLayoutListener(
                    object: ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            val view = calendarRv.findViewHolderForAdapterPosition(todayposition)!!.itemView?.findViewById<TextView>(R.id.rvDayTv)
                            view!!.performClick()
                            calendarRv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    }
                )
            }


        })



        binding.nextMonthBtn.setOnClickListener(View.OnClickListener {

            month = month + 1
            if(month >11){
                month = 0
                year = year +1
            }


            binding.calendarYearAndMonthTv.setText("${year}년 ${month+1}월")


            cal.set(year,(month),1)

            var maxday = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            Log.d("마지막날", "$maxday")

            var dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            Log.d("요일", "$dayOfWeek")


            caldateList.clear()
            for(i in 1..dayOfWeek-1){

                caldateList.add(MyScheduleCalendarData(year,month,0,-1,-1))
            }

            for(i in 1..maxday){


                var dateStr = "${year}-${String.format("%02d", month+1)}-${String.format("%02d", i)}"



                var checkNum = 0
                var meeting_result = 1


                Log.d("TAG", dateStr)
                //Log.d("TAG", scheduleArrayList.get(k).appointment_day)
                for(k in 0..scheduleArrayList.size-1){
                    if(dateStr ==  scheduleArrayList.get(k).appointment_day){
                        Log.d("TAG", "같다")
                        Log.d("TAG", "dateStr")
                        Log.d("TAG", "scheduleArrayList.get(k).appointment_day")
                        if(scheduleArrayList.get(k).meeting_result == 0){
                            meeting_result =0
                        }
                        if(scheduleArrayList.get(k).meeting_result == 2){
                            meeting_result =2
                        }
                        checkNum = 1
                        break
                    }

                }


                var evaluation_is_required = 1

                for(k in 0..scheduleArrayList.size-1){
                    if(dateStr ==  scheduleArrayList.get(k).appointment_day){

                        Log.d("TAG", "scheduleArrayList.get(k).appointment_day")
                        if(meeting_result == 1 && (scheduleArrayList.get(k).review_result == 0 || scheduleArrayList.get(k).user_evaluation == 0) ){
                            evaluation_is_required =0
                            Log.d("evaluation_is_required", evaluation_is_required.toString())
                            break
                        }
                    }

                }

                if(checkNum == 1){
                    if(meeting_result == 0){
                        caldateList.add(MyScheduleCalendarData(year,month,i,0,-1))
                    }else if(meeting_result == 1 || meeting_result == 2){
                        caldateList.add(MyScheduleCalendarData(year,month,i,1,evaluation_is_required))
                    }
                }

                if(checkNum ==0){
                    caldateList.add(MyScheduleCalendarData(year,month,i,-1,-1))
                }

                if(checkNum ==1){
                    checkNum = 0
                }

            }

            for(i in caldateList.size-1 .. 40){
                caldateList.add(MyScheduleCalendarData(year,month,0,-1,-1))
            }



            var mAdapter =  MyScheduleCalendarRvAdapter(caldateList)
            mAdapter.notifyDataSetChanged()
            mAdapter.setItemClickListener(object: MyScheduleCalendarRvAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {


                    selectPosition = position


                    // 클릭 시 이벤트 작성
                    if(caldateList.get(position).rvDay != 0){



                        var dateStr = "${year}-${String.format("%02d", caldateList.get(position).rvMonth+1)}-${String.format("%02d", caldateList.get(position).rvDay)}"

                        todayClickScheduleArrayList.clear()
                        for(k in 0..scheduleArrayList.size-1){
                            if(scheduleArrayList.get(k).appointment_day == dateStr){

                                todayClickScheduleArrayList.add(scheduleArrayList.get(k))

                            }
                        }

                        var TodayScheduleListAdapter =  MyScheduleTodayScheduleListRvAdapter(todayClickScheduleArrayList)
                        TodayScheduleListAdapter.notifyDataSetChanged()
                        todayScheduleListRv.adapter = TodayScheduleListAdapter

                        Log.d("TAG", "오늘"+todayClickScheduleArrayList.toString())

//                                //리사이클러뷰 구분선
//                                val dividerItemDecoration =
//                                    DividerItemDecoration(todayScheduleListRv.context, LinearLayoutManager(applicationContext).orientation)
//                                todayScheduleListRv.addItemDecoration(dividerItemDecoration)

                        if(todayClickScheduleArrayList.size>0){
                            binding.noScheduleTv.visibility = View.GONE
                        }else{
                            binding.noScheduleTv.visibility = View.VISIBLE
                        }

                    }



                }


            })
            calendarRv.adapter = mAdapter


            //오늘날짜 클릭(리사이클러뷰 뷰가 그려지고 나서 수행해야함, 그려지기 전에 클릭이벤트를 실행하면 null값이 나옴)


            var todayposition = 0
            if(today_year ==year && today_month ==month){
                for(a in 0..caldateList.size-1){
                    if(today_day == caldateList.get(a).rvDay){
                        todayposition = a
                        break
                    }
                }
                calendarRv.viewTreeObserver.addOnGlobalLayoutListener(
                    object: ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            val view = calendarRv.findViewHolderForAdapterPosition(todayposition)!!.itemView?.findViewById<TextView>(R.id.rvDayTv)
                            view!!.performClick()
                            calendarRv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    }
                )
            }


        })





    }

    override fun onStart() {
        super.onStart()
        MyPageUserScheduleRvGet()






    }


    fun MyPageUserScheduleRvGet(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.MyPageUserScheduleRvInterface::class.java)

        //db에 방이 있는지 확인한다.
        val user_schedule_get = api.my_page_user_schedule_rv_get(MainActivity.loginUserNickname,0)


        user_schedule_get.enqueue(object : Callback<MyPageUserScheduleData> {
            override fun onResponse(
                call: Call<MyPageUserScheduleData>,
                response: Response<MyPageUserScheduleData>
            ) {
                Log.d(ReviewFragment.TAG, "스케쥴 조회결과 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "스케쥴 조회결과 : ${response.body().toString()}")

                var items : MyPageUserScheduleData? =  response.body()


                if (items != null && isFisrtOpen ==0) {
                    scheduleArrayList = items.scheduleList as ArrayList<MyPageUserScheduleDataItem>



                    //달에 -1을 해준다.
                    cal.set(year,(month),1);

                    Log.d("날짜", "$year-$month-$day")

                    var maxday = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    Log.d("마지막날", "$maxday")

                    var dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                    Log.d("요일", "$dayOfWeek")




                    caldateList.clear()
                    for(i in 1..dayOfWeek-1){

                        caldateList.add(MyScheduleCalendarData(year,month,0,-1,-1))
                    }

                    for(i in 1..maxday){


                        var dateStr = "${year}-${String.format("%02d", month+1)}-${String.format("%02d", i)}"



                        var checkNum = 0
                        var meeting_result = 1


                        Log.d("TAG", dateStr)
                        //Log.d("TAG", scheduleArrayList.get(k).appointment_day)
                        for(k in 0..scheduleArrayList.size-1){
                            if(dateStr ==  scheduleArrayList.get(k).appointment_day){
                                Log.d("TAG", "같다")
                                Log.d("TAG", "dateStr")
                                Log.d("TAG", "scheduleArrayList.get(k).appointment_day")
                                if(scheduleArrayList.get(k).meeting_result == 0){
                                    meeting_result =0
                                }
                                if(scheduleArrayList.get(k).meeting_result == 2){
                                    meeting_result =2
                                }
                                checkNum = 1
                                break
                            }

                        }


                        var evaluation_is_required = 1

                        for(k in 0..scheduleArrayList.size-1){
                            if(dateStr ==  scheduleArrayList.get(k).appointment_day){

                                Log.d("TAG", "scheduleArrayList.get(k).appointment_day")
                                if(meeting_result == 1 && (scheduleArrayList.get(k).review_result == 0 || scheduleArrayList.get(k).user_evaluation == 0) ){
                                    evaluation_is_required =0
                                    Log.d("evaluation_is_required", evaluation_is_required.toString())
                                    break
                                }
                            }

                        }



                        if(checkNum == 1){
                            if(meeting_result == 0){
                                caldateList.add(MyScheduleCalendarData(year,month,i,0,-1))
                            }else if(meeting_result == 1 || meeting_result == 2){
                                caldateList.add(MyScheduleCalendarData(year,month,i,1,evaluation_is_required))
                            }
                        }

                        if(checkNum ==0){
                            caldateList.add(MyScheduleCalendarData(year,month,i,-1,-1))
                        }

                        if(checkNum ==1){
                            checkNum = 0
                        }

                    }

                    for(i in caldateList.size-1 .. 40){
                        caldateList.add(MyScheduleCalendarData(year,month,0,-1,-1))
                    }






                    var mAdapter =  MyScheduleCalendarRvAdapter(caldateList)

                    mAdapter.setItemClickListener(object: MyScheduleCalendarRvAdapter.OnItemClickListener{
                        override fun onClick(v: View, position: Int) {

                            selectPosition = position

                            // 클릭 시 이벤트 작성
                            if(caldateList.get(position).rvDay != 0){



                                var dateStr = "${year}-${String.format("%02d", caldateList.get(position).rvMonth+1)}-${String.format("%02d", caldateList.get(position).rvDay)}"

                                todayClickScheduleArrayList.clear()
                                for(k in 0..scheduleArrayList.size-1){
                                    if(scheduleArrayList.get(k).appointment_day == dateStr){

                                        todayClickScheduleArrayList.add(scheduleArrayList.get(k))

                                    }
                                }

                                var TodayScheduleListAdapter =  MyScheduleTodayScheduleListRvAdapter(todayClickScheduleArrayList)
                                TodayScheduleListAdapter.notifyDataSetChanged()
                                todayScheduleListRv.adapter = TodayScheduleListAdapter

                                Log.d("TAG", "오늘"+todayClickScheduleArrayList.toString())

//                                //리사이클러뷰 구분선
//                                val dividerItemDecoration =
//                                    DividerItemDecoration(todayScheduleListRv.context, LinearLayoutManager(applicationContext).orientation)
//                                todayScheduleListRv.addItemDecoration(dividerItemDecoration)

                                if(todayClickScheduleArrayList.size>0){
                                    binding.noScheduleTv.visibility = View.GONE
                                }else{
                                    binding.noScheduleTv.visibility = View.VISIBLE
                                }

                            }

                        }
                    })

                    calendarRv.adapter = mAdapter




                    //오늘날짜 클릭(리사이클러뷰 뷰가 그려지고 나서 수행해야함, 그려지기 전에 클릭이벤트를 실행하면 null값이 나옴)


                    var todayposition = 0
                    if(today_year ==year && today_month ==month){
                        for(a in 0..caldateList.size-1){
                            if(today_day == caldateList.get(a).rvDay){
                                todayposition = a
                                break
                            }
                        }
                        calendarRv.viewTreeObserver.addOnGlobalLayoutListener(
                            object: ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    val view = calendarRv.findViewHolderForAdapterPosition(todayposition)!!.itemView?.findViewById<TextView>(R.id.rvDayTv)
                                    view!!.performClick()
                                    calendarRv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                }
                            }
                        )
                    }






                }




                if(items!=null && isFisrtOpen==1){

                    scheduleArrayList = items.scheduleList as ArrayList<MyPageUserScheduleDataItem>

                    binding.calendarYearAndMonthTv.setText("${year}년 ${month+1}월")


                    cal.set(year,(month),1)

                    var maxday = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    Log.d("마지막날", "$maxday")

                    var dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                    Log.d("요일", "$dayOfWeek")


                    caldateList.clear()
                    for(i in 1..dayOfWeek-1){

                        caldateList.add(MyScheduleCalendarData(year,month,0,-1,-1))
                    }

                    for(i in 1..maxday){


                        var dateStr = "${year}-${String.format("%02d", month+1)}-${String.format("%02d", i)}"



                        var checkNum = 0
                        var meeting_result = 1


                        Log.d("TAG", dateStr)
                        //Log.d("TAG", scheduleArrayList.get(k).appointment_day)
                        for(k in 0..scheduleArrayList.size-1){
                            if(dateStr ==  scheduleArrayList.get(k).appointment_day){
                                Log.d("TAG", "같다")
                                Log.d("TAG", "dateStr")
                                Log.d("TAG", "scheduleArrayList.get(k).appointment_day")
                                if(scheduleArrayList.get(k).meeting_result == 0){
                                    meeting_result =0
                                }
                                if(scheduleArrayList.get(k).meeting_result == 2){
                                    meeting_result =2
                                }
                                checkNum = 1
                                break
                            }

                        }


                        var evaluation_is_required = 1

                        for(k in 0..scheduleArrayList.size-1){
                            if(dateStr ==  scheduleArrayList.get(k).appointment_day){

                                Log.d("TAG", "scheduleArrayList.get(k).appointment_day")
                                if(meeting_result == 1 && (scheduleArrayList.get(k).review_result == 0 || scheduleArrayList.get(k).user_evaluation == 0) ){
                                    evaluation_is_required =0
                                    Log.d("evaluation_is_required", evaluation_is_required.toString())
                                    break
                                }
                            }

                        }

                        if(checkNum == 1){
                            if(meeting_result == 0){
                                caldateList.add(MyScheduleCalendarData(year,month,i,0,-1))
                            }else if(meeting_result == 1 || meeting_result == 2){
                                caldateList.add(MyScheduleCalendarData(year,month,i,1,evaluation_is_required))
                            }
                        }

                        if(checkNum ==0){
                            caldateList.add(MyScheduleCalendarData(year,month,i,-1,-1))
                        }

                        if(checkNum ==1){
                            checkNum = 0
                        }

                    }

                    for(i in caldateList.size-1 .. 40){
                        caldateList.add(MyScheduleCalendarData(year,month,0,-1,-1))
                    }



                    var mAdapter =  MyScheduleCalendarRvAdapter(caldateList)
                    mAdapter.notifyDataSetChanged()
                    mAdapter.setItemClickListener(object: MyScheduleCalendarRvAdapter.OnItemClickListener{
                        override fun onClick(v: View, position: Int) {

                            selectPosition = position
                            // 클릭 시 이벤트 작성
                            if(caldateList.get(position).rvDay != 0){



                                var dateStr = "${year}-${String.format("%02d", caldateList.get(position).rvMonth+1)}-${String.format("%02d", caldateList.get(position).rvDay)}"

                                todayClickScheduleArrayList.clear()
                                for(k in 0..scheduleArrayList.size-1){
                                    if(scheduleArrayList.get(k).appointment_day == dateStr){

                                        todayClickScheduleArrayList.add(scheduleArrayList.get(k))

                                    }
                                }

                                var TodayScheduleListAdapter =  MyScheduleTodayScheduleListRvAdapter(todayClickScheduleArrayList)
                                TodayScheduleListAdapter.notifyDataSetChanged()
                                todayScheduleListRv.adapter = TodayScheduleListAdapter

                                Log.d("TAG", "오늘"+todayClickScheduleArrayList.toString())

//                                //리사이클러뷰 구분선
//                                val dividerItemDecoration =
//                                    DividerItemDecoration(todayScheduleListRv.context, LinearLayoutManager(applicationContext).orientation)
//                                todayScheduleListRv.addItemDecoration(dividerItemDecoration)

                                if(todayClickScheduleArrayList.size>0){
                                    binding.noScheduleTv.visibility = View.GONE
                                }else{
                                    binding.noScheduleTv.visibility = View.VISIBLE
                                }

                            }



                        }


                    })
                    calendarRv.adapter = mAdapter


                    //오늘날짜 클릭(리사이클러뷰 뷰가 그려지고 나서 수행해야함, 그려지기 전에 클릭이벤트를 실행하면 null값이 나옴)



                    calendarRv.viewTreeObserver.addOnGlobalLayoutListener(
                        object: ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                Log.d("누름", "onGlobalLayout: ")
                                val view = calendarRv.findViewHolderForAdapterPosition(selectPosition)!!.itemView?.findViewById<TextView>(R.id.rvDayTv)
                                view!!.performClick()
                                calendarRv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }
                        }
                    )

                }

                isFisrtOpen = 1



            }

            override fun onFailure(call: Call<MyPageUserScheduleData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }

    //액션버튼 메뉴 액션바에 집어 넣기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_page_user_schedule_top_menu, menu)
        return true
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.toMoveMyPageUserScheduleListActivity -> {
                //공유 버튼 눌렀을 때

                var toMoveMyPageUserScheduleListActivityintent : Intent = Intent(applicationContext, MyPageUserScheduleListActivity::class.java)
                startActivity(toMoveMyPageUserScheduleListActivityintent, null)
                return true
            }


            android.R.id.home -> {
                finish()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

}