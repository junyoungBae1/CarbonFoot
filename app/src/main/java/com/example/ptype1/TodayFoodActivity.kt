package com.example.ptype1

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.util.GregorianCalendar

class TodayFoodActivity: AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService
    private val items = mutableListOf<TodayFoodData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todayfood)
        val myEmail=MyApp.prefs.getString("userEmail",null)

        val dateBtn=findViewById<Button>(R.id.calBtn)
        val yesterday=findViewById<TextView>(R.id.leftBtn)
        val tomorrow=findViewById<TextView>(R.id.rightBtn)

        var today :GregorianCalendar
        var year : Int
        var month : Int
        var day : Int

        items.add(TodayFoodData(R.drawable.kingminseok,"1","1","1"))
        val calenderIns=Calendar.getInstance()
        var dateString=""

        retrofit = Retrofit.Builder() //retrofit 정의
            .baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService=retrofit.create(ApiService::class.java)

        dateBtn.setOnClickListener {

            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("정보를 불러오는 중...")
            progressDialog.setCancelable(false) // 사용자가 취소할 수 없도록 설정
            progressDialog.show()
            //정보를 불러오는중... 창

            today= GregorianCalendar()
            year=today.get(Calendar.YEAR)
            month=today.get(Calendar.MONTH)
            day=today.get(Calendar.DATE)


            val dlg= DatePickerDialog(this,object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    dateString="${year}-${month+1}-${dayOfMonth}"
                    calenderIns.set(year,month+1,dayOfMonth)
                    Log.d("dayCheckkkkkkk : ",dateString)

                    findViewById<TextView>(R.id.DayText).setText(dateString)
                    //날짜 변경한 날짜 반영.

                    val server=apiService.postDateRequest(myEmail,dateString)

                    server.enqueue(object : Callback<ImageDTO> {
                        override fun onResponse(call: Call<ImageDTO>, response: Response<ImageDTO>) {
                            if (response.isSuccessful) {

                                progressDialog.dismiss()

                                val responseBody=response.body()

                                Log.d("succcesss",responseBody.toString())

                                val recyclerview=findViewById<RecyclerView>(R.id.foodView)
                                val rvAdapter=TodayFoodAdapter(baseContext,items)
                                recyclerview.adapter=rvAdapter

                                recyclerview.layoutManager= GridLayoutManager(this@TodayFoodActivity,2)
                                // 응답 성공 처리

                            } else {

                                progressDialog.dismiss()
                                // 서버 응답 오류 처리
                                val errorBody = response.errorBody()?.string()
                                Log.d("응답오류?","$errorBody")
                                Log.d("errorCode","$response.code()")
                            }
                        }
                        override fun onFailure(call: Call<ImageDTO>, t: Throwable) {
                            progressDialog.dismiss()
                            // 네트워크 오류 처리
                            Log.d("FailureError", "오류 코드 : ${t.message}")
                        }
                    })

                }
            },year,month,day)
            dlg.show()


        }//캘린더로 날짜 선택

        yesterday.setOnClickListener {
            //val progressDialog = ProgressDialog(this)
            //progressDialog.setMessage("정보를 불러오는 중...")
            //progressDialog.setCancelable(false) // 사용자가 취소할 수 없도록 설정
            //progressDialog.show()
            //정보를 불러오는중... 창

            

        }

    }
}