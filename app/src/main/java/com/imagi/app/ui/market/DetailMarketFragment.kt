package com.imagi.app.ui.market

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.imagi.app.MapsActivity
import com.imagi.app.R
import com.imagi.app.network.DbServices
import kotlinx.android.synthetic.main.detail_market_fragment.*

class DetailMarketFragment : Fragment() {

    lateinit var buttonLocation : Button

    private lateinit var dbServices: DbServices

    companion object {
        fun newInstance(id:String): DetailMarketFragment {
            val fragment = DetailMarketFragment()
            val bundle = Bundle()
            bundle.putString("id", id)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        val myInflatedView: View = inflater.inflate(R.layout.detail_market_fragment, container, false)
        dbServices = DbServices(view?.context)
        dbServices.mContext = activity
        (activity as AppCompatActivity).supportActionBar?.title = "Detail Produk"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        buttonLocation = myInflatedView.findViewById(R.id.buttonLocation)
        buttonLocation.setOnClickListener {
            val intent = Intent(myInflatedView.context, MapsActivity::class.java)
            startActivity(intent)
        }

        if(dbServices.user.role.equals("Pedagang")){
            buttonEdit.visibility = View.VISIBLE
        }



        return myInflatedView
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



}