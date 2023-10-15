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
import android.widget.Toast

class MenuActivity: AppCompatActivity()  {
    private lateinit var requestCameraFileLauncher : ActivityResultLauncher<Intent> //카메라 런쳐 미리 선언
    var initTime =0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        //val userName=intent.getStringExtra("username")
        //getSupportActionBar()?.setTitle(userName+"님")

        val cardView1=findViewById<View>(R.id.card1)
        val cardView2=findViewById<View>(R.id.card2)
        val cardView3=findViewById<View>(R.id.card3)

        val img1=cardView1.findViewById<ImageView>(R.id.MenuImage)
        img1.setImageResource(R.drawable.cameraimg)

        val img2=cardView2.findViewById<ImageView>(R.id.MenuImage)
        img2.setImageResource(R.drawable.checkfootimg)

        val img3=cardView3.findViewById<ImageView>(R.id.MenuImage)
        img3.setImageResource(R.drawable.comimg)


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

        /*val Content4 = cardView4.findViewById<TextView>(R.id.ContentText)
        //val Detail4 = cardView1.findViewById<TextView>(R.id.DetailText)
        Content4.text="마이페이지"*/

        val btn= mutableListOf<View>()
        btn.add(findViewById(R.id.card1))
        btn.add(findViewById(R.id.card2))
        btn.add(findViewById(R.id.card3))
        //btn.add(findViewById(R.id.card4))
        val defaultColor = Color.parseColor("#FFFFFF")
        val handler = android.os.Handler()

        val requestCode=1000
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

            /*val option=BitmapFactory.Options()
            option.inSampleSize=20
            val bitmap=BitmapFactory.decodeFile(filePath,option)
            bitmap?.let{
                resultImage.setImageBitmap(bitmap)
            }*/

        }

        btn[0].setOnClickListener {

            var newColor= Color.parseColor("#E1DFDF")
            btn[0].setBackgroundColor(newColor)

            handler.postDelayed({
                btn[0].setBackgroundColor(defaultColor)
            }, 300) //

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



        btn[1].setOnClickListener {

            var newColor= Color.parseColor("#D1D0D0")
            btn[1].setBackgroundColor(newColor)

            handler.postDelayed({
                btn[1].setBackgroundColor(defaultColor)
            }, 300) //


            //val intent=Intent(this,CalendarActivity::class.java)
            //startActivity(intent)
        }

        btn[2].setOnClickListener {
            var newColor= Color.parseColor("#D1D0D0")
            btn[2].setBackgroundColor(newColor)

            handler.postDelayed({
                btn[2].setBackgroundColor(defaultColor)
            }, 300) //

            val intent=Intent(this,tabActivity::class.java)
            startActivity(intent)
        }


    }

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