package com.example.ptype1

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MyPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

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
}