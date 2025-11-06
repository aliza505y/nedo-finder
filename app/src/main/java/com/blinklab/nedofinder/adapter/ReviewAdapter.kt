package com.blinklab.nedofinder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.blinklab.nedofinder.R
import androidx.recyclerview.widget.RecyclerView
import com.blinklab.nedofinder.dataclass.ReviewDataClass
import de.hdodenhof.circleimageview.CircleImageView


class ReviewAdapter (private val reviewList: List<ReviewDataClass>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_rv_design, parent, false)
        return ReviewViewHolder(view)

    }

    override fun onBindViewHolder(
        holder: ReviewViewHolder,
        position: Int
    ) {
        val review = reviewList[position]
        holder.user_img.setImageResource(review.user_imge)
        holder.user_name.text = review.user_namede
        holder.user_review_time.text = review.user_review_timede
        holder.user_text_review.text = review.user_text_reviwede

    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val user_img: CircleImageView = itemView.findViewById(R.id.user_img)
        val user_name: TextView = itemView.findViewById(R.id.user_name)
        val user_review_time: TextView = itemView.findViewById(R.id.user_review_time)
        val user_text_review: TextView = itemView.findViewById(R.id.user_text_review)


    }

    }