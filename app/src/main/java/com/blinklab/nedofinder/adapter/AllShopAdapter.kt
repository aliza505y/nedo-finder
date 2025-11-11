package com.blinklab.nedofinder.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blinklab.nedofinder.dataclass.AllShopDataClass
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.activities.ViewShopActivity
import de.hdodenhof.circleimageview.CircleImageView

class AllShopAdapter(private val allList: ArrayList<AllShopDataClass>) :
    RecyclerView.Adapter<AllShopAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewAll = itemView.findViewById<ImageView>(R.id.shop_image)
        val nameAll = itemView.findViewById<TextView>(R.id.shop_name)
        val categoryAll = itemView.findViewById<TextView>(R.id.shop_category)
        val addressAll = itemView.findViewById<TextView>(R.id.shop_address)
        val pImageAll = itemView.findViewById<CircleImageView>(R.id.owner_image)
        val imageReview = itemView.findViewById<TextView>(R.id.shop_rating)
/*
        val shopViewBtn = itemView.findViewById<AppCompatButton>(R.id.all_shopView_btn)
*/
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.all_shop_design_file, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo= allList[position]
        holder.imageViewAll.setImageResource(todo.image)
        holder.pImageAll.setImageResource(todo.pImage)
        holder.nameAll.text = todo.name
        holder.categoryAll.text = todo.category
        holder.addressAll.text = todo.address
        holder.imageReview.text=todo.review
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ViewShopActivity::class.java)
            context.startActivity(intent)
        }

    }



 }