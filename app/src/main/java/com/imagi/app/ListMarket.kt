package com.imagi.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.imagi.app.adapter.MarketAdapter
import com.imagi.app.network.Market
import kotlinx.android.synthetic.main.activity_list_market.*

class ListMarket : AppCompatActivity() {

    var markets = ArrayList<Market>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_market)

        rvMarket.layoutManager = GridLayoutManager(this, 2)
        var price = 12000
        for (i in 1..10){
            price += i * 10
            var market = Market("Nama Produk $i", price)
            markets.add(market)
        }
        rvMarket.adapter = MarketAdapter(markets)
    }


}