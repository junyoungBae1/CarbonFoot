package com.example.ptype1

import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class TodayFoodActivity: AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService

    private var items = mutableListOf<TodayFoodData>()
    private lateinit var server :Call<ImageDTO>

    lateinit var progressDialog : ProgressDialog

    lateinit var recyclerview : RecyclerView
    val handler = android.os.Handler() //handler 및 btn 효과 변수 초기화

    lateinit var emptyView :TextView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todayfood)
        recyclerview = findViewById(R.id.foodView)
        emptyView = findViewById<TextView>(R.id.emptyView)

        /*날짜정의*/
        var today= GregorianCalendar()
        var year=today.get(Calendar.YEAR)
        var month=today.get(Calendar.MONTH)
        var day=today.get(Calendar.DATE)

        var calendarIns=Calendar.getInstance()
        calendarIns.set(year, month, day)
        /*날짜정의*/

        getSupportActionBar()?.setTitle("발자국")//appbar 형성
        val myEmail=MyApp.prefs.getString("userEmail",null)

        val dateBtn=findViewById<ImageView>(R.id.calBtn)
        dateBtn.bringToFront()
        val yesterday=findViewById<TextView>(R.id.leftBtn)
        val tomorrow=findViewById<TextView>(R.id.rightBtn)
        //버튼

        setTodayDateToTextView()
        //현재 시각 띄우는 함수 (UI에서)

        var dateString=LocalDate.now().toString() //오늘 날짜를 서버에 보냄

        retrofit = Retrofit.Builder() //retrofit 정의
            .baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService=retrofit.create(ApiService::class.java)
        server=apiService.postDateRequest(myEmail,dateString)

        progressDialog = ProgressDialog(this)//request완료될떄 까지 버퍼링
        enqueueImageRequest(server,progressDialog,dateString) //retrofit함수

        recyclerview.removeAllViewsInLayout();


        dateBtn.setOnClickListener {
            btnEffect(dateBtn)

            //정보를 불러오는중... 창
            DatePickerDialog(this,object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    calendarIns.set(year,month,dayOfMonth)
                    Log.d("dayCheckkkkkkk : ",dateString)

                    val sendDateFormat=SimpleDateFormat("yyyy-MM-dd")
                    val sendDateString = sendDateFormat.format(calendarIns.time)

                    server=apiService.postDateRequest(myEmail,sendDateString)
                    enqueueImageRequest(server,progressDialog,dateString)
                    //---------------server request 코드--------------

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd (E)", Locale.getDefault())
                    val dateString = dateFormat.format(calendarIns.time)
                    findViewById<TextView>(R.id.DayText).setText(dateString)
                    //-----------날짜 변경한 날짜 반영. --------------

                    items.clear()
                    val rvAdapter = TodayFoodAdapter(baseContext, items)
                    rvAdapter.ClearData(items)

                }
            },calendarIns.get(Calendar.YEAR), calendarIns.get(Calendar.MONTH),
                calendarIns.get(Calendar.DAY_OF_MONTH)).show()



        }//캘린더로 날짜 선택



        yesterday.setOnClickListener {
            btnEffect(yesterday)

            items.clear()
            val rvAdapter = TodayFoodAdapter(baseContext, items)
            rvAdapter.ClearData(items)

            val currentDate = getCurrentDateFromTextView()
            currentDate.add(Calendar.DAY_OF_MONTH, -1)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = dateFormat.format(currentDate.time)

            server=apiService.postDateRequest(myEmail,dateString)
            enqueueImageRequest(server,progressDialog,dateString)

            updateDateButton(currentDate)
            DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                // 날짜가 선택되었을 때의 동작을 여기에 작성합니다.
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))
        }

        tomorrow.setOnClickListener {
            btnEffect(tomorrow)

            items.clear()
            val rvAdapter = TodayFoodAdapter(baseContext, items)
            rvAdapter.ClearData(items)

            val currentDate = getCurrentDateFromTextView()
            currentDate.add(Calendar.DAY_OF_MONTH, 1)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = dateFormat.format(currentDate.time)

            server=apiService.postDateRequest(myEmail,dateString)
            enqueueImageRequest(server,progressDialog,dateString)

            updateDateButton(currentDate)
            DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))
        }

    }

    fun updateDateButton(selectedDate: Calendar)  {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd (E)", Locale.getDefault())
        val dateString = dateFormat.format(selectedDate.time)

        findViewById<TextView>(R.id.DayText).text = dateString

    }


    private fun getCurrentDateFromTextView(): Calendar {
        val dateString = findViewById<TextView>(R.id.DayText).text.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd (E)", Locale.getDefault())
        val currentDate = Calendar.getInstance()
        currentDate.time = dateFormat.parse(dateString)!!

        return currentDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setTodayDateToTextView() {
        val layout_today = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd (E)", Locale.getDefault())
        val modifiedDateString = layout_today.format(dateFormatter)

        findViewById<TextView>(R.id.DayText).setText(modifiedDateString)
    }

    fun enqueueImageRequest(call: Call<ImageDTO>,progressDialog:ProgressDialog,dateString: String?) {

        progressDialog.setMessage("정보를 불러오는 중...")
        progressDialog.setCancelable(false) // 사용자가 취소할 수 없도록 설정
        progressDialog.show()

        call.enqueue(object : Callback<ImageDTO> {
            override fun onResponse(call: Call<ImageDTO>, response: Response<ImageDTO>) {
                if (response.isSuccessful) {


                    progressDialog.dismiss()
                    val responseBody = response.body()


                    for (i in 0 until responseBody!!.imagesDataCount) {
                        Log.d("succcesss", responseBody.imagesData?.get(i)?.imageFoods.toString())
                        val valuesss= responseBody.imagesData?.get(i)
                        val encodedImageData = valuesss?.imageData
                        val decodedBytes = Base64.decode(encodedImageData, Base64.DEFAULT)
                        val decodedBitmap: Bitmap? = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                        val foodData= valuesss!!.imageFoods
                        val foodStoreTime=valuesss!!.imageDate

                        var foodname=""
                        var foodEmssion=0.0
                        for(i in 0 until foodData!!.size){
                            foodname=foodname+foodData[i].foodname
                            foodEmssion=foodEmssion+foodData[i].totalEmssion
                        }


                        val foodEmissionString= "Total : " + foodEmssion.toString()+" kgCo2e"

                        if (decodedBitmap != null) {
                            val drawable = BitmapDrawable(baseContext.resources, decodedBitmap)
                            items.add(TodayFoodData(drawable, foodname, foodEmissionString,foodStoreTime ))
                        }
                    }

                    /* item없으면 없다고 표시*/
                    if (responseBody!!.imagesDataCount<=0) {
                        recyclerview.visibility = View.GONE
                        emptyView.visibility = View.VISIBLE
                    } else {
                        recyclerview.visibility = View.VISIBLE
                        emptyView.visibility = View.GONE
                    }

                    val rvAdapter = TodayFoodAdapter(baseContext, items.asReversed())

                    recyclerview.adapter = rvAdapter
                    recyclerview.layoutManager = LinearLayoutManager(this@TodayFoodActivity)

                } else {
                    progressDialog.dismiss()
                    val errorBody = response.errorBody()?.string()
                    Log.d("응답오류?", "$errorBody")
                    Log.d("errorCode", "$response.code()")
                }
            }

            override fun onFailure(call: Call<ImageDTO>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("FailureError", "오류 코드 : ${t.message}")
            }
        })
    }

    private fun btnEffect(btn: View) {
        val defaultDrawable = btn.background // 현재 Drawable을 저장

        val newColor = Color.parseColor("#D1D0D0")
        btn.setBackgroundColor(newColor)

        handler.postDelayed({
            // 이전 Drawable로 복원
            btn.background = defaultDrawable
        }, 300)
    }
}