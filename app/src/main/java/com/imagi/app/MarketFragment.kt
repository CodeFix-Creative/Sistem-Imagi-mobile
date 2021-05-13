package com.imagi.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.adapter.MarketAdapter
import com.imagi.app.network.Market
import kotlinx.android.synthetic.main.fragment_market.*

class MarketFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_market, container, false)
    }

    var markets = ArrayList<Market>()

    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        print("HAIII")

        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvMarket)
        rvMarket.layoutManager = GridLayoutManager(this.context, 2)
        var price = 12000
        for (i in 1..10){
            price += i * 10
            var market = Market("Nama Produk $i", price)
            markets.add(market)
        }
//        rvMarket.adapter = MarketAdapter(markets)
        rvMarket.apply {
            layoutManager = this.layoutManager
            adapter = MarketAdapter(markets)
        }
    }

    companion object {
        fun newInstance(): MarketFragment = MarketFragment()
    }
}