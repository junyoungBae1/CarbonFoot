package com.example.ptype1

import android.widget.ImageView
import com.google.gson.annotations.SerializedName

data class RankingData(val rank:Int,
                       @SerializedName("username")
                       val username:String,
                       @SerializedName("score")
                       val score:Int ,
                       val getImgres:Int) {
}
