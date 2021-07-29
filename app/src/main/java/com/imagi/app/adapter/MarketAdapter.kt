package com.imagi.app.adapter

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.imagi.app.R
import com.imagi.app.model.Store
import kotlinx.android.synthetic.main.item_market.view.*
import java.io.InputStream
import java.net.URL


class MarketAdapter(val market: List<Store>, private val type:String, private var listenerDelete: (Store) -> Unit?, private var listener: (Store) -> Unit)  :
    RecyclerView.Adapter<MarketAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_market, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = market[position]

        holder.itemView.marketName.text = item.nama_toko
        holder.itemView.marketAddress.text = "${item.alamat_toko} ${item.distance?.div(1000)?.toInt()}"
        Glide.with(holder.itemView)
            .load(Uri.parse("${item.path_foto}"))
            .placeholder(R.drawable.market_2)
            .into(holder.itemView.productImage)
        if(type == "Pedagang"){
            holder.itemView.btn_delete.visibility = View.VISIBLE
            holder.itemView.btn_delete.setOnClickListener {
                listenerDelete(item)
            }
        }
        holder.itemView.setOnClickListener {
            listener(item)
        }


    }

    fun LoadImageFromWebOperations(url: String?): Drawable? {
        return try {
            val data: InputStream = URL(url).content as InputStream
            Log.d("DATA_IMAGE", "$data")
            Drawable.createFromStream(data, "src name")
        } catch (e: Exception) {
            Log.d("DATA_IMAGE_NULL", "")
            null
        }
    }

    override fun getItemCount(): Int {
        Log.d("Jumlah data", "${market?.size}")
        return market.size
    }


    class ViewHolder(view: View)  : RecyclerView.ViewHolder(view) {}
}