package com.blinklab.nedofinder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.dataclass.ManageDataClass
import com.makeramen.roundedimageview.RoundedImageView

class ManageShopAdapter(private val arrayList: ArrayList<ManageDataClass>) :
    RecyclerView.Adapter<ManageShopAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.manage_store_design,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val todo = arrayList[position]
        holder.image.setImageResource(todo.image)
        holder.name.text = todo.name
        holder.category.text = todo.category

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: RoundedImageView = itemView.findViewById(R.id.manage_shop_img)
        val name: TextView = itemView.findViewById(R.id.manage_shop_name)
        val category: TextView = itemView.findViewById(R.id.manage_store_category)
        val btn: AppCompatButton = itemView.findViewById(R.id.all_shopView_btn)
    }
}

