package com.example.ptype1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RankingAdapter(val context: Context, val List:MutableList<RankingData>) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingAdapter.ViewHolder {
        val v =LayoutInflater.from(parent.context).inflate(R.layout.rank_item,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RankingAdapter.ViewHolder, position: Int) {
        holder.bindItems(List[position])
    }

    override fun getItemCount(): Int {
        return List.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bindItems(item : RankingData){
            val rv_rank=itemView.findViewById<TextView>(R.id.rankingText)
            val rv_score=itemView.findViewById<TextView>(R.id.scoreText)
            val rv_name=itemView.findViewById<TextView>(R.id.nameText)
            val rv_image=itemView.findViewById<ImageView>(R.id.isking)

            rv_rank.text= item.rank.toString()
            rv_score.text= item.point.toString()
            rv_name.text=item.name

            val rv_image_num=item.getImgres
            if (rv_image_num<=3){
                rv_image.setImageResource(R.drawable.king)
            }else{
                rv_image.setImageResource(R.drawable.white)
            }


        }
    }
    fun updateData(newItems: List<RankingData>) {
        List.clear()
        List.addAll(newItems)
        notifyDataSetChanged()
    }
}