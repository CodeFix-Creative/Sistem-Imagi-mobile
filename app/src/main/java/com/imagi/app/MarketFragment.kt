package com.imagi.app

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.adapter.MarketAdapter
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.imagi.app.model.Store
import com.imagi.app.util.AppUtils
import kotlinx.android.synthetic.main.fragment_market.*
import timber.log.Timber
import java.lang.Exception

class MarketFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices

    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myInflatedView: View = inflater.inflate(R.layout.fragment_market, container, false)
        initializeFragment(myInflatedView)
        dbServices.mContext = context
        return myInflatedView
    }

    private fun initializeFragment(inflateView : View){
        recyclerView = inflateView.findViewById(R.id.rvMarket)
        progressBar = inflateView.findViewById(R.id.progressBarHome)
//        progressBar.visibility = View.VISIBLE
//        recyclerView.visibility = View.GONE

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dbServices = DbServices(context)

    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)
        dbServices = DbServices(getContext())

        try {
            viewModel.getStore(dbServices.findBearerToken())

        }catch (e:Exception){
            Timber.d("Error : ${e.message}")
        }


        observeViewModel()
    }

    private fun observeViewModel(){

        viewModel.isShowLoader.observe(this, {
            if(it){
                recyclerView.visibility = View.GONE
                Timber.d("SHOW_LOADER")
//                context?.let { it1 -> AppUtils.showAlert(it1, "Show Loader") }
                progressBar.visibility = View.VISIBLE
            }else{
                Timber.d("HIDE_LOADER")
                recyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        })

        viewModel.errorMessage.observe(this,{
            view?.let { it1 -> AppUtils.showAlert(it1.context, it) }
        })

        viewModel.storeLiveData.observe(this, {
            Timber.d("SHOW_DATA")
            val list = recyclerView
            list.invalidate()

            val adapters = MarketAdapter(it){}

//            context?.let { it1 -> AppUtils.showAlert(it1, "Show DATA") }
            list.layoutManager =  GridLayoutManager(activity, 2)
            adapters?.notifyDataSetChanged()
            list.adapter = adapters
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.d("HAIII", "hello")

        super.onViewCreated(view, savedInstanceState)

//        viewModel.isShowLoader.observe(viewLifecycleOwner, {
//            if(it){
//                Timber.d("HIDE_LOADER")
////                recyclerView.visibility = View.VISIBLE
//                progressBar.visibility = View.GONE
//                progressBarHome.visibility = View.GONE
//            }else{
////                recyclerView.visibility = View.GONE
//                Timber.d("SHOW_LOADER")
//                progressBar.visibility = View.VISIBLE
//            }
//        })
//
//        viewModel.errorMessage.observe(viewLifecycleOwner,{
//            view.let { it1 -> AppUtils.showAlert(it1.context, it) }
//        })
//
//        viewModel.storeLiveData.observe(viewLifecycleOwner, {
//            val list = recyclerView
//            list.invalidate()
//
//            val adapters = MarketAdapter(it){}
//
//            list.layoutManager =  LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
//            adapters?.notifyDataSetChanged()
//            list.adapter = adapters
//        })

//        Handler().postDelayed(Runnable {
//
//        }, 1000)

//        recyclerView.layoutManager = GridLayoutManager(view.context, 2)
//        var price = 12000
////        for (i in 1..10){
////            price += i * 10
////            var market = Store("Nama Produk ${i}", price)
////            markets.add(market)
////        }
////        rvMarket.adapter = MarketAdapter(markets)
////        viewModel.storeLiveData
//        recyclerView.apply {
//            layoutManager = this.layoutManager
//            adapter = viewModel.storeLiveData.value?.let { MarketAdapter(it) {} }
//
//        }

    }

    companion object {
        fun newInstance(): MarketFragment = MarketFragment()
    }
}