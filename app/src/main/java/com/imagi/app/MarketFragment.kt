package com.imagi.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.imagi.app.adapter.MarketAdapter
import com.imagi.app.network.Market
import com.imagi.app.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_market.*

class MarketFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar

    var markets = ArrayList<Market>()

    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myInflatedView: View = inflater.inflate(R.layout.fragment_market, container, false)
        initializeFragment(myInflatedView)

        return myInflatedView
    }

    private fun initializeFragment(inflateView : View){
        recyclerView = inflateView.findViewById(R.id.rvMarket)
        progressBar = inflateView.findViewById(R.id.progressBarHome)
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.d("HAIII", "hello")

        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed(Runnable {
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }, 1000)

        recyclerView.layoutManager = GridLayoutManager(view.context, 2)
        var price = 12000
        for (i in 1..10){
            price += i * 10
            var market = Market("Nama Produk $i", price)
            markets.add(market)
        }
//        rvMarket.adapter = MarketAdapter(markets)
        recyclerView.apply {
            layoutManager = this.layoutManager
            adapter = MarketAdapter(markets)
        }

    }

    companion object {
        fun newInstance(): MarketFragment = MarketFragment()
    }
}