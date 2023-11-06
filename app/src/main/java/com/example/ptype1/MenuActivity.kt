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
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.renderscript.ScriptGroup.Input
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream

class MenuActivity: AppCompatActivity()  {
    private lateinit var requestCameraFileLauncher : ActivityResultLauncher<Intent> //카메라 런쳐 미리 선언
    private lateinit var requestGalleryLauncher : ActivityResultLauncher<Intent>

    private lateinit var CameraView : View
    private lateinit var albumView  : View
    private lateinit var rankView  : View
    private lateinit var communityView : View
    private lateinit var calendarView : View
    private lateinit var myPageView : View
    val handler = android.os.Handler() //handler 및 btn 효과 변수 초기화

    var initTime =0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val userName= MyApp.prefs.getString("userName",null)
        getSupportActionBar()?.setTitle(userName+"님")//appbar 형성


        // 메뉴 layout 형성 ------------------------
        CameraView =findViewById(R.id.CameraView)
        albumView  =findViewById(R.id.UploadView)
        rankView =findViewById(R.id.RankingView)
        communityView =findViewById(R.id.CommunityView)
        calendarView =findViewById(R.id.CalendarView)
        myPageView =findViewById(R.id.MyPageView)

        /*val Content4 = cardView4.findViewById<TextView>(R.id.ContentText)
        //val Detail4 = cardView1.findViewById<TextView>(R.id.DetailText)
        Content4.text="마이페이지"*/

        val btn= mutableListOf<View>()
        btn.add(CameraView )
        btn.add(albumView )
        btn.add(rankView)
        btn.add(communityView)
        btn.add(calendarView)
        btn.add(myPageView)

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
                intent.putExtra("Select","1")
                startActivity(intent)
            }
        }

        // 갤러리 연동 ----------------------------
        requestGalleryLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
                result-> if (result.resultCode != Activity.RESULT_OK) {
                val backIntent=Intent(this,MenuActivity::class.java)
                startActivity(backIntent)
            }else{
                val imageURI=result.data?.data
                if (imageURI!=null){
                    val intent=Intent(this,CameraActivity::class.java)

                    intent.putExtra("photoURI",imageURI.toString())
                    intent.putExtra("Select","2")
                    startActivity(intent)
                }

            }
        }


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

        btn[1].setOnClickListener {//갤러리
            btnEffect(btn[1])
            val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type="image/*"
            requestGalleryLauncher.launch(intent)
        }


        btn[2].setOnClickListener {//랭킹 확인
            btnEffect(btn[2])
            val intent=Intent(this,RankingActivity::class.java)
            startActivity(intent)
        }

        btn[3].setOnClickListener {//커뮤니티 확인
            btnEffect(btn[3])
            val intent=Intent(this,CommunityActivity::class.java)
            startActivity(intent)
        }

        btn[4].setOnClickListener {//오늘 먹은 음식들 확인

            btnEffect(btn[4])
            val intent=Intent(this,TodayFoodActivity::class.java)
            startActivity(intent)
        }

        //마이페이지 버튼
        btn[5].setOnClickListener {//오늘 먹은 음식들 확인

            btnEffect(btn[5])
            val intent=Intent(this,MyPageActivity::class.java)
            startActivity(intent)
        }


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