package com.example.ptype1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TodayFoodAdapter(val context: Context, val List:MutableList<TodayFoodData>) : RecyclerView.Adapter<TodayFoodAdapter.ViewHolder>() {
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
        fun bindItems(item : TodayFoodData){
            val img=itemView.findViewById<ImageView>(R.id.ImgOfFoodList)
            val text_food=itemView.findViewById<TextView>(R.id.NameOfFoodList)
            val text_emission=itemView.findViewById<TextView>(R.id.Co2OfFoodList)
            val text_time=itemView.findViewById<TextView>(R.id.TimeOfFoodList)

            Glide.with(context).load(item.img).into(img)
            text_food.text=item.foodname
            text_emission.text=item.emission
            text_time.text=item.date
            //Glide.with(context).load(item.Imageurl).into(rv_img)

        }
    }
}