package com.imagi.app.adapter
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.DetailMarket
import kotlinx.android.synthetic.main.item_market.view.*
import com.imagi.app.R
import com.imagi.app.model.Store

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
        holder.itemView.productImage.setImageResource(R.drawable.ic_launcher_background)

        holder.itemView.setOnClickListener {
            listener(item)
        }


    }

    override fun getItemCount(): Int {
        Log.d("Jumlah data" , "${market?.size}")
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