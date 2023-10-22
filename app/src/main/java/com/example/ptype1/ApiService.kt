package com.example.ptype1

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*

data class ResponseDTO(
    @SerializedName("user")
    var user : UserData,
    @SerializedName("token")
    var token : String? =null,
    @SerializedName("userList")
    var userList:MutableList<UserData>? =null
)

data class ScoreDTO(
    @SerializedName("username")
    var username : String,
    @SerializedName("score")
    var score : Number,
)

data class FoodDTO(
    @SerializedName("totalEmission")
    var totalEmission : Number
)

interface ApiService {

    @GET("/retrofit/get")
    fun getRequest(@Query("name") name: String): Call<ResponseDTO>

    @GET("/score/getScore")
    fun getScoreRequest(

    ) :Call<List<ScoreDTO>>

    @POST("user/logout")
    @Headers("Content-Type: application/json")
    fun PostLogoutRequest(
        @Header("Authorization") token: String
    ): Call<Void>

    //FormData
    //UrlEncoded
    @FormUrlEncoded
    @POST("user/login")
    fun postLoginRequest(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<ResponseDTO>

    //FormData
    //UrlEncoded
    //회원가입
    @FormUrlEncoded
    @POST("user/register")
    fun postRegisterRequest(
        @Field("username") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phonenum") phonenum: String
    ): Call<ResponseDTO>

    @FormUrlEncoded
    @PUT("/retrofit/put/{id}")
    fun putRequest(
        @Path("id") id: String,
        @Field("content") content: String
    ): Call<ResponseDTO>

    @FormUrlEncoded
    @POST("calculator/calculateEmission")
    fun postFoodRequest(
        @Field("foodname") foodname: String
    ): Call<FoodDTO>

    @DELETE("/retrofit/delete/{id}")
    fun deleteRequest(@Path("id") id: String): Call<ResponseDTO>
}