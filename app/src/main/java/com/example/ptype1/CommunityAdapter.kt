package com.example.ptype1

import android.content.ClipData
import android.content.Context
import android.content.Intent
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

        var click_item=itemView.findViewById<CardView>(R.id.card_community)

        init {

            //item LongClick Listener
            click_item.setOnClickListener {
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

            title.text=item.title
            writer.text=item.writer

        }
    }
}