package com.example.ptype1

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import com.example.ptype1.ApiService
import com.example.ptype1.R

import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptype1.checkingDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ComCheckActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService

    var noticeToken : String? = null


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (intent.getStringExtra("writer")==MyApp.prefs.getString("userName",null)) {
            menuInflater.inflate(R.menu.appbar_btn, menu)
        }

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_textcheck)
        getSupportActionBar()?.setTitle("자유게시판")


        val checkTitle=findViewById<TextView>(R.id.checkTextTitle)
        val checkContent=findViewById<TextView>(R.id.checkContentText)
        val checkWriter=findViewById<TextView>(R.id.checkTextWriter)
        val checkDate=findViewById<TextView>(R.id.checkTextDate)

        val content=intent.getStringExtra("content")
        val date=intent.getStringExtra("date")
        val title=intent.getStringExtra("title")
        val writer=intent.getStringExtra("writer")

        noticeToken= intent.getStringExtra("noticeToken")
        val useremail= intent.getStringExtra("userEmail")

        //modifybtn= findViewById(R.id.modifyTextBtn)
        //deletebtn= findViewById(R.id.removeTextBtn)
        //UpdateAndDelete=findViewById(R.id.UpdateAndDelete)
        //UpdateAndDelete.visibility = View.GONE



        checkTitle.text=title
        checkContent.text=content
        checkWriter.text=writer
        checkDate.text=date

        retrofit = Retrofit.Builder() //retrofit 정의
            .baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService=retrofit.create(ApiService::class.java)

        if(noticeToken!=null && useremail!=null){
            val server=apiService.postGetBoard(noticeToken!!,useremail)
            Log.d("noticeTokenCheck",noticeToken!!)

            server.enqueue(object : Callback<checkingDTO> {
                override fun onResponse(call: Call<checkingDTO>, response: Response<checkingDTO>) {
                    if(response.isSuccessful) {
                        val responseBody= response.body()

                        Log.d("responseBodyyyis", response.body().toString())


                        if(responseBody!!.data.matchResult==1) {
                            if(checkWriter.text==MyApp.prefs.getString("userName",null)){
                                checkWriter.text= writer+" (나의 글) "
                            }
                             //UpdateAndDelete.visibility = View.VISIBLE
                        }


                    }else{
                        val errorBody = response.errorBody()?.string()
                        Log.d("whyGetBoardError","$errorBody")
                        Log.d("errorCode","$response.code()")
                    }
                }

                override fun onFailure(call: Call<checkingDTO>, t: Throwable) {
                    var errorMessage = "Unknown Error"
                    Toast.makeText(this@ComCheckActivity, errorMessage, Toast.LENGTH_LONG).show()
                    Log.d("NetworkError", "네트워크 오류: ${t.message}")
                }

            })

            /*modifybtn.setOnClickListener {
                val intent = Intent(this, ComWriteActivity::class.java)
                intent.putExtra("UpdateTrigger","update")
                intent.putExtra("UpdateToken",noticeToken)

                startActivity(intent)
            }

            deletebtn.setOnClickListener {
                val builder= AlertDialog.Builder(this)
                builder.setTitle("alarm").setMessage("삭제하시겠습니까?").
                setPositiveButton("YES",
                    DialogInterface.OnClickListener{ dialogInterface: DialogInterface, i: Int ->

                        val server= apiService.postDelete(noticeToken)
                        Log.d("tokennnnn?",noticeToken)


                        server.enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if(response.isSuccessful) {
                                    Log.d("Deleteeee?",response.body().toString())
                                    Toast.makeText(this@ComCheckActivity,"게시글을 삭제하였습니다",Toast.LENGTH_LONG).show()
                                    val intent=Intent(this@ComCheckActivity,CommunityActivity::class.java)
                                    startActivity(intent)

                                }else{
                                    val errorBody = response.errorBody()?.string()
                                    Log.d("whyGetBoardError","$errorBody")
                                    Log.d("errorCode","$response.code()")
                                }
                            }
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                var errorMessage = "Unknown Error"
                                Toast.makeText(this@ComCheckActivity, errorMessage, Toast.LENGTH_LONG).show()
                                Log.d("NetworkError", "네트워크 오류: ${t.message}")
                            }
                        })

                    }).setNegativeButton("NO", DialogInterface.OnClickListener { dialogInterface, i ->  })
                builder.create()
                builder.show()
            }*/


        }

    }



    override fun onKeyDown(keyCode:Int, event: KeyEvent?):Boolean{
        if(keyCode== KeyEvent.KEYCODE_BACK){
            val intent = Intent(this, CommunityActivity::class.java) // TargetActivity로 이동하려는 Intent 생성
            startActivity(intent) // Intent를 시작하여 TargetActivity로 이동
            finish() // 현재 액티비티를 종료하고 이전 액티비티로 돌아감
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(noticeToken!=null){
            when (item.itemId) {
                R.id.rewrite_btn -> {
                    val intent = Intent(this, ComWriteActivity::class.java)
                    intent.putExtra("UpdateTrigger","update")
                    intent.putExtra("UpdateToken",noticeToken)

                    startActivity(intent)
                    return true
                }
                R.id.delete_btn -> {
                    enqueueRewriteRequest()
                    return true
                }
                else -> return super.onOptionsItemSelected(item)
            }
        }
        return false
    }

    fun enqueueRewriteRequest(){
        val builder= AlertDialog.Builder(this)
        builder.setTitle("alarm").setMessage("삭제하시겠습니까?").
        setPositiveButton("YES",
            DialogInterface.OnClickListener{ dialogInterface: DialogInterface, i: Int ->

                val server= apiService.postDelete(noticeToken!!)


                server.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.isSuccessful) {
                            Log.d("Deleteeee?",response.body().toString())
                            Toast.makeText(this@ComCheckActivity,"게시글을 삭제하였습니다",Toast.LENGTH_LONG).show()
                            val intent=Intent(this@ComCheckActivity,CommunityActivity::class.java)
                            startActivity(intent)

                        }else{
                            val errorBody = response.errorBody()?.string()
                            Log.d("whyGetBoardError","$errorBody")
                            Log.d("errorCode","$response.code()")
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        var errorMessage = "Unknown Error"
                        Toast.makeText(this@ComCheckActivity, errorMessage, Toast.LENGTH_LONG).show()
                        Log.d("NetworkError", "네트워크 오류: ${t.message}")
                    }
                })

            }).setNegativeButton("NO", DialogInterface.OnClickListener { dialogInterface, i ->  })
        builder.create()
        builder.show()
    }
}