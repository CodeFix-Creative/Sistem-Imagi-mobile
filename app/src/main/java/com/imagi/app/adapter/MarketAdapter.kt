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


class MarketAdapter(val market: List<Store>, private var listener: (Store) -> Unit)  :
    RecyclerView.Adapter<MarketAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_market, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = market[position]

        holder.itemView.marketName.text = item.nama_toko
        holder.itemView.marketAddress.text = item.alamat_toko
//        try{
        Glide.with(holder.itemView)
            .load(Uri.parse("${item.path_foto}"))
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.itemView.productImage)
//        }catch (e:Exception){
//            holder.itemView.productImage.setImageResource()
//
//        }
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


//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
//
//        private var view : View = itemView
//
//        private  lateinit var market: Store
//
//        init {
//            itemView.setOnClickListener(this)
//        }
//
//        override fun onClick(v: View?) {
//            Toast.makeText(view.context, "${market.nama_toko} OnCLicked", Toast.LENGTH_SHORT).show()
//            val intent = Intent(view.context, DetailMarket::class.java)
//            view.context.startActivity(intent)
//        }
//
//        fun bindData(market: Store){
//            this.market = market
//            view.productImage.setImageResource(R.drawable.ic_launcher_background)
//            view.marketName.setText(market.nama_toko)
//            view.productPrice.setText(market.alamat_toko.toString())
//        }
//
//    }
}