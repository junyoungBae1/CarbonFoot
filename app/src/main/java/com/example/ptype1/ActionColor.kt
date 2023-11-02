package com.example.ptype1


import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView

class CustomTextView(context: Context) : androidx.appcompat.widget.AppCompatTextView(context) {
    init {
        // 텍스트 색상을 흰색으로 설정
        setTextColor(Color.WHITE)
    }
}