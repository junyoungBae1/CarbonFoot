package com.example.ptype1

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyPageActivity : AppCompatActivity() {

    lateinit var logoutBtn : TextView
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        logoutBtn=findViewById(R.id.logoutBtn)
        val handler = android.os.Handler()
        // 버튼 및 핸들러 사전 초기화
        val defaultColor = Color.parseColor("#4CAF50") //버튼 누르면 글짜 색 바뀌게끔


        retrofit = Retrofit.Builder() //retrofit 정의
            .baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService=retrofit.create(ApiService::class.java)
        //Retrofit 정의

        // token을 호출하고 token이 유효하면 userData 가져오기
        val jwt_token=MyApp.prefs.getString("jwt_token",null)

        if (jwt_token!=null){
            val userName= MyApp.prefs.getString("userName",null)
            val userEmail=MyApp.prefs.getString("userEmail",null)
            val userPhone=MyApp.prefs.getString("userPhone",null)
            //val userPw=intent.getStringExtra("userpassword")

            val MyPageUser=findViewById<TextView>(R.id.nameMyPage)
            val MyPageEmail=findViewById<TextView>(R.id.idMyPage)
            val MyPagePhone=findViewById<TextView>(R.id.phoneMyPage)

            MyPageUser.text=userName
            MyPageEmail.text=userEmail
            MyPagePhone.text=userPhone
        }

        //여기까지 userDataSet가져오기


        logoutBtn.setOnClickListener {//등록 버튼 동작

            var newColor= Color.parseColor("#2C9430")
            logoutBtn.setTextColor(newColor)

            handler.postDelayed({
                logoutBtn.setTextColor(defaultColor)
            }, 300) //버튼 누르면 글 색 바뀜

            //로그아웃 하면 token 반환하도록 하는
            val builder=AlertDialog.Builder(this)
            builder.setTitle("alarm").setMessage("로그아웃 하시겠습니까?").
                    setPositiveButton("YES",DialogInterface.OnClickListener{ dialogInterface: DialogInterface, i: Int ->
                        val server=apiService.PostLogoutRequest("Bearer $jwt_token")

                        server.enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    // 응답 성공 처리
                                    MyApp.prefs.removeString("jwt_token")
                                    MyApp.prefs.removeString("userName")
                                    MyApp.prefs.removeString("userEmail")
                                    MyApp.prefs.removeString("userPhone")

                                    Toast.makeText(this@MyPageActivity,"로그아웃 되었습니다",Toast.LENGTH_LONG).show()
                                    val intent= Intent(this@MyPageActivity,MainActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    // 서버 응답 오류 처리

                                    Toast.makeText(this@MyPageActivity,"로그인 세션 만료.",Toast.LENGTH_LONG).show()
                                    val intent=Intent(this@MyPageActivity,MainActivity::class.java)
                                    startActivity(intent)

                                    val errorBody = response.errorBody()?.string()
                                    Log.d("whyloginerror","$errorBody")
                                    Log.d("errorCode","$response.code()")
                                    Log.d("tokken?",jwt_token)
                                }
                            }
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                // 네트워크 오류 처리
                                Toast.makeText(this@MyPageActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                            }
                        })

                    }).setNegativeButton("NO",DialogInterface.OnClickListener { dialogInterface, i ->  })

            builder.create()
            builder.show()
        }

        //여기까지 logout

    }
}