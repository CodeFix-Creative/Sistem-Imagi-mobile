package com.imagi.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.R
import com.imagi.app.model.Product
import kotlinx.android.synthetic.main.item_product.*
import kotlinx.android.synthetic.main.item_product.view.*

class ProductAdapter(val list: List<Product>,private var role: String = "Customer",
                     private var deleteListener:(Product)->Unit,
                     private var listerner: (Product)-> Unit) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view:View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context,).inflate(R.layout.item_product, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.itemView.productName.text = item.nama_barang
        holder.itemView.productPrice.text = "${item.harga_rp}/${item.satuan}"

        holder.itemView.setOnClickListener {
            listerner(list[position])
        }
        if(role!=null){
            if(role == "Pedagang"){
                holder.itemView.vc_btn_delete.visibility = View.VISIBLE
                holder.itemView.vc_btn_delete.setOnClickListener {
                    deleteListener(list[position])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}