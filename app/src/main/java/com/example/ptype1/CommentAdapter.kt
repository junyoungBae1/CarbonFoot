package com.example.ptype1

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommentAdapter(val context: Context, val List:MutableList<CommentData>,private val listener: OnItemDeleteClickListener) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        holder.bindItems(List[position])
    }

    override fun getItemCount(): Int {
        return List.size
    }


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var  cancelBtn  = itemView.findViewById<TextView>(R.id.cancelBtn)
        var del_noticeToken : String? = null
        var del_commentId : String? =null

        init {
            //item LongClick Listener

            cancelBtn.setOnClickListener {

                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    Log.d("positionis",del_noticeToken!!)
                    if(del_noticeToken!=null){
                        listener.onItemDelete(del_noticeToken, del_commentId)
                    }

                }
            }
        }

        fun bindItems(item: CommentData) {

            val writer = itemView.findViewById<TextView>(R.id.comment_Writer)
            val comment = itemView.findViewById<TextView>(R.id.community_comment)
            val date = itemView.findViewById<TextView>(R.id.comment_Date)
            Log.d("isWriteris",item.isWriter.toString())

            if(item.isWriter){

                cancelBtn.visibility = View.VISIBLE
                del_noticeToken= item.noticeToken
                del_commentId=item.commentId

                Log.d("DeleteNotice",del_noticeToken.toString())

            }else{
                cancelBtn.visibility = View.GONE

            }

            writer.text=item.writer
            comment.text=item.comment
            date.text=item.date
        }


    }

    fun ClearData(List:MutableList<CommentData>){
        notifyDataSetChanged()
    }

    interface OnItemDeleteClickListener {
        fun onItemDelete(noticeToken: String?, commentId: String?)
    }



}