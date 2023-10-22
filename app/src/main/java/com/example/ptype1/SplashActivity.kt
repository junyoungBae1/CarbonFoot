package com.example.ptype1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if(isLoggedIn()) {
                startActivity(Intent(this, MenuActivity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }, 2000)
        
        

    }

    private fun isLoggedIn(): Boolean {
        return MyApp.prefs.containsString("jwt_token")

    }
}