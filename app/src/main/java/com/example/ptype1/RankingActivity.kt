package com.example.ptype1

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RankingActivity : AppCompatActivity() {

    private val items= mutableListOf<RankingData>()
    //private lateinit var retrofit: Retrofit
    //private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)
        getSupportActionBar()?.setTitle("랭킹")//appbar 형성
        //items add 필요

        //val myName=MyApp.prefs.getString("userEmail",null)
        //val myScroe= MyApp.prefs.getString("userName",null)


            //val userScoreData=userResponse[i]
        items.add(RankingData(1,"윤성준",150,1))
        items.add(RankingData(2,"송민석",149,2))
        items.add(RankingData(3,"배준영",148,3))
        items.add(RankingData(4,"고승민",147,4))
        items.add(RankingData(5,"김도원",146,5))
        items.add(RankingData(6,"정준형",145,6))
        items.add(RankingData(7,"문민우",144,7))

        /*val cardView=findViewById<View>(R.id.conLayout)

        val img1=cardView.findViewById<ImageView>(R.id.isking)
        val img2=cardView.findViewById<ImageView>(R.id.isking)
        val img3=cardView.findViewById<ImageView>(R.id.isking)
        val img4=cardView.findViewById<ImageView>(R.id.isking)

        img1.setImageResource(R.drawable.white)
        img2.setImageResource(R.drawable.white)
        img3.setImageResource(R.drawable.white)
        img4.setImageResource(R.drawable.white)*/

        //Log.d("itemmmmmmm is",userScoreData.toString())




        val recyclerview=findViewById<RecyclerView>(R.id.Rank_View)
        val rvAdapter=RankingAdapter(baseContext,items)
        recyclerview.adapter=rvAdapter
        //rvAdapter.updateData(items)
        recyclerview.layoutManager=LinearLayoutManager(this@RankingActivity)
        //retrofit = Retrofit.Builder().baseUrl("http://ec2-13-125-13-127.ap-northeast-2.compute.amazonaws.com/")
        //    .addConverterFactory(GsonConverterFactory.create()).build()
        //apiService=retrofit.create(ApiService::class.java)

        //val server=apiService.getScoreRequest()
        /*server.enqueue(object : Callback<List<ScoreDTO>> {
            override fun onResponse(call: Call<List<ScoreDTO>>, response: Response<List<ScoreDTO>>) {
                if(response.isSuccessful) {
                    Log.d("Scoreeeeeeeee", response.body().toString())

                    val userResponse=response.body()

                    if (userResponse!= null) {
                        for(i in 0 until userResponse.size){
                            val userScoreData=userResponse[i]
                            items.add(RankingData(i+1,userScoreData.username,userScoreData.score))
                            Log.d("itemmmmmmm is",userScoreData.toString())

                        }

                        val recyclerview=findViewById<RecyclerView>(R.id.Rank_View)
                        val rvAdapter=RankingAdapter(baseContext,items)
                        recyclerview.adapter=rvAdapter
                        //rvAdapter.updateData(items)
                        recyclerview.layoutManager=LinearLayoutManager(this@RankingActivity)




                    }



                }else{
                    val errorBody = response.errorBody()?.string()
                    Log.d("whyloginerror","$errorBody")
                    Log.d("errorCode","$response.code()")
                }
            }

            override fun onFailure(call: Call<List<ScoreDTO>>, t: Throwable) {
                Toast.makeText(this@RankingActivity,"unknownError", Toast.LENGTH_LONG).show()
                Log.d("NetworkError", "네트워크 오류: ${t.message}")
            }

        })*/


    }


}