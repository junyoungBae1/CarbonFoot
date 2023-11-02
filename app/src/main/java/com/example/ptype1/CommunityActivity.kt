package com.example.ptype1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class CommunityActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService
    private lateinit var writeBtn : Button

    private val items= mutableListOf<CommunityData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        val email=MyApp.prefs.getString("userEmail",null)
        val token=MyApp.prefs.getString("jwt_token",null)
        val userName=MyApp.prefs.getString("userName",null)

        getSupportActionBar()?.setTitle("자유게시판")//appbar 형성


        writeBtn=findViewById(R.id.comWriteBtn)

        retrofit = Retrofit.Builder() //retrofit 정의
            .baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService=retrofit.create(ApiService::class.java)

        val server=apiService.getRead()

        Log.d("server>>>",server.toString())

        server.enqueue(object : Callback<noticeDTO> {
            override fun onResponse(call: Call<noticeDTO>, response: Response<noticeDTO>) {
                if(response.isSuccessful) {
                    Log.d("responseBodyyyy",response.body().toString())

                    for(i in response.body()?.data!!){
                        items.add(CommunityData(i.noticeToken,i.title,i.content,i.writer,i.userEmail,i.date,i.unknown,i.matchResult))
                    }


                    val recyclerview=findViewById<RecyclerView>(R.id.Community_View)
                    val rvAdapter=CommunityAdapter(baseContext,items.asReversed())
                    recyclerview.adapter=rvAdapter


                    //rvAdapter.updateData(items)
                    recyclerview.layoutManager= LinearLayoutManager(this@CommunityActivity)

                }else{
                    val errorBody = response.errorBody()?.string()
                    Log.d("whyGetBoardError","$errorBody")
                    Log.d("errorCode","$response.code()")
                }
            }

            override fun onFailure(call: Call<noticeDTO>, t: Throwable) {
                var errorMessage = "Unknown Error"
                Toast.makeText(this@CommunityActivity, errorMessage, Toast.LENGTH_LONG).show()
                Log.d("NetworkError", "네트워크 오류: ${t.message}")
            }

        })

        /*val recyclerview=findViewById<RecyclerView>(R.id.Community_View)
        val rvAdapter=RankingAdapter(baseContext,items)
        recyclerview.adapter=rvAdapter
        //rvAdapter.updateData(items)
        recyclerview.layoutManager= LinearLayoutManager(this@CommunityActivity)*/

        writeBtn.setOnClickListener {
            var intent=Intent(this,ComWriteActivity::class.java)
            startActivity(intent)
        }



    }
    override fun onKeyDown(keyCode:Int, event: KeyEvent?):Boolean{
        if(keyCode== KeyEvent.KEYCODE_BACK){
            val intent = Intent(this, MenuActivity::class.java) // TargetActivity로 이동하려는 Intent 생성
            startActivity(intent) // Intent를 시작하여 TargetActivity로 이동
            finish() // 현재 액티비티를 종료하고 이전 액티비티로 돌아감
            return true
        }

        return super.onKeyDown(keyCode, event)
    }
}