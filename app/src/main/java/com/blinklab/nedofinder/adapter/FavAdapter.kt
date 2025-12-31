package com.blinklab.nedofinder.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.blinklab.nedofinder.dataclass.FavDataClass
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.activities.ViewShopActivity
import com.blinklab.nedofinder.dataclass.AddShopDataClass
import de.hdodenhof.circleimageview.CircleImageView

class FavAdapter (private val mList: ArrayList<AddShopDataClass>)
    : RecyclerView.Adapter<FavAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fav_design_file, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val todo= mList[position]
        holder.imageViewFav.load(todo.shopImage)
        holder.profile.load(todo.ownerImage)
        holder.nameFAv.text = todo.ownerName
        holder.categoryFav.text = todo.category
         holder.itemView.setOnClickListener {
    val context = holder.itemView.context

    val shopId = todo._shopKey ?: todo.shopId
    val uid = todo._uidKey ?: todo.ownerId

    if (shopId.isNullOrBlank() || uid.isNullOrBlank()) {
        Toast.makeText(context, "Shop id missing", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
    }

    val intent = Intent(context, ViewShopActivity::class.java)
        .putExtra("shopId", shopId)
        .putExtra("uid", uid)

    context.startActivity(intent)
}

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewFav= itemView.findViewById<ImageView>(R.id.fav_store_img)
        val profile = itemView.findViewById<CircleImageView>(R.id.fav_profile_img)
        val nameFAv = itemView.findViewById<TextView>(R.id.fav_store_name)
        val categoryFav = itemView.findViewById<TextView>(R.id.fav_store_category)
        val reviews = itemView.findViewById<TextView>(R.id.fav_shop_review)


    }
}

