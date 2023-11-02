package com.example.ptype1

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SharedPref(context: Context) {

    private val prefs:SharedPreferences=
        context.getSharedPreferences("prefName",Activity.MODE_PRIVATE)

    fun getString(key: String, defValue: String?): String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String?) {
        prefs.edit().putString(key, str).apply()
    }

    fun getNum(key: String, defValue: Number): Int {
        return prefs.getInt(key, defValue as Int)
    }

    fun setNum(key: String, num : Number) {
        prefs.edit().putInt(key, num as Int).apply()
    }

    fun removeString(key: String ) {
        prefs.edit().remove(key).apply()
    }

    fun containsString(key:String) :Boolean{
        return prefs.contains(key)
    }





}