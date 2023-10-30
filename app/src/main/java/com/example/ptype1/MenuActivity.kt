package com.example.ptype1

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

class MenuActivity: AppCompatActivity()  {
    private lateinit var requestCameraFileLauncher : ActivityResultLauncher<Intent> //카메라 런쳐 미리 선언
    private lateinit var cardView1 : View
    private lateinit var cardView2 : View
    private lateinit var cardView3 : View
    private lateinit var cardView4 : View
    val handler = android.os.Handler() //handler 및 btn 효과 변수 초기화

    var initTime =0L
    private fun btnEffect(btn: View) {
        val defaultColor = Color.parseColor("#FFFFFF")
        var newColor= Color.parseColor("#D1D0D0")
        btn.setBackgroundColor(newColor)
        handler.postDelayed({
            btn.setBackgroundColor(defaultColor)
        }, 300) //
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val userName= MyApp.prefs.getString("userName",null)
        getSupportActionBar()?.setTitle(userName+"님")//appbar 형성


        // 메뉴 layout 형성 ------------------------
        cardView1=findViewById(R.id.card1)
        cardView2=findViewById(R.id.card2)
        cardView3=findViewById(R.id.card3)
        cardView4=findViewById(R.id.card4)

        val img1=cardView1.findViewById<ImageView>(R.id.MenuImage)
        val img2=cardView2.findViewById<ImageView>(R.id.MenuImage)
        val img3=cardView3.findViewById<ImageView>(R.id.MenuImage)
        val img4=cardView4.findViewById<ImageView>(R.id.MenuImage)

        img1.setImageResource(R.drawable.cameraimg)
        img2.setImageResource(R.drawable.checkfootimg)
        img3.setImageResource(R.drawable.comimg)
        img4.setImageResource(R.drawable.communityimg)


        val Content1 = cardView1.findViewById<TextView>(R.id.ContentText)
        val Detail1 = cardView1.findViewById<TextView>(R.id.DetailText)
        Content1.text="촬영하기"
        Detail1.text="음식들의 탄소배출량을 알 수 있습니다."

        val Content2 = cardView2.findViewById<TextView>(R.id.ContentText)
        val Detail2 = cardView2.findViewById<TextView>(R.id.DetailText)
        Content2.text="하루 탄소발자국"
        Detail2.text="오늘의 탄소발자국은?"

        val Content3 = cardView3.findViewById<TextView>(R.id.ContentText)
        val Detail3 = cardView3.findViewById<TextView>(R.id.DetailText)
        Content3.text="랭킹"
        Detail3.text="다른 사람들과 탄소발자국을 비교해보세요!"

        val Content4 = cardView4.findViewById<TextView>(R.id.ContentText)
        val Detail4 = cardView4.findViewById<TextView>(R.id.DetailText)
        Content4.text="커뮤니티"
        Detail4.text="소통의 장"

        /*val Content4 = cardView4.findViewById<TextView>(R.id.ContentText)
        //val Detail4 = cardView1.findViewById<TextView>(R.id.DetailText)
        Content4.text="마이페이지"*/

        val btn= mutableListOf<View>()
        btn.add(findViewById(R.id.card1))
        btn.add(findViewById(R.id.card2))
        btn.add(findViewById(R.id.card3))
        btn.add(findViewById(R.id.card4))

        // 메뉴 layout 형성 ----------------------

        // 카메라 연동 ----------------------------
        var filePath=""
        requestCameraFileLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            result-> if (result.resultCode != Activity.RESULT_OK) {
                val backIntent=Intent(this,MenuActivity::class.java)
                startActivity(backIntent)
            }else{
                val intent=Intent(this,CameraActivity::class.java)
                intent.putExtra("photoPath",filePath)
                startActivity(intent)
            }
        }
        // 카메라 연동 ----------------------------


        btn[0].setOnClickListener {//사진찍는 btn

            btnEffect(btn[0])

            val timeStamp:String=SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File?=getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file=File.createTempFile(
                "JPEG_${timeStamp}_",".jpg",storageDir)
            filePath=file.absolutePath
            Log.d("filePath is : ",filePath)

            val photoURI: Uri =FileProvider.getUriForFile(
                this,"com.example.ptype1.fileprovider",file
            )
            val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
            requestCameraFileLauncher.launch(intent)

            //val intent=Intent(this,CameraActivity::class.java)
            //startActivity(intent)
        }


        btn[1].setOnClickListener {//오늘 먹은 음식들 확인

            btnEffect(btn[1])
            val intent=Intent(this,TodayFoodActivity::class.java)
            startActivity(intent)
        }

        btn[2].setOnClickListener {//랭킹 확인
            btnEffect(btn[2])
            val intent=Intent(this,RankingActivity::class.java)
            startActivity(intent)
        }

        btn[3].setOnClickListener {//랭킹 확인
            btnEffect(btn[3])
            val intent=Intent(this,CommunityActivity::class.java)
            startActivity(intent)
        }

        //마이페이지 버튼
        val MyPageBtn=findViewById<Button>(R.id.mypagebtn)

        MyPageBtn.setOnClickListener {
            val intent=Intent(this,MyPageActivity::class.java)
            startActivity(intent)
        }


    }

    //뒤로가기 버튼을 두번 누르면 종료하게끔
    override fun onKeyDown(keyCode:Int, event: KeyEvent?):Boolean{
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