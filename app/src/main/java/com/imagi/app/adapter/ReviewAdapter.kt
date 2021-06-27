package com.imagi.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.R
import com.imagi.app.model.Product
import com.imagi.app.model.Review
import kotlinx.android.synthetic.main.item_product.view.*
import kotlinx.android.synthetic.main.item_review.view.*

class ReviewAdapter(val list: List<Review>, private var listerner: (Product)-> Unit) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    class ViewHolder(view:View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context,).inflate(R.layout.item_review, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.itemView.vc_customer_name.text = item.nama_customer
        holder.itemView.vc_comment.text = item.review
        if(item.balasan == null){
            holder.itemView.vc_replay_bound.visibility = View.GONE
        }else{
            holder.itemView.vc_replay.text = item.balasan.balasan
            holder.itemView.vc_seller_name.text = item.balasan.nama_pedagang
            holder.itemView.vc_replay_bound.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}