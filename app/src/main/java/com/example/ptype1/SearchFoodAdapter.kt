package com.example.ptype1

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView



class SearchFoodAdapter(val context: Context, val List: MutableList<String>) : RecyclerView.Adapter<SearchFoodAdapter.ViewHolder>() {
    val handler = android.os.Handler() //handler 및 btn 효과 변수 초기화



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFoodAdapter.ViewHolder {
        val v =LayoutInflater.from(parent.context).inflate(R.layout.searchfood_item,parent,false)

        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: SearchFoodAdapter.ViewHolder, position: Int) {
        holder.bindItems(List[position])
    }

    override fun getItemCount(): Int {
        return List.size
    }



    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var click_item=itemView.findViewById<View>(R.id.card_searchfood)
        var searchResult=itemView.findViewById<TextView>(R.id.SearchnameText)

        init {
            //item LongClick Listener
            click_item.setOnClickListener {
                btnEffect(click_item)

                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val intent= Intent(itemView.context,CameraActivity::class.java)
                    intent.putExtra("ModfiedFood",searchResult.text)
                    Log.d("searchResultis", searchResult.text.toString())
                    itemView.context.startActivity(intent)

                }
            }
        }

        fun bindItems(item : String){
            searchResult.text=item
        }
    }

    fun ClearData(List:MutableList<String>){
        List.clear()
        notifyDataSetChanged()
    }


    private fun btnEffect(btn: View) {
        val defaultDrawable = btn.background // 현재 Drawable을 저장

        val newColor = Color.parseColor("#D1D0D0")
        btn.setBackgroundColor(newColor)

        handler.postDelayed({
            // 이전 Drawable로 복원
            btn.background = defaultDrawable
        }, 500)
    }

}


