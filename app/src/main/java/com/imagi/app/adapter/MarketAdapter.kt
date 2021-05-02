package com.imagi.app.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.network.Market
import kotlinx.android.synthetic.main.item_market.view.*
import com.imagi.app.R

class MarketAdapter(val market : ArrayList<Market>)  : RecyclerView.Adapter<MarketAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_market, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(market.get(position))
    }

    override fun getItemCount(): Int {
        return market.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var view : View = itemView

        private  lateinit var market: Market

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Toast.makeText(view.context, "${market.name} OnCLicked", Toast.LENGTH_SHORT).show()
        }

        fun bindData(market: Market){
            this.market = market
            view.productImage.setImageResource(R.drawable.ic_launcher_background)
            view.marketName.setText(market.name)
            view.productPrice.setText(market.price.toString())
        }

    }
}