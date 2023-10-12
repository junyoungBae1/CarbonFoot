package com.example.ptype1


import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text
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

            var intent= Intent(this@MainActivity,MenuActivity::class.java)
            startActivity(intent)

            /*idEditText=findViewById(R.id.loginEmail)
            passwordEditText=findViewById(R.id.loginPassword)
            val id=idEditText.text.toString()
            val password=passwordEditText.text.toString()

            val server=apiService.postLoginRequest(id,password)


            server.enqueue(object :  Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    if (response.isSuccessful) {

                        Toast.makeText(this@MainActivity,"로그인 성공", Toast.LENGTH_LONG).show()

                        val userResponse=response.body()
                        val useremail=userResponse?.email
                        val username=userResponse?.name
                        val userphone=userResponse?.phonenum
                        val userpassword=userResponse?.password

                        val data_name=intent.putExtra("username",username)
                        val data_email=intent.putExtra("useremail",useremail)
                        val data_phone=intent.putExtra("userphone",userphone)
                        val data_password=intent.putExtra("userpassword",userpassword)


                        var intent= Intent(this@MainActivity,MenuActivity::class.java)
                        startActivity(intent)

                    } else {
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
            */
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