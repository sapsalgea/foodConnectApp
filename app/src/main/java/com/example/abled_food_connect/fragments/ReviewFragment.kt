package com.example.abled_food_connect.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.data.ReviewFragmentLoadingData
import com.example.abled_food_connect.data.ReviewFragmentLodingDataItem
import com.example.abled_food_connect.adapter.ReviewFragmentGridViewAdapter
import com.example.abled_food_connect.interfaces.ReviewFragRvUsingInterface
import com.example.abled_food_connect.R
import com.example.abled_food_connect.ReviewSearchActivity
import com.example.abled_food_connect.data.MainFragmentItemData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ReviewFragment:Fragment() {


    //리사이클러뷰 어래이리스트
    private var gridView_arrayList = ArrayList<ReviewFragmentLodingDataItem>()

    //리사이클러뷰
    lateinit var rv : RecyclerView


    //그리드뷰 어댑터
   var mAdapter = ReviewFragmentGridViewAdapter()


    companion object{
        const val TAG : String = "리뷰 프래그먼트 로그"
        fun newInstance(): ReviewFragment{
            return ReviewFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"리뷰프래그먼트 onCreate()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"리뷰프래그먼트 onAttach()")
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG,"리뷰프래그먼트 onResume()")
        reviewDbLoading()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.review_fragment, container, false)

        Log.d(TAG,"리뷰프래그먼트 onCreateView()")


        reviewDbLoading()




        rv = view.findViewById<RecyclerView>(R.id.rv)
        //rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = GridLayoutManager(context,3)

        rv.adapter = mAdapter

        //리사이클러뷰 구분선


        rv.addItemDecoration(HorizontalItemDecorator(5))
        rv.addItemDecoration(VerticalItemDecorator(5))


        rv.setHasFixedSize(true)



        //메뉴바 나오게한다.
        setHasOptionsMenu(true)


        return view
    }




    fun reviewDbLoading(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ReviewFragRvUsingInterface::class.java)
        val callGetSearchNews = api.review_frag_rv_using_interface(MainActivity.user_table_id)


        callGetSearchNews.enqueue(object : Callback<ReviewFragmentLoadingData> {
            override fun onResponse(
                call: Call<ReviewFragmentLoadingData>,
                response: Response<ReviewFragmentLoadingData>
            ) {
                Log.d(TAG, "리뷰성공 : ${response.raw()}")
                Log.d(TAG, "리뷰성공 : ${response.body().toString()}")

                var items : ReviewFragmentLoadingData? =  response.body()


                Log.d(TAG, "성공 : ${items!!.roomList}")


                gridView_arrayList.clear()
                gridView_arrayList = items!!.roomList as ArrayList<ReviewFragmentLodingDataItem>

                mAdapter.reviewList = gridView_arrayList
                mAdapter.notifyDataSetChanged()




            }

            override fun onFailure(call: Call<ReviewFragmentLoadingData>, t: Throwable) {
                Log.d(TAG, "실패 : $t")
            }
        })
    }

    class HorizontalItemDecorator(private val divHeight : Int) : RecyclerView.ItemDecoration() {

        @Override
        override fun getItemOffsets(outRect: Rect, view: View, parent : RecyclerView, state : RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.left = divHeight
            outRect.right = divHeight
        }
    }

    class VerticalItemDecorator(private val divHeight : Int) : RecyclerView.ItemDecoration() {

        @Override
        override fun getItemOffsets(outRect: Rect, view: View, parent : RecyclerView, state : RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.top = divHeight
            outRect.bottom = divHeight
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.review_fragment_top_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search_btn) {


            activity?.let{
                val intent = Intent(context, ReviewSearchActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent)

            }

            Toast.makeText(context, "버튼클릭", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }



}