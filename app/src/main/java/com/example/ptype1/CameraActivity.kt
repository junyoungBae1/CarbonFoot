package com.example.ptype1

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CameraActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val photoPath = intent.getStringExtra("photoPath")
        val bitmap=BitmapFactory.decodeFile(photoPath)

        val imgView=findViewById<ImageView>(R.id.cameraResultImg)
        imgView.setImageBitmap(bitmap)
        Glide.with(this).load(photoPath).into(imgView)

        val totalCo2Text=findViewById<TextView>(R.id.totalCo2Amount)

        retrofit = Retrofit.Builder() //retrofit 정의
            .baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService=retrofit.create(ApiService::class.java)

        val server=apiService.postFoodRequest("오곡밥")

        server.enqueue(object : Callback<FoodDTO> {
            override fun onResponse(call: Call<FoodDTO>, response: Response<FoodDTO>) {
                if (response.isSuccessful) {

                    val responseBody=response.body()
                    val numCo2= responseBody?.totalEmission?.toFloat()
                    totalCo2Text.text="총 Co2는 $numCo2 \n(Co2e)입니다 "

                    Log.d("succcesss",responseBody.toString())

                    // 응답 성공 처리

                } else {
                    // 서버 응답 오류 처리
                    val errorBody = response.errorBody()?.string()
                    Log.d("응답오류?","$errorBody")
                    Log.d("errorCode","$response.code()")
                }
            }
            override fun onFailure(call: Call<FoodDTO>, t: Throwable) {
                // 네트워크 오류 처리
                Log.d("FailureError", "오류 코드 : ${t.message}")
            }
        })






    }
}