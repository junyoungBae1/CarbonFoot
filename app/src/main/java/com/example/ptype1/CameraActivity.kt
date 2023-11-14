package com.example.ptype1

import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import java.io.InputStream
//import com.example.ptype1.ml.LiteModelAiyVisionClassifierFoodV11
import java.text.DecimalFormat
import org.tensorflow.lite.support.label.Category
class CameraActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService

    lateinit var cls: TensorCls
    lateinit var progressDialog : ProgressDialog
    lateinit var  foodresult : TextView

    var foodDetectionName : String? =null
    var cardView : CardView? =null
    private var foodDataSet : Co2Data? =null

    lateinit var regDataBtn : Button
    lateinit var searchBtn : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        progressDialog = ProgressDialog(this)//////

        progressDialog.setMessage("정보를 불러오는 중...")
        progressDialog.setCancelable(false) // 사용자가 취소할 수 없도록 설정
        progressDialog.show()


        val photoUriString= MyApp.prefs.getString("PhotoURI",null)
        val userEmail= MyApp.prefs.getString("userEmail",null)
        val connectionValue= MyApp.prefs.getNum("Select",0)


        var imageUri : Uri?=null
        var bitmap : Bitmap? =null
        var inputStream : InputStream?

        val imgView=findViewById<ImageView>(R.id.cameraResultImg)
        foodresult=findViewById(R.id.FoodResultMainText) //음식 출력 및 음식 text출력
        cardView = findViewById(R.id.cardCo2Result) //Co2결과창 정의

        regDataBtn = findViewById(R.id.RegFoodDataBtn) //음식 저장 btn
        searchBtn=findViewById(R.id.FoodDetectionError) //수정 btn

        val modifiedFood=intent.getStringExtra("ModfiedFood")


        retrofit = Retrofit.Builder() //retrofit 정의
            .baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService=retrofit.create(ApiService::class.java) //retrofit 사전 초기화


        if(connectionValue==1){ //촬영일때
            val bitmap_1=BitmapFactory.decodeFile(photoUriString)
            bitmap=bitmap_1
            imgView.setImageBitmap(bitmap_1)
            Glide.with(this).load(photoUriString).into(imgView)

        }else if(connectionValue==2){ //갤러리일때
            imageUri = Uri.parse(photoUriString)
            inputStream = contentResolver.openInputStream(imageUri)

            val bitmap_2=BitmapFactory.decodeStream(inputStream,null,null)

            bitmap=bitmap_2
            imgView.setImageBitmap(bitmap_2)
        }

        /*tensorFlow*/
        //val labels = loadLabelsFile("labels.txt")

        var result : List<Category>
        var detectResult : List<Pair<String, Float>>?

        //globalScope
        GlobalScope.launch(Dispatchers.Main){

            if(modifiedFood==null){
                cls=TensorCls(baseContext)
                result =withContext(Dispatchers.Default) {
                    cls.classify(bitmap!!)
                }
                detectResult = result.take(3).map { it.label to it.score }
                Log.d("detectiss",detectResult.toString())
                foodDetectionName=DelectFood(detectResult?.get(0)?.first)

            }else{
                foodDetectionName=modifiedFood
            }

            progressDialog.dismiss()
            foodresult.text=foodDetectionName //음식출력


            if(foodDetectionName=="음식을 인지하지 못했습니다"){

                regDataBtn.visibility = View.GONE
                searchBtn.text="음식 직접 검색하기"
            }else {
                regDataBtn.visibility = View.VISIBLE
                searchBtn.text = "음식이 틀렸습니까? 직접 검색"
            }

            val server=apiService.postFoodRequest(foodDetectionName!!)//임시
            EnqueueFoodRequest(server,bitmap!!,cardView!!)


            regDataBtn.setOnClickListener { //저장하기 btn

                var photoPart : MultipartBody.Part? =null
                if(connectionValue==1){
                    val file= File(photoUriString)
                    val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                    photoPart = MultipartBody.Part.createFormData("img", file.name, requestFile)
                }else if(connectionValue==2){
                    val filePath: String? = getRealPathFromContentUri(imageUri!!)
                    val file2 = File(filePath)
                    val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file2)
                    photoPart = MultipartBody.Part.createFormData("img", file2.name, requestFile)

                } // 사진 file dataset 정의


                val menu_items = arrayOf("아침", "점심", "저녁","간식") // 선택할 메뉴 항목들
                var etc=-1

                val builder = AlertDialog.Builder(this@CameraActivity)
                builder.setTitle("메뉴 선택") // 다이얼로그 제목 설정
                builder.setItems(menu_items) { dialog, which ->
                    val selectedMenu = menu_items[which] // 선택한 메뉴 항목

                    if(selectedMenu=="간식")etc=0
                    else if(selectedMenu=="아침")etc=1
                    else if(selectedMenu=="점심")etc=2
                    else if(selectedMenu=="저녁")etc=3

                    if (photoPart != null) {
                        foodDataSet?.let { it1 -> EnqueueFoodCo2Request(server,photoPart, it1,userEmail,etc) }
                    }

                    Toast.makeText(this@CameraActivity, "저장하였습니다", Toast.LENGTH_SHORT).show()
                    val intent=Intent(this@CameraActivity,MenuActivity::class.java)
                    startActivity(intent)
                }
                builder.setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss() // 다이얼로그 닫기
                }

                val dialog = builder.create()
                dialog.show()


            } //regBtn


            searchBtn.setOnClickListener{
                val intent=Intent(this@CameraActivity,SearchFoodActivity::class.java)
                startActivity(intent)


            }


        } //global scope





    }

    fun getRealPathFromContentUri(contentUri: Uri): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = baseContext.contentResolver.query(contentUri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
        }
        return filePath
    } //사진 앨범에서 찾을 때

    fun EnqueueFoodRequest(server : Call<FoodDTO> ,bitmap: Bitmap,cardView: CardView){

        server.enqueue(object : Callback<FoodDTO> {
            override fun onResponse(call: Call<FoodDTO>, response: Response<FoodDTO>) {
                if (response.isSuccessful) {
                    progressDialog.dismiss()

                    val resBody=response.body()

                    var EmissionCo2= resBody?.totalEmission?.toDouble()
                    var EmissionCo2toTree=0.0
                    var EmissionCo2toCar=0.0

                    if (resBody!= null){
                        //EmissionCo2= responseBody.totalEmission.toDouble()
                        EmissionCo2=resBody.totalEmission.toDouble()
                        EmissionCo2toTree= EmissionCo2*4.16
                        EmissionCo2toCar= EmissionCo2*27.27
                    }

                    val co2TextView = cardView.findViewById<TextView>(R.id.Co2Text)
                    val treeTextView = cardView.findViewById<TextView>(R.id.treeText)
                    val carTextView = cardView.findViewById<TextView>(R.id.carText)

                    val df = DecimalFormat("#.##")
                    val EmissionCo2_v = df.format(EmissionCo2)
                    val EmissionCo2toTree_v = df.format(EmissionCo2toTree)
                    val EmissionCo2toCar_v = df.format(EmissionCo2toCar)

                    co2TextView.text=EmissionCo2_v+" kgCO₂e"
                    treeTextView.text="소나무 \n약 "+ EmissionCo2toTree_v+ " 그루 "
                    carTextView.text="약 "+ EmissionCo2toCar_v +" km"
                    //계산하기 버튼을 누르고 서버 연결 성공하면 data들 띄우기

                    foodDataSet = Co2Data(arrayListOf(foodDetectionName!!),arrayListOf(EmissionCo2!!),arrayListOf(EmissionCo2toTree),arrayListOf(EmissionCo2toCar),bitmap!!)
                    //추후 저장할 때 사용하기위한 dataset선언

                    cardView.visibility = View.VISIBLE //계산 성공하면 창에 띄우기

                    // 응답 성공 처리

                } else {
                    progressDialog.dismiss()
                    // 서버 응답 오류 처리
                    val errorBody = response.errorBody()?.string()
                    Log.d("응답오류?","$errorBody")
                    Log.d("errorCode","$response.code()")
                }
            }
            override fun onFailure(call: Call<FoodDTO>, t: Throwable) {
                progressDialog.dismiss()
                // 네트워크 오류 처리
                Log.d("FailureError", "오류 코드 : ${t.message}")
            }
        })

    }

    fun EnqueueFoodCo2Request(server : Call<FoodDTO>,photoPart : MultipartBody.Part,foodDataSet : Co2Data
    ,userEmail : String,etc : Int){

        val jsonFoodArray = JSONArray(foodDataSet!!.foodArr).toString()
        val jsonCo2Array = JSONArray(foodDataSet!!.totalCo2).toString()//배열 json화


        val emailRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userEmail)
        val FoodRequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), jsonFoodArray)
        val Co2RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), jsonCo2Array)
        var etcRequestBody : RequestBody?
        //responseBody 변수형으로 계산하기에서 얻은 data를 재가공

        etcRequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), etc.toString())

        val server2= photoPart?.let { it1 ->
            apiService.postFoodCo2Request(emailRequestBody,FoodRequestBody,Co2RequestBody,etcRequestBody!!, it1)
        }

        Log.d("etcccccccc",etc.toString())

        server2!!.enqueue(object : Callback<Void> {
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
    fun DelectFood(foodName : String?) : String{
        when(foodName){
            "Sanchae_bibimbap"->return "비빔밥"
            "Dwaeji_Gukbab"->return "돼지국밥"
            "Ramen"->return "라면"
            "Mul_naengmyeon"->return "물냉면"
            "Bibim_naengmyeon"->return "비빔냉면"
            "Jajangmyeon"->return "짜장면"
            "Jjambbong"->return "짬뽕"
            "Pasta"->return "파스타"
            "Rice"-> return "쌀밥"

        }

        return "음식을 인지하지 못했습니다"
    }

    /*

    Sanchae_bibimbap
    Dwaeji_Gukbab
    Ramen
    Mul_naengmyeon
    Bibim_naengmyeon
    Jajangmyeon
    Jjambbong
    Pasta*/

}

