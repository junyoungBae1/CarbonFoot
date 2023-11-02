package com.example.ptype1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ComWriteActivity:AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService
    private lateinit var noticeRegBtn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_textwrite)
        getSupportActionBar()?.setTitle("게시글 등록")

        val userName= MyApp.prefs.getString("userName",null)
        val userEmail= MyApp.prefs.getString("userEmail",null)

        var editTitle=findViewById<TextView>(R.id.writeEditTitle)
        var editContent=findViewById<TextView>(R.id.writeEditContent)

        var regEditTitle : String
        var regEditContent : String

        val content=intent.getStringExtra("UpdateTrigger")
        val noticeToken=intent.getStringExtra("UpdateToken")
            //그냥 글인지 수정 글인지



        retrofit = Retrofit.Builder().baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        apiService=retrofit.create(ApiService::class.java)

        noticeRegBtn=findViewById(R.id.writeRegBtn)
        if(content=="update"){
            noticeRegBtn.text="재등록"
        } //수정 글일 때

        noticeRegBtn.setOnClickListener {

            regEditTitle=editTitle.text.toString()
            regEditContent=editContent.text.toString()

            Log.d("regEditTitle",regEditTitle)
            Log.d("regEditContent",regEditContent)
            Log.d("Emaillis",userEmail)

            if(content==null){
                val server=apiService.postCreate(regEditTitle,regEditContent,userName,userEmail)//userName*?/"
                server.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.isSuccessful) {
                            Toast.makeText(this@ComWriteActivity, "저장 성공!", Toast.LENGTH_LONG).show()

                            val intent= Intent(this@ComWriteActivity,CommunityActivity::class.java)
                            finish()
                            startActivity(intent)
                        }else{
                            val errorBody = response.errorBody()?.string()
                            Log.d("whyloginerror","$errorBody")
                            Log.d("ewrerrorCode","$response.code()")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@ComWriteActivity,"unknownError", Toast.LENGTH_LONG).show()
                        Log.d("NetworkError", "네트워크 오류: ${t.message}")
                    }

                })
            } //여기까지 그냥 글 작성
            else if(content=="update"){
                val server=
                    noticeToken?.let { it1 ->
                        apiService.postUpdate(regEditTitle,regEditContent, it1)
                    }
                server!!.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.isSuccessful) {
                            Toast.makeText(this@ComWriteActivity, "저장 성공!", Toast.LENGTH_LONG).show()

                            val intent= Intent(this@ComWriteActivity,CommunityActivity::class.java)
                            finish()
                            startActivity(intent)
                        }else{
                            val errorBody = response.errorBody()?.string()
                            Log.d("whyloginerror","$errorBody")
                            Log.d("ewrerrorCode","$response.code()")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@ComWriteActivity,"unknownError", Toast.LENGTH_LONG).show()
                        Log.d("NetworkError", "네트워크 오류: ${t.message}")
                    }

                })

            }




        }



    }

    override fun onKeyDown(keyCode:Int, event: KeyEvent?):Boolean{
        if(keyCode== KeyEvent.KEYCODE_BACK){
            finish() // 현재 액티비티를 종료 (선택사항)
            return true
            }

        return super.onKeyDown(keyCode, event)
    }

}