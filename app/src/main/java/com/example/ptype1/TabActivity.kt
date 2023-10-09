package com.example.ptype1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class TabActivity : AppCompatActivity(){

    protected lateinit var tabLayout: TabLayout
    protected lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        tabLayout=findViewById(R.id.tabs)
        viewPager=findViewById(R.id.viewPager)

        val adapter=MyFragPageAdapter(this)



    }
}