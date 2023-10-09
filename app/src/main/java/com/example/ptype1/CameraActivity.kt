package com.example.ptype1

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val photoPath = intent.getStringExtra("photoPath")
        val bitmap=BitmapFactory.decodeFile(photoPath)

        val imgView=findViewById<ImageView>(R.id.cameraResultImg)
        imgView.setImageBitmap(bitmap)
        Glide.with(this).load(photoPath).into(imgView)




    }
}