package com.example.ptype1

import com.example.ptype1.ApiService
import com.example.ptype1.R

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptype1.checkingDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ComCheckActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService
    private lateinit var modifybtn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_textcheck)

        val checkTitle=findViewById<TextView>(R.id.checkTextTitle)
        val checkContent=findViewById<TextView>(R.id.checkContentText)
        val checkWriter=findViewById<TextView>(R.id.checkTextWriter)

        val content=intent.getStringExtra("content")
        val date=intent.getStringExtra("date")
        val title=intent.getStringExtra("title")
        val writer=intent.getStringExtra("writer")

        val noticeToken= intent.getStringExtra("noticeToken")
        val useremail= intent.getStringExtra("userEmail")

        modifybtn= findViewById(R.id.modifyTextBtn)
        modifybtn.visibility = View.GONE



        checkTitle.text=title
        checkContent.text=content
        checkWriter.text=writer

        retrofit = Retrofit.Builder() //retrofit 정의
            .baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService=retrofit.create(ApiService::class.java)

        if(noticeToken!=null && useremail!=null){
            val server=apiService.postGetBoard(noticeToken,useremail)

            server.enqueue(object : Callback<checkingDTO> {
                override fun onResponse(call: Call<checkingDTO>, response: Response<checkingDTO>) {
                    if(response.isSuccessful) {
                        Log.d("responseBodyyyis", response.body()?.match.toString())


                        if(response.body()?.match==1){

                            modifybtn.visibility = View.VISIBLE
                        }


                    }else{
                        val errorBody = response.errorBody()?.string()
                        Log.d("whyGetBoardError","$errorBody")
                        Log.d("errorCode","$response.code()")
                    }
                }

                override fun onFailure(call: Call<checkingDTO>, t: Throwable) {
                    var errorMessage = "Unknown Error"
                    Toast.makeText(this@ComCheckActivity, errorMessage, Toast.LENGTH_LONG).show()
                    Log.d("NetworkError", "네트워크 오류: ${t.message}")
                }

            })
        }

    }
}