package com.imagi.app.ui.market

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.FeedbackActivity
import com.imagi.app.MapsActivity
import com.imagi.app.R
import com.imagi.app.adapter.MarketAdapter
import com.imagi.app.model.Store
import com.imagi.app.network.Market
import kotlinx.android.synthetic.main.detail_market_fragment.*

class DetailMarketFragment : Fragment() {

    private var enableScrolling = true

    lateinit var buttonLocation : Button

    lateinit var buttonFeedback : Button

    var markets = ArrayList<Market>()

    var marchentName = this?.activity?.findViewById<TextView>(R.id.marchentName)

    fun isEnableScrolling(): Boolean {
        return enableScrolling
    }

    fun setEnableScrolling(enableScrolling: Boolean) {
        this.enableScrolling = enableScrolling
    }

    companion object {
        fun newInstance(id:String): DetailMarketFragment {
            val fragment = DetailMarketFragment()
            val bundle = Bundle()
            bundle.putString("id", id)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val myInflatedView: View = inflater.inflate(R.layout.detail_market_fragment, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Detail Produk"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        buttonLocation = myInflatedView.findViewById(R.id.buttonLocation)
        buttonLocation.setOnClickListener {
            val intent = Intent(myInflatedView.context, MapsActivity::class.java)
            startActivity(intent)
        }

        buttonFeedback = myInflatedView.findViewById(R.id.buttonFeedbackToMarket)
        buttonFeedback.setOnClickListener {
            val intent = Intent(myInflatedView.context, FeedbackActivity::class.java)
            startActivity(intent)
        }

        return myInflatedView
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



}