package com.example.ptype1

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.DayOfWeek
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todayfood)
        recyclerview = findViewById<RecyclerView>(R.id.foodView)
        val myEmail=MyApp.prefs.getString("userEmail",null)

        val dateBtn=findViewById<Button>(R.id.calBtn)
        val yesterday=findViewById<TextView>(R.id.leftBtn)
        val tomorrow=findViewById<TextView>(R.id.rightBtn) //버튼

        setTodayDateToTextView()
        //현재 시각 띄우는 함수 (UI에서)


        val calendarIns=Calendar.getInstance()
        var dateString=LocalDate.now().toString() //오늘 날짜를 서버에 보냄

        retrofit = Retrofit.Builder() //retrofit 정의
            .baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService=retrofit.create(ApiService::class.java)
        server=apiService.postDateRequest(myEmail,dateString)

        progressDialog = ProgressDialog(this)//request완료될떄 까지 버퍼링
        enqueueImageRequest(server,progressDialog) //retrofit함수

        recyclerview.removeAllViewsInLayout();


        dateBtn.setOnClickListener {

            progressDialog.setMessage("정보를 불러오는 중...")
            progressDialog.setCancelable(false) // 사용자가 취소할 수 없도록 설정
            progressDialog.show()
            //정보를 불러오는중... 창

            val today= GregorianCalendar()
            val year=today.get(Calendar.YEAR)
            val month=today.get(Calendar.MONTH)
            val day=today.get(Calendar.DATE)

            val dlg= DatePickerDialog(this,object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

                    calendarIns.set(year,month,dayOfMonth)
                    Log.d("dayCheckkkkkkk : ",dateString)

                    val sendDateFormat=SimpleDateFormat("yyyy-MM-dd")
                    val sendDateString = sendDateFormat.format(calendarIns.time)

                    server=apiService.postDateRequest(myEmail,sendDateString)
                    enqueueImageRequest(server,progressDialog)
                    //---------------server request 코드--------------

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd (E)", Locale.getDefault())
                    val dateString = dateFormat.format(calendarIns.time)
                    findViewById<TextView>(R.id.DayText).setText(dateString)
                    //-----------날짜 변경한 날짜 반영. --------------

                    items.clear()
                    val rvAdapter = TodayFoodAdapter(baseContext, items)
                    rvAdapter.ClearData(items)

                }
            },year,month,day)
            dlg.show()


        }//캘린더로 날짜 선택

        /*yesterday.setOnClickListener {
            items.clear()
            val rvAdapter = TodayFoodAdapter(baseContext, items)
            rvAdapter.ClearData(items)

            val currentDate = getCurrentDateFromTextView()
            currentDate.add(Calendar.DAY_OF_MONTH, -1)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = dateFormat.format(currentDate.time)

            server=apiService.postDateRequest(myEmail,dateString)
            progressDialog.setMessage("정보를 불러오는 중...")
            progressDialog.setCancelable(false) // 사용자가 취소할 수 없도록 설정
            progressDialog.show()
            enqueueImageRequest(server,progressDialog)

            updateDateButton(currentDate)

            DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))
        }

        tomorrow.setOnClickListener {
            items.clear()
            val rvAdapter = TodayFoodAdapter(baseContext, items)
            rvAdapter.ClearData(items)

            val currentDate = getCurrentDateFromTextView()
            currentDate.add(Calendar.DAY_OF_MONTH, 1)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = dateFormat.format(currentDate.time)

            server=apiService.postDateRequest(myEmail,dateString)
            progressDialog.setMessage("정보를 불러오는 중...")
            progressDialog.setCancelable(false) // 사용자가 취소할 수 없도록 설정
            progressDialog.show()
            enqueueImageRequest(server,progressDialog)

            updateDateButton(currentDate)
            DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))
        }*/

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

    fun enqueueImageRequest(call: Call<ImageDTO>,progressDialog:ProgressDialog) {
        call.enqueue(object : Callback<ImageDTO> {
            override fun onResponse(call: Call<ImageDTO>, response: Response<ImageDTO>) {
                if (response.isSuccessful) {

                    progressDialog.dismiss()
                    val responseBody = response.body()

                    Log.d("succcesss", responseBody.toString())
                    for (i in 0 until responseBody!!.imagesDataCount) {
                        val encodedImageData = responseBody?.imagesData?.get(i)?.imageData
                        val decodedBytes = Base64.decode(encodedImageData, Base64.DEFAULT)
                        val decodedBitmap: Bitmap? = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                        if (decodedBitmap != null) {
                            val drawable = BitmapDrawable(baseContext.resources, decodedBitmap)
                            items.add(TodayFoodData(drawable, "1", "1", "1"))
                        }
                    }

                    val rvAdapter = TodayFoodAdapter(baseContext, items)
                    recyclerview.setAdapter(rvAdapter);
                    recyclerview.adapter = rvAdapter
                    recyclerview.layoutManager = GridLayoutManager(this@TodayFoodActivity, 2)

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
}