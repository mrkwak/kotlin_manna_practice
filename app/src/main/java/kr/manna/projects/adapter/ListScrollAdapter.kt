package kr.manna.projects.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.manna.projects.R
import kr.manna.projects.entity.ItemList

class ListScrollAdapter (private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeItem = 0
    private val viewTypeLoading = 1

    var data = mutableListOf<ItemList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            viewTypeItem -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_rv,parent,false)
                return ItemViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.loading_rv,parent,false)
                return LoadingViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if ( holder is ItemViewHolder){
            holder.bind(data[position])
        }

    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val txtName: TextView = view.findViewById(R.id.itemName)
        private val txtPrice: TextView = view.findViewById(R.id.itemPrice)
        private val txtNo: TextView = view.findViewById(R.id.itemNo)
        private val itemImage: ImageView = view.findViewById(R.id.itemImage)

        fun bind(item: ItemList) {
            txtName.text = item.name
            txtPrice.text = item.price.toString()
            txtNo.text = item.no.toString()
            Glide.with(itemView).load(item.img).into(itemImage)

        }
    }

    inner class LoadingViewHolder(rvView: View): RecyclerView.ViewHolder(rvView)


    // 뷰의 타입을 정해주는 곳이다.
    override fun getItemViewType(position: Int): Int {
        // 게시물과 프로그레스바 아이템뷰를 구분할 기준이 필요하다.
        return when (data[position].positionLoading) {
            true -> viewTypeLoading
            else -> viewTypeItem
        }
    }

    fun deleteLoading(position : Int) {
        when (data[position].positionLoading) {
            true -> data.removeAt(data.lastIndex)
        }

    }

}