package com.example.ptype1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RankingAdapter(val context: Context, val List: MutableList<RankingData>) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {
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

            rv_score.text= item.point.toString()
            rv_name.text=item.name



            if (item.getImgres <= 3) {

                // 배경 색상을 변경🥇🥈🥉
                when (item.getImgres) {

                }

                // 나머지 내용
                val params = itemView.layoutParams as ViewGroup.LayoutParams
                params.height = 200 // 새로운 높이 설정
                itemView.layoutParams = params

                rv_rank.textSize=15f
                rv_name.textSize = 20f // 글씨 크기를 24sp로 설정, 필요한 크기로 변경
                rv_score.textSize = 20f

                // 글씨 색상 변경
                rv_name.setTextColor(ContextCompat.getColor(itemView.context, R.color.green2))
                rv_score.setTextColor(ContextCompat.getColor(itemView.context, R.color.green2))
            }
            else{
                rv_rank.text= item.rank.toString()
            }


        }
    }
    fun updateData(newItems: List<RankingData>) {
        List.clear()
        List.addAll(newItems)
        notifyDataSetChanged()
    }
}