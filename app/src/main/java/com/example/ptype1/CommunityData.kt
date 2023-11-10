package com.example.ptype1

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CommunityData(val noticeToken: String?,
                         val title: String?,
                         val content: String?,
                         val writer: String?,
                         val userEmail:String?,
                         val date : String?,
                         val unknown : Int =0,
                         val comments : MutableList<CommentData>,
                         val matchResult : Int)

data class CommentData(
    @SerializedName("userEmail")
    val userEmail:String?=null,
    @SerializedName("writer")
    val writer: String?=null,
    @SerializedName("content")
    val comment: String?=null,
    @SerializedName("date")
    val date : String?=null,
    @SerializedName("_id")
    val commentId : String?=null,
    @SerializedName("isWriter")
    val isWriter : Boolean,
    var noticeToken : String? =null
)



