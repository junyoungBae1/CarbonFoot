package com.example.ptype1

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout

class DrawerActivity :AppCompatActivity() {
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var drawer : DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_drawer)
        super.onCreate(savedInstanceState)

        drawer=findViewById(R.id.drawer)

        toggle= ActionBarDrawerToggle(this,drawer,R.string.open,R.string.close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}