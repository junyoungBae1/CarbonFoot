package com.example.ptype1

import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity(){

    private lateinit var usernameEditText: EditText
    private lateinit var idEditText : EditText
    private lateinit var passwordEditText: EditText
    private lateinit var checkPasswordText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var signupButton: Button

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        //회원 가입 텍스트 입력창 선언
        usernameEditText = findViewById(R.id.editTextName)
        idEditText=findViewById(R.id.editTextID)
        passwordEditText = findViewById(R.id.editTextPassword)
        checkPasswordText=findViewById(R.id.editTextPasswordCheck)
        phoneNumberEditText = findViewById(R.id.editTextMobile)
        signupButton = findViewById(R.id.RegisterButton)
        //회원 가입 텍스트 입력창 선언


        retrofit = Retrofit.Builder().baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create()).build()
        apiService=retrofit.create(ApiService::class.java)
        //retrofit 선언

        signupButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = idEditText.text.toString()
            val password = passwordEditText.text.toString()
            val checkPassword = checkPasswordText.text.toString()
            val phonenum = phoneNumberEditText.text.toString()


            if (checkPassword!=password){
                checkPasswordText.backgroundTintList = ColorStateList.valueOf(Color.RED)
                //checkPasswordText.setHint("비밀번호를 올바르게 입력해주세요") //비밀번호 확인창 경고


                val builder=AlertDialog.Builder(this)
                builder.setTitle("Error").setMessage("비밀번호 확인이 올바르지 않습니다").
                        setPositiveButton("확인",DialogInterface.OnClickListener
                        { dialogInterface, i ->  })

                builder.create()
                builder.show()

                return@setOnClickListener

            } //이거는 비밀번호 확인하는 과정


            val server=apiService.postRegisterRequest(username,email,password,phonenum)
            server.enqueue(object : Callback<ResponseDTO>{
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    if(response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "회원가입 성공", Toast.LENGTH_LONG).show()
                        Log.d("usernameisssss",username)

                        val intent=Intent(this@RegisterActivity,MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        val errorBody = response.errorBody()?.string()
                        Log.d("whyloginerror","$errorBody")
                        Log.d("errorCode","$response.code()")
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity,"unknownError",Toast.LENGTH_LONG).show()
                    Log.d("NetworkError", "네트워크 오류: ${t.message}")
                }

            })

        }

    }
}

