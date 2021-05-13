package com.imagi.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.adapter.MarketAdapter
import com.imagi.app.network.Market
import com.imagi.app.ui.market.DetailMarketFragment
import kotlinx.android.synthetic.main.detail_market_fragment.*
import kotlinx.android.synthetic.main.fragment_market.*

class DetailMarket : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_market_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DetailMarketFragment.newInstance())
                .commitNow()
        }
    }

}