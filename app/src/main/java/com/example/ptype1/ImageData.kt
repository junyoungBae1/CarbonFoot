package com.example.ptype1

import com.google.gson.annotations.SerializedName

data class ImageData(
    @SerializedName("image_data") val imageData: String?,
    @SerializedName("image_foods") val imageFoods: List<foodData>?
)

data class foodData(
    @SerializedName("foodname") val foodname: String?,
    @SerializedName("totalEmission") val totalEmssion: Double
)
