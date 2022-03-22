package kr.manna.projects.activity

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.manna.projects.adapter.ListScrollAdapter
import kr.manna.projects.databinding.ActivityListScrollBinding
import kr.manna.projects.entity.Item
import kr.manna.projects.entity.ItemList
import kr.manna.projects.service.RetrofitService
import mannaPlanet.hermes.commonActivity.MAppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListScrollActivity : MAppCompatActivity() {
    lateinit var listScrollAdapter : ListScrollAdapter
    private var page = 1
    private var status = false

    private val binding by viewBinding(ActivityListScrollBinding::inflate)
    // TODO 새로고침 버튼
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        listScrollAdapter = ListScrollAdapter(this)
        binding.rvItem.adapter = listScrollAdapter

        getItemList(page++)

        binding.rvItem.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                //TODO !! try catch 로
                // TODO 통신시 아이템갯수 확인해서 끝인지 확인 , 기존 status 체크를 fun getItemList 에서
                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)?.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                // 스크롤이 끝에 도달했는지 확인
                if (!binding.rvItem.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && !status) {
                    getItemList(page++)
                }
            }

        })
    }

    private fun getItemList(page :Int) {
        // TODO 상품 가격 콤마 추가
        status = true
        val retrofit = Retrofit.Builder().baseUrl("http://211.171.200.80:9999/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(RetrofitService::class.java)
        Log.d("ABCDE", "onResponse getItemList_Page : $page")

        service.getItemList(page).enqueue(object :
            Callback<Item> {
            override fun onResponse(call: Call<Item>, response: Response<Item>) {
                if (listScrollAdapter.data.size > 0) {

                    listScrollAdapter.deleteLoading(listScrollAdapter.data.size -1)
                    Log.d("ABCDE", "onResponse deleteLoading : ${listScrollAdapter.data.size}")
                }
                    // TODO outcode 처리 리스트 비었을때 처리
                if(response.isSuccessful){
                    // 정상적으로 통신이 성고된 경우
                    val result : Item? = response.body()
                    Log.d("ABCDE", "onResponse result :$page , $result")
                    listScrollAdapter.data.addAll(result?.outData!!)
                    listScrollAdapter.data.add(
                        ItemList(
                            no = 0,
                            name = "loading",
                            price = 1000,
                            img = "none",
                            positionLoading = true
                        )
                    )
                }else{
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("ABCDE", "onResponse 실패$response")

                }

                listScrollAdapter.notifyDataSetChanged()
                status = false
            }
            override fun onFailure(call: Call<Item>, t: Throwable) {
                // 통신 실패 (인터넷 끊, 예외 발생 등 시스템적인 이유)
                Log.d("LIST", "onFailure 에러: " + t.message.toString());
                if (listScrollAdapter.data.size > 0) {

                    listScrollAdapter.deleteLoading(listScrollAdapter.data.size -1)
                    Log.d("ABCDE", "onResponse deleteLoading : ${listScrollAdapter.data.size}")
                }
                listScrollAdapter.notifyDataSetChanged()
                status = false
            }
        })

    }

}

