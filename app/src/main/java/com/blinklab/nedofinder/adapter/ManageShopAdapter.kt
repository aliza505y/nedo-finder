package com.blinklab.nedofinder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.dataclass.AddShopDataClass
import com.blinklab.nedofinder.dataclass.ManageDataClass
import com.makeramen.roundedimageview.RoundedImageView

class ManageShopAdapter(private val arrayList: ArrayList<AddShopDataClass>) :
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
        holder.shopImage.load(todo.shopImage)
        holder.shopName.text = todo.shopName
        holder.status.text = todo.status
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shopImage: RoundedImageView = itemView.findViewById(R.id.manage_shop_img)
        val shopName: TextView = itemView.findViewById(R.id.manage_shop_name)
        val status: TextView = itemView.findViewById(R.id.shop_status)
    }
}

