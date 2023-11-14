package com.example.ptype1

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Double.sum
import java.text.DecimalFormat
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
    lateinit var timeSelectBtn : Button

    private var total_score=mutableListOf<Int>()
    private var total_Emission=mutableListOf<Double>()
    private lateinit var totalEmissionText : TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todayfood)
        recyclerview = findViewById(R.id.foodView)
        emptyView = findViewById(R.id.emptyView)
        timeSelectBtn=findViewById(R.id.foodSelectedTime)
        totalEmissionText=findViewById(R.id.textTotalCo2Text)

        total_score= mutableListOf(0, 0, 0,0)
        total_Emission= mutableListOf(0.0, 0.0, 0.0,0.0)





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
                    val rvAdapter = TodayFoodAdapter(baseContext, items,object: TodayFoodAdapter.OnItemDeleteClickListener {
                        override fun onItemDelete(date: String) {
                            // 여기에 삭제 요청을 보내는 코드를 작성합니다.
                            deleteItem(date)
                        }
                    })
                    rvAdapter.ClearData(items)

                }
            },calendarIns.get(Calendar.YEAR), calendarIns.get(Calendar.MONTH),
                calendarIns.get(Calendar.DAY_OF_MONTH)).show()



        }//캘린더로 날짜 선택



        yesterday.setOnClickListener {
            btnEffect(yesterday)

            items.clear()
            val rvAdapter = TodayFoodAdapter(baseContext, items,object: TodayFoodAdapter.OnItemDeleteClickListener {
                override fun onItemDelete(date: String) {
                    // 여기에 삭제 요청을 보내는 코드를 작성합니다.
                    deleteItem(date)
                }
            })
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
            val rvAdapter = TodayFoodAdapter(baseContext, items,object: TodayFoodAdapter.OnItemDeleteClickListener {
                override fun onItemDelete(date: String) {
                    // 여기에 삭제 요청을 보내는 코드를 작성합니다.
                    deleteItem(date)
                }
            })
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

        timeSelectBtn.setOnClickListener {
            // 팝업 메뉴 생성
            val popupMenu = PopupMenu(this, timeSelectBtn)

            // 메뉴 리소스 파일과 연동
            popupMenu.menuInflater.inflate(R.menu.todayfood_btn, popupMenu.menu)

            // 메뉴 아이템 클릭 리스너 등록
            popupMenu.setOnMenuItemClickListener { item ->
                // 각 메뉴 아이템에 대한 동작 처리
                when (item.itemId) {
                    R.id.alltimefood->{
                        val filteredList = items
                        ViewEmpty(filteredList.size)
                        timeSelectBtn.text="전체"
                        val rvAdapter = TodayFoodAdapter(baseContext, filteredList.asReversed(),object: TodayFoodAdapter.OnItemDeleteClickListener {
                            override fun onItemDelete(date: String) {
                                // 여기에 삭제 요청을 보내는 코드를 작성합니다.
                                deleteItem(date)
                            }
                        })
                        rvAdapter.ClearData(filteredList) // 어댑터 데이터 업데이트
                        recyclerview.adapter = rvAdapter // RecyclerView에 새로운 어댑터 설정
                        var sum_total_score=0.0
                        for(i in total_Emission){
                            sum_total_score+=i
                        }
                        totalEmissionText.text="총 탄소배출량은 "+  Double2(sum_total_score)+" 입니다."
                    }

                    R.id.snack->{
                        val filteredList = items.filterTo(mutableListOf()) { it.etc == 0 }
                        ViewEmpty(filteredList.size)
                        timeSelectBtn.text="간식"
                        val rvAdapter = TodayFoodAdapter(baseContext, filteredList.asReversed(),object: TodayFoodAdapter.OnItemDeleteClickListener {
                            override fun onItemDelete(date: String) {
                                // 여기에 삭제 요청을 보내는 코드를 작성합니다.
                                deleteItem(date)
                            }
                        })
                        rvAdapter.ClearData(filteredList) // 어댑터 데이터 업데이트
                        recyclerview.adapter = rvAdapter // RecyclerView에 새로운 어댑터 설정
                        totalEmissionText.text="총 탄소배출량은 "+  Double2(total_Emission.get(0))+" 입니다."
                    }
                    R.id.morning -> {


                        val filteredList = items.filterTo(mutableListOf()) { it.etc == 1 }
                        ViewEmpty(filteredList.size)
                        timeSelectBtn.text="아침"
                        val rvAdapter = TodayFoodAdapter(baseContext, filteredList.asReversed(),object: TodayFoodAdapter.OnItemDeleteClickListener {
                            override fun onItemDelete(date: String) {
                                // 여기에 삭제 요청을 보내는 코드를 작성합니다.
                                deleteItem(date)
                            }
                        })
                        rvAdapter.ClearData(filteredList) // 어댑터 데이터 업데이트
                        recyclerview.adapter = rvAdapter // RecyclerView에 새로운 어댑터 설정
                        // 아침 메뉴 아이템 선택 시 동작
                        totalEmissionText.text="총 탄소배출량은 "+  Double2(total_Emission.get(1))+" 입니다."
                    }
                    R.id.afternoon -> {
                        val filteredList = items.filterTo(mutableListOf()) { it.etc == 2 }
                        ViewEmpty(filteredList.size)
                        timeSelectBtn.text="점심"
                        val rvAdapter = TodayFoodAdapter(baseContext, filteredList.asReversed(),object: TodayFoodAdapter.OnItemDeleteClickListener {
                            override fun onItemDelete(date: String) {
                                // 여기에 삭제 요청을 보내는 코드를 작성합니다.
                                deleteItem(date)
                            }
                        })
                        rvAdapter.ClearData(filteredList) // 어댑터 데이터 업데이트
                        recyclerview.adapter = rvAdapter // RecyclerView에 새로운 어댑터 설정
                        // 점심 메뉴 아이템 선택 시 동작
                        totalEmissionText.text="총 탄소배출량은 "+  Double2(total_Emission.get(2))+" 입니다."
                    }
                    R.id.dinner -> {
                        val filteredList = items.filterTo(mutableListOf()) { it.etc == 3 }
                        ViewEmpty(filteredList.size)
                        timeSelectBtn.text="저녁"
                        val rvAdapter = TodayFoodAdapter(baseContext, filteredList.asReversed(),object: TodayFoodAdapter.OnItemDeleteClickListener {
                            override fun onItemDelete(date: String) {
                                // 여기에 삭제 요청을 보내는 코드를 작성합니다.
                                deleteItem(date)
                            }
                        })
                        rvAdapter.ClearData(filteredList) // 어댑터 데이터 업데이트
                        recyclerview.adapter = rvAdapter // RecyclerView에 새로운 어댑터 설정
                        // 저녁 메뉴 아이템 선택 시 동작
                        totalEmissionText.text="총 탄소배출량은 "+  Double2(total_Emission.get(3))+" 입니다."
                    }
                }
                true
            }

            // 팝업 메뉴 표시
            popupMenu.show()
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

    fun enqueueImageRequest(call: Call<ImageDTO>,progressDialog:ProgressDialog,dateString : String) {

        progressDialog.setMessage("정보를 불러오는 중...")
        progressDialog.setCancelable(false) // 사용자가 취소할 수 없도록 설정
        progressDialog.show()

        call.enqueue(object : Callback<ImageDTO> {
            override fun onResponse(call: Call<ImageDTO>, response: Response<ImageDTO>) {
                if (response.isSuccessful) {

                    progressDialog.dismiss()
                    val responseBody = response.body()
                    Log.d("ResponseBoddddyis",responseBody.toString())

                    for (i in 0 until responseBody!!.imagesDataCount) {
                        Log.d("succcesss", responseBody.imagesData?.get(i)?.imageFoods.toString())
                        val valuesss= responseBody.imagesData?.get(i)
                        val encodedImageData = valuesss?.imageData
                        val decodedBytes = Base64.decode(encodedImageData, Base64.DEFAULT)
                        val decodedBitmap: Bitmap? = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                        val foodData= valuesss!!.imageFoods
                        val foodStoreTime=valuesss!!.imageDate

                        //val timeString = foodStoreTime!!.substringAfter(" ").substringBeforeLast(":") // 시간 문자열을 정수형으로 변환
                        //val hour = timeString.substringBefore(":").toInt()
                        val foodetc=valuesss!!.etc


                        var foodname=""
                        var foodEmssion=0.0
                        var foodScore=0

                        val df = DecimalFormat("#.##")

                        for(i in 0 until foodData!!.size){

                            foodname=foodname+foodData[i].foodname
                            foodEmssion=foodEmssion+foodData[i].totalEmssion
                            //foodScore=foodScore+foodData[i].score
                        }

                        val foodEmssion_v = df.format(foodEmssion)
                        val foodEmissionString= "Total : " + foodEmssion_v+" kgCo2e"


                        if (decodedBitmap != null) {
                            val drawable = BitmapDrawable(baseContext.resources, decodedBitmap)
                            items.add(TodayFoodData(drawable, foodname, foodEmissionString,foodStoreTime,foodScore,foodetc ))
                        }
                    }

                    total_Emission= mutableListOf(0.0, 0.0, 0.0,0.0)
                    if(responseBody.total_score!=null && responseBody.total_Emssion!=null){
                        total_score= responseBody.total_score
                        total_Emission=responseBody.total_Emssion
                    }
                    var sum_total_score=0.0
                    for(i in total_Emission){
                        sum_total_score+=i
                    }

                    Double2(sum_total_score)
                    totalEmissionText.text="총 탄소배출량은 "+  Double2(sum_total_score)+" 입니다." // 초기 탄소배출량 정의


                    /* item없으면 없다고 표시*/
                    ViewEmpty(responseBody!!.imagesDataCount)

                    val rvAdapter = TodayFoodAdapter(baseContext, items.asReversed(),object: TodayFoodAdapter.OnItemDeleteClickListener {
                        override fun onItemDelete(date: String) {
                            // 여기에 삭제 요청을 보내는 코드를 작성합니다.
                            deleteItem(date)
                        }
                    })

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
        }, 30)
    }

    /*fun ETCis(etc :Int) : Int {

        if(etc<=4 || etc>=17){
            return 2
        } else if( 5<=etc && etc<=11 ){
            return 0
        }else{
            return 1
        }

        return -1
    }*/

    fun ViewEmpty(count : Int){
        if (count<=0) {
            recyclerview.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerview.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    fun deleteItem(date: String){
        val server3=apiService.postImgDelete(date)

        val builder= AlertDialog.Builder(this@TodayFoodActivity)
        builder.setTitle("alarm").setMessage("삭제하시겠습니까?").
        setPositiveButton("YES",
            DialogInterface.OnClickListener{ dialogInterface: DialogInterface, i: Int ->

                server3.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.isSuccessful) {
                            Log.d("Deleteeee?",response.body().toString())
                            Toast.makeText(this@TodayFoodActivity,"항목을 삭제하였습니다", Toast.LENGTH_LONG).show()


                            finish();//인텐트 종료
                            overridePendingTransition(0, 0);//인텐트 효과 없애기
                            val intent = getIntent(); //인텐트
                            startActivity(intent); //액티비티 열기
                            overridePendingTransition(0, 0)

                        }else{
                            val errorBody = response.errorBody()?.string()
                            Log.d("whyGetBoardError","$errorBody")
                            Log.d("errorCode","$response.code()")
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        var errorMessage = "Unknown Error"
                        Toast.makeText(this@TodayFoodActivity, errorMessage, Toast.LENGTH_LONG).show()
                        Log.d("NetworkError", "네트워크 오류: ${t.message}")
                    }
                })

            }).setNegativeButton("NO", DialogInterface.OnClickListener { dialogInterface, i ->  })
        builder.create()
        builder.show()
    }

    fun Double2(num : Double):String{
        val df = DecimalFormat("#.##")
        val successDouble2 = df.format(num)

        return successDouble2

    }

}




