package com.imagi.app.ui.market

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.R
import com.imagi.app.adapter.MarketAdapter
import com.imagi.app.network.Market
import kotlinx.android.synthetic.main.detail_market_fragment.*

class DetailMarketFragment : Fragment() {

    private var enableScrolling = true

    var markets = ArrayList<Market>()

    fun isEnableScrolling(): Boolean {
        return enableScrolling
    }

    fun setEnableScrolling(enableScrolling: Boolean) {
        this.enableScrolling = enableScrolling
    }

    companion object {
        fun newInstance() = DetailMarketFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.title = "Detail Produk"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        return inflater.inflate(R.layout.detail_market_fragment, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvProduct)
        recyclerView.layoutManager = GridLayoutManager(this.context, 2)
        var price = 12000
        for (i in 1..4){
            price += i * 4
            var market = Market("Nama Produk $i", price)
            markets.add(market)
        }
//        rvMarket.adapter = MarketAdapter(markets)
        rvProduct.apply {
            layoutManager = this.layoutManager
            adapter = MarketAdapter(markets)
        }
        rvProduct.setOnTouchListener(View.OnTouchListener { v, _ -> false })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }


}