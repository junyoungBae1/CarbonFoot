package com.example.ptype1

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.Date

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

data class noticeDTO(
    @SerializedName("data")
    var data : MutableList<CommunityData>
)

data class checkingDTO(
    @SerializedName("matchResult")
    var match : Int
)

data class ImageDTO(
    @SerializedName("images_data")
    var images_data : ImageData
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


    @Multipart
    @POST("image/upload")
    fun postFoodCo2Request(
        @Part("email") email: RequestBody,
        @Part("foodname") foodname: RequestBody,
        @Part("totalEmission") totalEmission: RequestBody,
        @Part("etc") etc: RequestBody,
        @Part img: MultipartBody.Part
    ): Call<Void>

    @FormUrlEncoded
    @POST("image/find")
    fun postDateRequest(
        @Field("email") email:String,
        @Field("date") date:String
    ):Call<ImageDTO>

    @FormUrlEncoded
    @POST("noticeBoard/getBoard")
    fun postGetBoard(
        @Field("noticetoken") token : String,
        @Field("userEmail") email : String
    ):Call<checkingDTO>

    @FormUrlEncoded
    @POST("noticeBoard/create")
    fun postCreate(
        @Field("title") title : String,
        @Field("content") content : String,
        @Field("writer") writer : String,
        @Field("userEmail") email : String
    ):Call<Void>

    @GET("/noticeBoard/read")
    fun getRead(

    ) :Call<noticeDTO>




}