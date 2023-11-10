package com.example.ptype1

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import org.tensorflow.lite.Interpreter
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
//import com.example.ptype1.ml.LiteModelAiyVisionClassifierFoodV11
import org.tensorflow.lite.support.common.FileUtil
import java.text.DecimalFormat
import org.tensorflow.lite.support.label.Category
class CameraActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService
    private var foodDataSet : Co2Data? =null

    lateinit var cls: TensorCls
    var model =  "yolov4-416-final-int8.tflite"

    //lateinit var model: LiteModelAiyVisionClassifierFoodV11//임시


    lateinit var progressDialog : ProgressDialog
    lateinit var  foodresult : TextView
    lateinit var foodDetectionName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        progressDialog = ProgressDialog(this)//////

        progressDialog.setMessage("정보를 불러오는 중...")
        progressDialog.setCancelable(false) // 사용자가 취소할 수 없도록 설정
        progressDialog.show()


        val photoPath = intent.getStringExtra("photoPath") //카메라 찍고저장한 파일
        val imageUriString = intent.getStringExtra("photoURI") //갤러리에서 가져온 파일
        val connectionValue = intent.getStringExtra("Select")
        val imgView=findViewById<ImageView>(R.id.cameraResultImg)
        foodresult=findViewById(R.id.FoodResultMainText)

        var imageUri : Uri?=null
        var bitmap : Bitmap? =null
        var inputStream : InputStream?=null

        val calculBtn=findViewById<Button>(R.id.calculateBtn)//계산 btn
        val regDataBtn = findViewById<Button>(R.id.RegFoodDataBtn) //음식 저장 btn
        val back_text=findViewById<TextView>(R.id.FoodDetectionError)



        if(connectionValue=="1"){ //촬영일때
            val bitmap_1=BitmapFactory.decodeFile(photoPath)
            bitmap=bitmap_1
            imgView.setImageBitmap(bitmap_1)
            Glide.with(this).load(photoPath).into(imgView)

        }else if(connectionValue=="2"){ //갤러리일때
            imageUri = Uri.parse(imageUriString)
            inputStream = contentResolver.openInputStream(imageUri)

            val bitmap_2=BitmapFactory.decodeStream(inputStream,null,null)

            bitmap=bitmap_2
            imgView.setImageBitmap(bitmap_2)
        }

        /*tensorFlow*/
        //val labels = loadLabelsFile("labels.txt")

        var result : List<Category>
        var detectResult : List<Pair<String, Float>>? =null
        GlobalScope.launch(Dispatchers.Main){
            cls=TensorCls(baseContext)
            result =withContext(Dispatchers.Default) {
                cls.classify(bitmap!!)
            }
            detectResult = result.take(3).map { it.label to it.score }

            foodDetectionName=DelectFood(detectResult?.get(0)?.first)
            progressDialog.dismiss()
            foodresult.text=foodDetectionName //음식출력

            if(foodDetectionName=="음식을 인지하지 못했습니다"){
                calculBtn.visibility = View.GONE
                regDataBtn.visibility = View.GONE
                back_text.visibility= View.VISIBLE
                return@launch
            }
        }

        /*model = LiteModelAiyVisionClassifierFoodV11.newInstance(this)
        val bit_map = Bitmap.createScaledBitmap(bitmap!!, 224, 224, true)

        var detectImgArray: List<Pair<String, Float>>
        detectImgArray=processImage(bit_map!!)*/

        //Log.d("sfsfs",detectImgArray.toString())
        //cls = TensorCls(this)
        //Log.d("제발",cls.classify(bitmap!!).toString())

        /*tensorFlow*/


        //찍은 사진을 layout에 띄우는 코드


        val userEmail= MyApp.prefs.getString("userEmail",null)


        retrofit = Retrofit.Builder() //retrofit 정의
            .baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService=retrofit.create(ApiService::class.java) //retrofit 사전 초기화


        val cardView = findViewById<CardView>(R.id.cardCo2Result) //Co2결과창 정의
        calculBtn.setOnClickListener { //계산하기 btn

            progressDialog.setMessage("정보를 불러오는 중...")
            progressDialog.setCancelable(false) // 사용자가 취소할 수 없도록 설정
            progressDialog.show()

            val server=apiService.postFoodRequest(foodDetectionName)//임시

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

                        foodDataSet = Co2Data(arrayListOf(foodDetectionName),arrayListOf(EmissionCo2!!),arrayListOf(EmissionCo2toTree),arrayListOf(EmissionCo2toCar),bitmap!!)
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

        var selectedItem: String?=null //아침 중식 석식 간식 선택 Item 선언
        var selectedPhoto : String? =null // 사진 선택 유무 Item
        var etc=-1
       //간식일 때 etc value 0


        regDataBtn.setOnClickListener { //저장하기 btn

            if(foodDataSet==null){
                Toast.makeText(this, "계산한 후 눌러주세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var photoPart : MultipartBody.Part? =null
            if(connectionValue=="1"){
                val file= File(photoPath)
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                photoPart = MultipartBody.Part.createFormData("img", file.name, requestFile)
            }else if(connectionValue=="2"){
                val filePath: String? = getRealPathFromContentUri(imageUri!!)
                val file2 = File(filePath)
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file2)
                photoPart = MultipartBody.Part.createFormData("img", file2.name, requestFile)

            } // 사진 file dataset 정의

            val jsonFoodArray = JSONArray(foodDataSet!!.foodArr).toString()
            val jsonCo2Array = JSONArray(foodDataSet!!.totalCo2).toString()//배열 json화

            val emailRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userEmail)
            val FoodRequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), jsonFoodArray)
            val Co2RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), jsonCo2Array)
            var etcRequestBody : RequestBody?
            //responseBody 변수형으로 계산하기에서 얻은 data를 재가공




            if(foodDataSet!=null &&  photoPart!=null){
                var server2 : Call<Void>
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
                        when (selectedItem) {
                            "조식" -> etc = 1
                            "중식" -> etc = 2
                            "석식" -> etc = 3
                            else -> etc = 0
                        }

                        etcRequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(), etc.toString())

                        val secondBuilder = AlertDialog.Builder(this)
                            .setTitle("두 번째 다이얼로그")
                            .setSingleChoiceItems(check_image, -1) { dialog, which ->
                                selectedPhoto = check_image[which]
                            }
                            .setPositiveButton("확인") { _, _ ->
                                if(selectedPhoto=="저장한다"){
                                    Toast.makeText(this, "저장되었습니다", Toast.LENGTH_LONG).show()
                                    server2=apiService.postFoodCo2Request(emailRequestBody,FoodRequestBody,Co2RequestBody,etcRequestBody!!,photoPart)
                                    Log.d("etcccccccc",etc.toString())

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
                                }else{
                                    Toast.makeText(this, "저장하지 않았습니다", Toast.LENGTH_LONG).show()
                                }


                                var intent= Intent(this@CameraActivity,MenuActivity::class.java)
                                startActivity(intent)
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

    private fun getFunInterpreter(modelPath: String): Interpreter {
        return Interpreter(uploadModelFile(this,modelPath))
    }

    private fun uploadModelFile(cameraActivity: CameraActivity, modelPath: String): MappedByteBuffer {
        val fileDes=cameraActivity.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDes.fileDescriptor)
        val fileChannel : FileChannel=inputStream.channel

        var startOffSet=fileDes.startOffset
        var declared_Length=fileDes.declaredLength

        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffSet,declared_Length)

    }

    /*
    private fun processImage(bitmap: Bitmap) :  List<Pair<String, Float>> {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .build()
        var tImage = TensorImage(DataType.FLOAT32)
        tImage.load(bitmap)
        tImage = imageProcessor.process(tImage)

        // Runs model inference and gets result.
        val outputs = model.process(tImage)
        val probability = outputs.probabilityAsCategoryList

        val top5 = probability.sortedByDescending { it.score }.take(5)

        top5.forEach { classification ->
            println("${classification.label}, ${classification.score}")
        }//임시

        return top5.map { Pair(it.label, it.score) }
    }*/

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

