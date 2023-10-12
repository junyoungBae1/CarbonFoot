package com.example.ptype1

import retrofit2.Call
import retrofit2.http.*

data class ResponseDTO(
    var name: String? = null,
    var email:String? = null,
    var password:String? = null,
    var phonenum: String? = null

)

interface ApiService {

    @GET("/retrofit/get")
    fun getRequest(@Query("name") name: String): Call<ResponseDTO>

    @GET("user/logout")
    fun getLogoutRequest(): Call<Void>

    //FormData
    //UrlEncoded
    @FormUrlEncoded
    @POST("user/login")
    fun postLoginRequest(
        @Field("email") email: String,
        @Field("password") password: String
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

    @DELETE("/retrofit/delete/{id}")
    fun deleteRequest(@Path("id") id: String): Call<ResponseDTO>
}