package com.imagi.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.imagi.app.adapter.MarketAdapter
import com.imagi.app.network.Market
import kotlinx.android.synthetic.main.activity_list_market.*

class ListMarkets : Fragment() {
    var markets = ArrayList<Market>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rvMarket.layoutManager = GridLayoutManager(this.context, 2)
        var price = 12000
        for (i in 1..10){
            price += i * 10
            var market = Market("Nama Produk $i", price)
            markets.add(market)
        }
        rvMarket.adapter = MarketAdapter(markets)
    }


}