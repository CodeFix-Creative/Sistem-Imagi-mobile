package com.imagi.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.R
import com.imagi.app.model.Product
import kotlinx.android.synthetic.main.item_product.*
import kotlinx.android.synthetic.main.item_product.view.*
import kotlinx.android.synthetic.main.item_product.view.productName
import kotlinx.android.synthetic.main.item_product.view.productPrice
import kotlinx.android.synthetic.main.item_product_search.view.*

class ProductSearchAdapter(val list: List<Product>, private var role: String = "Customer",
                           private var deleteListener:(Product)->Unit,
                           private var listerner: (Product)-> Unit) :
    RecyclerView.Adapter<ProductSearchAdapter.ViewHolder>() {

    class ViewHolder(view:View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context,).inflate(R.layout.item_product_search, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.itemView.productName.text = item.nama_barang
        holder.itemView.productPrice.text = "${item.harga_rp}/${item.satuan}"
        holder.itemView.tv_distance.text = "${(item.distance?.div(1000))?.toInt()} km"
        holder.itemView.vc_market_name.text = item.nama_toko

        holder.itemView.setOnClickListener {
            listerner(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}