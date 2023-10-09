package com.example.ptype1


import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    var initTime =0L //뒤로가기 버튼 시각
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn=findViewById<Button>(R.id.cirLoginButton)
        val regBtn=findViewById<TextView>(R.id.regButton)
        val handler = android.os.Handler()

        btn.setOnClickListener {

            Toast.makeText(this,"로그인 성공", Toast.LENGTH_LONG).show()

            var intent= Intent(this,MenuActivity::class.java)
            startActivity(intent)
        }


        val defaultColor = Color.parseColor("#4CAF50")
        regBtn.setOnClickListener {

            var newColor= Color.parseColor("#2C9430")
            regBtn.setTextColor(newColor)

            handler.postDelayed({
                regBtn.setBackgroundColor(defaultColor)
            }, 300) //

            var intent=Intent(this,RegisterActivity::class.java)
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