package com.example.ptype1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RankingAdapter (val context: Context, val List:MutableList<RankingData>):RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingAdapter.ViewHolder {
        val v =LayoutInflater.from(parent.context).inflate(R.layout.nontop_rank_item,parent,false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return List.size
    }

    override fun onBindViewHolder(holder: RankingAdapter.ViewHolder, position: Int) {
        holder.bindItems(List[position])
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item : RankingData){
            val rv_rank=itemView.findViewById<ImageView>(R.id.non_Rank)
            val rv_name=itemView.findViewById<TextView>(R.id.non_nameText)
            val rv_score=itemView.findViewById<TextView>(R.id.non_rankText)

        }

    }

}