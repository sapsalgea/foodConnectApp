package com.example.abled_food_connect.fragments

import android.R.menu
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.ActionAlarmActivity
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.RankingExplanationActivity
import com.example.abled_food_connect.adapter.RankingFragmentRvAdapter
import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RankingFragment:Fragment() {

    var rankingFragmentRvDataArrayList = ArrayList<RankingFragmentRvDataItem>()
    lateinit var rankingFragmentRv : RecyclerView

    //내 랭킹정보를 보여주는 리니어 레이아웃
    lateinit var myRankingInfoLinearLayout : LinearLayout

    //정보가 없을 경우 보여주는 텍스트뷰
    lateinit var noListAlertTv : TextView

    lateinit var filter_item :MenuItem

    var what_tier = "전체"

    companion object{
        const val TAG : String = "랭킹 프래그먼트 로그"
        fun newInstance(): RankingFragment{
            return RankingFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"랭킹프래그먼트 onCreate()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"랭킹프래그먼트 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.ranking_fragments, container, false)

        noListAlertTv = view.findViewById(R.id.noListAlertTv)

        rankingFragmentRv = view.findViewById(R.id.rankingFragmentRv)


        rankingFragmentRv.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        rankingFragmentRv.setHasFixedSize(false)


        if(MainActivity.ranking_explanation_check == 0){
            val intent = Intent(getActivity(), RankingExplanationActivity::class.java)
            startActivity(intent)
        }


        // 구분선 넣기
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), LinearLayoutManager(requireContext()).orientation)

        rankingFragmentRv.addItemDecoration(dividerItemDecoration)

        //처음 접속시 상위 50개를 불러온다.
        seasonPointListLoading()


        //내 랭킹 정보를 보여주는 리니어 레이아웃
        //myRankingInfoLinearLayout = view.findViewById(R.id.myRankingInfoLinearLayout)



        //스크롤 페이징
        // 스크롤 리스너
        rankingFragmentRv.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)




                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1


                Log.d("보는 위치", "${lastVisibleItemPosition}")
                // 스크롤이 일정위치까지 올라가면 올리면 페이징 데이터를 가져온다.
                if (!rankingFragmentRv.canScrollVertically(-1)) {

                }



                // 스크롤이 끝에 도달했는지 확인
                if (!rankingFragmentRv.canScrollVertically(1)) {
                    Log.d("TAG", "리사이클러뷰 최하단")
                    pagingDownLoading(what_tier)



                }else{

                }



//                // 스크롤이 끝에 도달했는지 확인
//                if (!rankingFragmentRv.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount) {
//                    Log.d("TAG", "리사이클러뷰 최하단")
//                    pagingDownLoading()
//
//
//
//                }else{
//
//                }
            }
        })



        //메뉴바 나오게한다.
        setHasOptionsMenu(true)



        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.ranking_fragment_menu, menu)

        filter_item = menu.findItem(R.id.filter_btn)




//        var search_item: MenuItem? = menu?.findItem(R.id.Item5)
//        var search_view: SearchView = search_item?.actionView as SearchView
//
//        search_view.queryHint = "닉네임을 입력해주세요."
//
//
//
//
//
////        search_view.setOnCloseListener({
////            on
////
////        })
//
//        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//
//
//            // 입력 완료됬을때
//            override fun onQueryTextSubmit(query: String?): Boolean {
//
//                if (query != null) {
//                    NicNameSearchListLoading(query)
//                }else{
//                    Toast.makeText(requireContext(), "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
//                }
//                //Toast.makeText(requireContext(), query, Toast.LENGTH_SHORT).show()
//                //tv1.text = query
//                // true하면 키보드가 안내려감
//                return false
//            }
//
//            // 입력할때마다 반응하는
//            override fun onQueryTextChange(newText: String?): Boolean {
//                //tv2.text = newText
//                // true하면 키보드가 안내려감 근데 입력 도중이라 그다지 상관은 없음
//                return false
//            }
//
//        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {


            R.id.ranking_explanation_btn -> {
                activity.let {
                    val intent = Intent(context, RankingExplanationActivity::class.java)
                    startActivity(intent)
                }
            }


            R.id.Item0 ->{
                seasonPointListLoading()
                what_tier = "전체"
                Toast.makeText(requireContext(), "전체", Toast.LENGTH_SHORT).show()
            }
            R.id.Item1 ->{
                what_tier = "챌린저"
                tierSearchLoading("챌린저")
                Toast.makeText(requireContext(), "챌린저", Toast.LENGTH_SHORT).show()

            }

            R.id.Item2 -> {
                what_tier = "마스터"
                tierSearchLoading("마스터")
                Toast.makeText(requireContext(), "마스터", Toast.LENGTH_SHORT).show()

            }
            R.id.Item3 ->{
                what_tier = "다이아몬드"
                tierSearchLoading("다이아몬드")
                Toast.makeText(requireContext(), "다이아몬드", Toast.LENGTH_SHORT).show()

            }
            R.id.Item4 ->{
                what_tier = "플래티넘"
                tierSearchLoading("플래티넘")
                Toast.makeText(requireContext(), "플래티넘", Toast.LENGTH_SHORT).show()

            }
            R.id.Item5 -> {
                what_tier = "골드"
                tierSearchLoading("골드")
                Toast.makeText(requireContext(), "골드", Toast.LENGTH_SHORT).show()

            }
            R.id.Item6 ->{
                what_tier = "실버"
                tierSearchLoading("실버")
                Toast.makeText(requireContext(), "실버", Toast.LENGTH_SHORT).show()

            }
            R.id.Item7 ->{
                what_tier = "브론즈"
                tierSearchLoading("브론즈")
                Toast.makeText(requireContext(), "브론즈", Toast.LENGTH_SHORT).show()

            }
            R.id.Item8 ->{
                what_tier = "스톤"
                tierSearchLoading("스톤")
                Toast.makeText(requireContext(), "스톤", Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }




    fun seasonPointListLoading(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.rankingFragmentRvSeasonPointListGetInterface::class.java)

        //내역을 가져온다
        val dm_message_list_get = api.season_point_list_get(MainActivity.loginUserNickname)


        dm_message_list_get.enqueue(object : Callback<RankingFragmentRvData> {
            override fun onResponse(
                call: Call<RankingFragmentRvData>,
                response: Response<RankingFragmentRvData>
            ) {
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.body().toString()}")

                var items : RankingFragmentRvData? =  response.body()




                if(!rankingFragmentRvDataArrayList.isEmpty()){
                    Log.d("RankingFragment.TAG", "실행됨")
                    rankingFragmentRvDataArrayList.clear()
                }


                rankingFragmentRvDataArrayList = items!!.rankingList as ArrayList<RankingFragmentRvDataItem>
                rankingFragmentRvDataArrayList.add(0,RankingFragmentRvDataItem(0,"",0,"유저","","포인트","순위",0,"티어","",1))


                Log.d("목록나와라", rankingFragmentRvDataArrayList.toString())


                val mAdapter =  RankingFragmentRvAdapter(rankingFragmentRvDataArrayList)
                rankingFragmentRv.adapter = mAdapter





            }

            override fun onFailure(call: Call<RankingFragmentRvData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }







    fun pagingDownLoading(what_tier: String){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.rankingRvPagingToDownInterface::class.java)

        //내역을 가져온다
        val dm_message_list_get = api.paging_to_down_get(rankingFragmentRvDataArrayList.get(rankingFragmentRvDataArrayList.size-1).number,what_tier)


        dm_message_list_get.enqueue(object : Callback<RankingFragmentRvData> {
            override fun onResponse(
                call: Call<RankingFragmentRvData>,
                response: Response<RankingFragmentRvData>
            ) {
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.body().toString()}")

                var items : RankingFragmentRvData? =  response.body()




                var pageingData = items!!.rankingList as ArrayList<RankingFragmentRvDataItem>

                var lastArraylistsize =  rankingFragmentRvDataArrayList.size

                var insetCount = 0

                if(pageingData != null && pageingData.size>0){

                    for(i : Int in 0..pageingData.size-1){


                        rankingFragmentRvDataArrayList.add(pageingData.get(i))

                        insetCount++
                    }
                }

                requireActivity().runOnUiThread({
                    rankingFragmentRv.adapter?.notifyItemRangeInserted(lastArraylistsize, lastArraylistsize+insetCount)

                })




            }

            override fun onFailure(call: Call<RankingFragmentRvData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }




    fun tierSearchLoading(what_tier : String){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.rankingFragmentRvSeasonTierListGetInterface::class.java)

        //내역을 가져온다
        val dm_message_list_get = api.season_tier_list_get(what_tier)
        Log.d("뭐가져가냐", what_tier)


        dm_message_list_get.enqueue(object : Callback<RankingFragmentRvData> {
            override fun onResponse(
                call: Call<RankingFragmentRvData>,
                response: Response<RankingFragmentRvData>
            ) {
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.body().toString()}")

                var items : RankingFragmentRvData? =  response.body()




                if(!rankingFragmentRvDataArrayList.isEmpty()){
                    Log.d("RankingFragment.TAG", "실행됨")
                    rankingFragmentRvDataArrayList.clear()
                }


                rankingFragmentRvDataArrayList = items!!.rankingList as ArrayList<RankingFragmentRvDataItem>

                if(rankingFragmentRvDataArrayList.size>0){
                    noListAlertTv.visibility = View.GONE
                }else{
                    noListAlertTv.visibility = View.VISIBLE
                }
                rankingFragmentRvDataArrayList.add(0,RankingFragmentRvDataItem(0,"",0,"유저","","포인트","순위",0,"티어","",1))


                Log.d("목록나와라", rankingFragmentRvDataArrayList.toString())


                val mAdapter =  RankingFragmentRvAdapter(rankingFragmentRvDataArrayList)
                rankingFragmentRv.adapter = mAdapter





            }

            override fun onFailure(call: Call<RankingFragmentRvData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }




//    fun NicNameSearchListLoading(search_nicname : String){
//        val retrofit = Retrofit.Builder()
//            .baseUrl(getString(R.string.http_request_base_url))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val api = retrofit.create(API.rankingNicNameSearchInterface::class.java)
//
//        //내역을 가져온다
//        val dm_message_list_get = api.anking_nicname_search_get(search_nicname)
//
//
//        dm_message_list_get.enqueue(object : Callback<RankingFragmentRvData> {
//            override fun onResponse(
//                call: Call<RankingFragmentRvData>,
//                response: Response<RankingFragmentRvData>
//            ) {
//                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.raw()}")
//                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.body().toString()}")
//
//                var items : RankingFragmentRvData? =  response.body()
//
//
//
//
//                if(!rankingFragmentRvDataArrayList.isEmpty()){
//                    Log.d("RankingFragment.TAG", "실행됨")
//                    rankingFragmentRvDataArrayList.clear()
//                }
//
//
//                rankingFragmentRvDataArrayList = items!!.rankingList as ArrayList<RankingFragmentRvDataItem>
//                rankingFragmentRvDataArrayList.add(0,RankingFragmentRvDataItem(0,"",0,"유저","","포인트","순위",0,"티어","",1))
//
//
//                Log.d("목록나와라", rankingFragmentRvDataArrayList.toString())
//
//
//                val mAdapter =  RankingFragmentRvAdapter(rankingFragmentRvDataArrayList)
//                rankingFragmentRv.adapter = mAdapter
//
//
//
//
//
//            }
//
//            override fun onFailure(call: Call<RankingFragmentRvData>, t: Throwable) {
//                Log.d(ReviewFragment.TAG, "실패 : $t")
//            }
//        })
//    }



}