package com.example.ptype1

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ptype1.ComCheckActivity
import org.w3c.dom.Text

class CommunityAdapter(val context: Context, val List:MutableList<CommunityData>) : RecyclerView.Adapter<CommunityAdapter.ViewHolder>() {

    val handler = android.os.Handler() //handler 및 btn 효과 변수 초기화
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.community_item,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CommunityAdapter.ViewHolder, position: Int) {
        holder.bindItems(List[position])
    }

    override fun getItemCount(): Int {
        return List.size
    }


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var click_item=itemView.findViewById<View>(R.id.card_community)

        init {
            //item LongClick Listener
            click_item.setOnClickListener {

                btnEffect(click_item)
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val itemm = List[position]
                    val intent = Intent(itemView.context, ComCheckActivity::class.java)
                    intent.putExtra("content", itemm.content)
                    intent.putExtra("noticeToken", itemm.noticeToken)
                    intent.putExtra("date", itemm.date)
                    intent.putExtra("userEmail", itemm.userEmail)
                    intent.putExtra("title",itemm.title)
                    intent.putExtra("writer",itemm.writer)

                    itemView.context.startActivity(intent)
                }
            }

        }

        fun bindItems(item : CommunityData){

            val title = itemView.findViewById<TextView>(R.id.community_Text)
            val writer = itemView.findViewById<TextView>(R.id.community_Writer)
            val content=itemView.findViewById<TextView>(R.id.community_Content)
            val date=itemView.findViewById<TextView>(R.id.community_Date)

            title.text=item.title
            writer.text=item.writer
            content.text=item.content
            date.text=item.date

        }
    }

    private fun btnEffect(btn: View) {
        val defaultDrawable = btn.background // 현재 Drawable을 저장

        val newColor = Color.parseColor("#D1D0D0")
        btn.setBackgroundColor(newColor)

        handler.postDelayed({
            // 이전 Drawable로 복원
            btn.background = defaultDrawable
        }, 300)
    }
}