package com.example.ptype1

import android.app.Application

class MyApp: Application() {
    companion object {
        lateinit var prefs: SharedPref
    }

    override fun onCreate() {
        prefs = SharedPref(applicationContext)
        super.onCreate()
    }
}