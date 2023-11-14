package com.example.ptype1

 import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Timer
import java.util.TimerTask


class SearchFoodActivity: AppCompatActivity() {

    private val items= mutableListOf<String>()
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService
    lateinit var server : Call<SearchDTO>

    lateinit var SearchFoodView : RecyclerView
    lateinit var SearchEmptyText : TextView
    lateinit var SearchFoodCheckText : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchfood)
        getSupportActionBar()?.setTitle("검색하기")

        retrofit = Retrofit.Builder().baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        apiService=retrofit.create(ApiService::class.java)

        val search_text=findViewById<EditText>(R.id.search_inputtext) //텍스트창
        //val search_btn=findViewById<TextView>(R.id.search_store)


        SearchFoodView= findViewById(R.id.SearchFood_View)
        SearchEmptyText = findViewById(R.id.SearchEmptyView)
        //SearchFoodCheckText =findViewById(R.id.SearchFoodCheckText)


        val rvAdapter=SearchFoodAdapter(baseContext,items)
        SearchFoodView.adapter=rvAdapter
        //rvAdapter.updateData(items)
        SearchFoodView.layoutManager= LinearLayoutManager(this)


        search_text.addTextChangedListener(object : TextWatcher {
            private var debounceTimer: Timer? = null

            override fun afterTextChanged(s: Editable?) {
                debounceTimer?.cancel()
                debounceTimer = Timer()
                debounceTimer?.schedule(object : TimerTask() {
                    override fun run() {
                        EnqueueSearchFood(s.toString())
                    }
                }, 500) // 500ms 동안 추가 입력이 없을 때만 서버에 요청
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 입력하기 전에 호출
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                rvAdapter.ClearData(items)
                // 입력이 변경될 때마다 호출
            }
        })


    }


    fun EnqueueSearchFood(s : String){

        server=apiService.getSearchFoods(s)
        if(s=="")return

        server.enqueue(object : Callback<SearchDTO> {
            override fun onResponse(call: Call<SearchDTO>, response: Response<SearchDTO>) {
                if(response.isSuccessful) {
                    Log.d("Scoreeeeeeeee", response.body().toString())

                    val data = response.body()
                    Log.d("itemmmmmmm is",data.toString())

                    val food_list= data?.foodnames

                    if(food_list!=null){
                        SearchFoodView.visibility = View.VISIBLE
                        SearchEmptyText.visibility = View.GONE

                        for( i in 0 until food_list?.size!!){
                            items.add(food_list.get(i))
                        }

                        val rvAdapter=SearchFoodAdapter(baseContext,items)
                        SearchFoodView.adapter=rvAdapter
                        //rvAdapter.updateData(items)
                        SearchFoodView.layoutManager=LinearLayoutManager(this@SearchFoodActivity)

                    }else{
                        SearchFoodView.visibility = View.GONE
                        SearchEmptyText.visibility = View.VISIBLE
                    }




                }else{
                    val errorBody = response.errorBody()?.string()
                    Log.d("whyloginerror","$errorBody")
                }
            }

            override fun onFailure(call: Call<SearchDTO>, t: Throwable) {
                Toast.makeText(this@SearchFoodActivity,"unknownError", Toast.LENGTH_LONG).show()
                Log.d("NetworkError", "네트워크 오류: ${t.message}")
            }
        })
    }
}