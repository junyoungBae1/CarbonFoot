package com.example.ptype1

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MyPageActivity : AppCompatActivity() {

    private lateinit var logoutBtn : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val handler = android.os.Handler()
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE)

        val sharedPref=getSharedPreferences("prefName", Activity.MODE_PRIVATE)
        val jwt_token=sharedPref.getString("jwt_token",null)

        if (jwt_token!=null){
            val userName=intent.getStringExtra("username")
            val userEmail=intent.getStringExtra("useremail")
            val userPhone=intent.getStringExtra("userphone")
            //val userPw=intent.getStringExtra("userpassword")

            val MyPageUser=findViewById<TextView>(R.id.nameMyPage)
            val MyPageEmail=findViewById<TextView>(R.id.idMyPage)
            val MyPagePhone=findViewById<TextView>(R.id.phoneMyPage)

            MyPageUser.text=userName
            MyPageEmail.text=userEmail
            MyPagePhone.text=userPhone
        }


        val defaultColor = Color.parseColor("#4CAF50") //버튼 누르면 글짜 색 바뀌게끔
        logoutBtn.setOnClickListener {//등록 버튼 동작

            var newColor= Color.parseColor("#2C9430")
            logoutBtn.setTextColor(newColor)

            handler.postDelayed({
                logoutBtn.setTextColor(defaultColor)
            }, 300) //버튼 누르면 글 색 바뀜

            val builder=AlertDialog.Builder(this)
            builder.setTitle("alarm").setMessage("로그아웃 하시겠습니까?").
                    setPositiveButton("YES",DialogInterface.OnClickListener{ dialogInterface: DialogInterface, i: Int ->
                     Toast.makeText(this,"로그아웃 되었습니다",Toast.LENGTH_LONG).show()

                        val editor = sharedPreferences.edit()
                        editor.remove("jwt_token")
                        editor.apply()

                        val intent= Intent(this,MainActivity::class.java)
                        startActivity(intent)

                    }).setNegativeButton("NO",DialogInterface.OnClickListener { dialogInterface, i ->  })

            builder.create()
            builder.show()


        }

    }
}