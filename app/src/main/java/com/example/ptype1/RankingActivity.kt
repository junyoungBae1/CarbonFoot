package com.example.ptype1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RankingActivity : AppCompatActivity() {

    private val items= mutableListOf<RankingData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)
        //items add 필요

        val recyclerview : RecyclerView=findViewById(R.id.Rankview)
        val rvAdapter=RankingAdapter(baseContext,items)
        recyclerview.adapter=rvAdapter
        recyclerview.layoutManager=LinearLayoutManager(this)

    }

}