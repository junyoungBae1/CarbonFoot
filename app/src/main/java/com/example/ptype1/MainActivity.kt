package com.example.ptype1


import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    var initTime =0L // 변수


    private lateinit var idEditText : EditText
    private lateinit var passwordEditText: EditText
    private lateinit var btn: Button

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService
    private lateinit var regBtn : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn=findViewById(R.id.cirLoginButton) //로그인버튼
        regBtn=findViewById(R.id.regButton) //등록버튼

        val handler = android.os.Handler()

        retrofit = Retrofit.Builder() //retrofit 정의
            .baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService=retrofit.create(ApiService::class.java)

        btn.setOnClickListener { //로그인 버튼 동작

            //var intent= Intent(this@MainActivity,MenuActivity::class.java)
            //startActivity(intent)

            idEditText=findViewById(R.id.loginEmail)
            passwordEditText=findViewById(R.id.loginPassword)
            val id=idEditText.text.toString()
            val password=passwordEditText.text.toString()

            val server=apiService.postLoginRequest(id,password)


            server.enqueue(object :  Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    if (response.isSuccessful) {

                        Toast.makeText(this@MainActivity,"로그인 성공", Toast.LENGTH_LONG).show()

                        val userResponse=response.body()
                        var userTotalData= userResponse?.user

                        val useremail= userTotalData?.email
                        val username=userTotalData?.username
                        val userphone=userTotalData?.phonenum
                        //val userpassword=userResponse?.password

                        Log.d("sfsfsfsfsf", userResponse.toString())
                        val token=userResponse?.token


                        if(token!=null){
                            MyApp.prefs.setString("jwt_token",token)

                            MyApp.prefs.setString("userName",username)
                            MyApp.prefs.setString("userEmail",useremail)
                            MyApp.prefs.setString("userPhone",userphone)
                            //MyApp.prefs.setString("userPassword",token)
                            Log.d("userrrr",response.body().toString())

                        }


                        var intent= Intent(this@MainActivity,MenuActivity::class.java)
                        startActivity(intent)

                    } else { //로그인 안된코든가
                        Toast.makeText(this@MainActivity,"아이디나 비밀번호가 틀렸습니다.",Toast.LENGTH_LONG).show()

                        val errorBody = response.errorBody()?.string()
                        Log.d("로그인이안된듯?","$errorBody")
                        Log.d("errorCode","$response.code()")
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Toast.makeText(this@MainActivity,"unknownError",Toast.LENGTH_LONG).show()
                    Log.d("NetworkError", "네트워크 오류: ${t.message}")
                }
            })

            //var intent= Intent(this@MainActivity,MenuActivity::class.java)
            //startActivity(intent)

        }

        val defaultColor = Color.parseColor("#4CAF50") //버튼 누르면 글짜 색 바뀌게끔
        regBtn.setOnClickListener {//등록 버튼 동작

            var newColor= Color.parseColor("#2C9430")
            regBtn.setTextColor(newColor)

            handler.postDelayed({
                regBtn.setTextColor(defaultColor)
            }, 300) //버튼 누르면 글 색 바뀜

            var intent=Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onKeyDown(keyCode:Int, event: KeyEvent?):Boolean{ //로그인 화면에서 뒤로 가기 두번 누르면 강종
        if(keyCode== KeyEvent.KEYCODE_BACK){
            if(System.currentTimeMillis()-initTime>2000){
                Toast.makeText(this,"한번 더 누르시면 어플이 종료됩니다.", Toast.LENGTH_LONG).show()
                initTime=System.currentTimeMillis()
                return true
            }else{
                finishAffinity() // 현재 액티비티와 모든 하위 액티비티를 종료합니다.
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}