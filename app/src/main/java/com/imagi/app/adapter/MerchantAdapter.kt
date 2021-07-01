package com.imagi.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.R
import com.imagi.app.model.Store
import com.imagi.app.model.User
import kotlinx.android.synthetic.main.item_market.view.*

class MerchantAdapter(val list: List<User>, private var listener: (User) -> Unit) : RecyclerView.Adapter<MerchantAdapter.ViewHolder>() {

    class ViewHolder(view: View)  : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_merchant, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.itemView.marketName.text = item.nama
        holder.itemView.marketAddress.text = item.no_telp
        holder.itemView.productImage.setImageResource(R.drawable.ic_launcher_background)

        holder.itemView.setOnClickListener {
            listener(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}