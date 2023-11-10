
package com.example.ptype1

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TodayFoodAdapter(val context: Context, val List:MutableList<TodayFoodData>) : RecyclerView.Adapter<TodayFoodAdapter.ViewHolder>() {

    val handler = android.os.Handler() //handler 및 btn 효과 변수 초기화
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayFoodAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.myfood_item,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: TodayFoodAdapter.ViewHolder, position: Int) {
        holder.bindItems(List[position])
    }

    override fun getItemCount(): Int {
        return List.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bindItems(item : TodayFoodData) {
            val img = itemView.findViewById<ImageView>(R.id.ImgOfFoodList)
            val text_food = itemView.findViewById<TextView>(R.id.NameOfFoodList)
            val text_emission = itemView.findViewById<TextView>(R.id.Co2OfFoodList)
            val text_time = itemView.findViewById<TextView>(R.id.TimeOfFoodList)
            val text_score=itemView.findViewById<TextView>(R.id.ScoreOfFoodList)

            Glide.with(context).load(item.img).into(img)
            text_food.text = item.foodname
            text_emission.text = item.emission
            text_score.text=item.score.toString()


            var etcText=""
            if(item.etc==0){
                etcText="간식"
            }
            else if(item.etc==1){
                etcText="조식"
            }else if(item.etc==2){
                etcText="중식"
            }else if(item.etc==3){
                etcText="석식"
            }

            text_time.text = item.date+" ("+etcText+")"
            //Glide.with(context).load(item.Imageurl).into(rv_img)
        }
    }


    fun ClearData(List:MutableList<TodayFoodData>){
        notifyDataSetChanged()
    }


}