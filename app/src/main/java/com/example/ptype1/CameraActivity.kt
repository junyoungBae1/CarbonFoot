package com.example.ptype1

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class CameraActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService
    private lateinit var foodDataSet : Co2Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val photoPath = intent.getStringExtra("photoPath")
        val bitmap=BitmapFactory.decodeFile(photoPath)
        val imgView=findViewById<ImageView>(R.id.cameraResultImg)
        imgView.setImageBitmap(bitmap)
        Glide.with(this).load(photoPath).into(imgView)
        //찍은 사진을 layout에 띄우는 코드

        val calculBtn=findViewById<Button>(R.id.calculateBtn)//계산 btn
        val regDataBtn = findViewById<Button>(R.id.RegFoodDataBtn) //음식 저장 btn


        retrofit = Retrofit.Builder() //retrofit 정의
            .baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService=retrofit.create(ApiService::class.java) //retrofit 사전 초기화


        val cardView = findViewById<CardView>(R.id.cardCo2Result) //Co2결과창 정의
        calculBtn.setOnClickListener { //계산하기 btn
            val server=apiService.postFoodRequest("오곡밥")

            server.enqueue(object : Callback<FoodDTO> {
                override fun onResponse(call: Call<FoodDTO>, response: Response<FoodDTO>) {
                    if (response.isSuccessful) {

                        var EmissionCo2=0.0
                        var EmissionCo2toTree=0.0
                        var EmissionCo2toCar=0.0

                        val responseBody=response.body()
                        if (responseBody!= null){
                            //EmissionCo2= responseBody.totalEmission.toDouble()
                            EmissionCo2=0.4
                            EmissionCo2toTree= 0.1
                            EmissionCo2toCar= 1.7
                        }

                        val co2TextView = cardView.findViewById<TextView>(R.id.Co2Text)
                        val treeTextView = cardView.findViewById<TextView>(R.id.treeText)
                        val carTextView = cardView.findViewById<TextView>(R.id.carText)

                        co2TextView.text="$EmissionCo2 kgCO₂e"
                        treeTextView.text="소나무 \n약 $EmissionCo2toTree 그루 "
                        carTextView.text="약 $EmissionCo2toCar km"
                        //계산하기 버튼을 누르고 서버 연결 성공하면 data들 띄우기

                        foodDataSet = Co2Data(arrayListOf("오곡밥"),arrayListOf(EmissionCo2),arrayListOf(EmissionCo2toTree),arrayListOf(EmissionCo2toCar),bitmap)
                        //추후 저장할 때 사용하기위한 dataset선언

                        cardView.visibility = View.VISIBLE //계산 성공하면 창에 띄우기

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

        var selectedItem: String?=null //아침 중식 석식 간식 선택 Item 선언
        var selectedPhoto : String? =null // 사진 선택 유무 Item
        var etc=1
        if (selectedItem=="간식"){
            etc=0
        } //간식일 때 etc value 0


        regDataBtn.setOnClickListener { //저장하기 btn
            val jsonFoodArray = JSONArray(foodDataSet.foodArr).toString()
            val jsonCo2Array = JSONArray(foodDataSet.totalCo2).toString()//배열 json화

            val file= File(photoPath)
            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            val photoPart = MultipartBody.Part.createFormData("img", file.name, requestFile) //
            // 사진 file dataset 정의

            val emailRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "a000000@konkuk.ac.kr")
            val FoodRequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), jsonFoodArray)
            val Co2RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), jsonCo2Array)
            val etcRequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), etc.toString())
            //responseBody 변수형으로 계산하기에서 얻은 data를 재가공


            if(foodDataSet.totalCo2!=null){
                val server2=apiService.postFoodCo2Request(emailRequestBody,FoodRequestBody,Co2RequestBody,etcRequestBody,photoPart)
                var alertDialog: AlertDialog? = null
                var secondDialog: AlertDialog? = null //알람 다이어로그 정의

                val items = arrayOf("조식", "중식", "석식", "간식")
                val check_image = arrayOf("저장한다", "저장하지 않는다") //item 변수 정의

                val builder = AlertDialog.Builder(this) //AlertDialog 이중 생성
                    .setTitle("식사 유형을 선택해주세요")
                    .setSingleChoiceItems(items, -1) { dialog, which ->
                        selectedItem = items[which]
                    }
                    .setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss()

                        val secondBuilder = AlertDialog.Builder(this)
                            .setTitle("두 번째 다이얼로그")
                            .setSingleChoiceItems(check_image, -1) { dialog, which ->
                                selectedPhoto = check_image[which]
                            }
                            .setPositiveButton("확인") { _, _ ->
                                Toast.makeText(this, "저장되었습니다", Toast.LENGTH_LONG).show()

                                server2.enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        if (response.isSuccessful) {

                                            val responseBody=response.body()
                                            Log.d("sucesssssss",responseBody.toString())
                                            // 응답 성공 처리
                                        } else {
                                            // 서버 응답 오류 처리
                                            val errorBody = response.errorBody()?.string()
                                            Log.d("응답오류?","$errorBody")
                                            Log.d("errorCodeWhy","$response.code()")
                                        }
                                    }
                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        // 네트워크 오류 처리
                                        Log.d("FailureError", "오류 코드 : ${t.message}")
                                    }
                                })
                            }
                            .setNegativeButton("취소") { _, _ ->
                                // 두 번째 다이얼로그의 취소 버튼 클릭 시 수행할 동작 추가
                                secondDialog?.dismiss()
                                alertDialog?.dismiss()
                            }

                        secondDialog = secondBuilder.create()
                        secondDialog?.show()
                    }
                    .setNegativeButton("취소") { _, _ ->
                        // 첫 번째 다이얼로그의 취소 버튼 클릭 시 수행할 동작 추가
                        alertDialog?.dismiss()
                    }

                alertDialog = builder.create()
                alertDialog.show()

            }
        }

        /*val SearchBtn=findViewById<TextView>(R.id.FoodDetectionError)
        SearchBtn.setOnClickListener {

        }*/
    }

}