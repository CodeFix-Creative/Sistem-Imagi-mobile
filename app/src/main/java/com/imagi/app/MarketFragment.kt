package com.imagi.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.adapter.MarketAdapter
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.imagi.app.ui.merchent.SearchActivityPage
import com.imagi.app.util.AppUtils
import timber.log.Timber
import java.lang.Exception

class MarketFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices
    private lateinit var btnSearch: LinearLayout

    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var currentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myInflatedView: View = inflater.inflate(R.layout.fragment_market, container, false)
        initializeFragment(myInflatedView)
        dbServices.mContext = context
        this.currentView = myInflatedView
        btnSearch = myInflatedView.findViewById(R.id.vc_search_bar)
        btnSearch.setOnClickListener {
            var intent = Intent(activity, SearchActivityPage::class.java)
            startActivity(intent)
        }
        return myInflatedView
    }

    private fun initializeFragment(inflateView : View){
        recyclerView = inflateView.findViewById(R.id.rvMarket)
        progressBar = inflateView.findViewById(R.id.progressBarHome)

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

        var position = dbServices.location
        var startPosition = position.latitude?.let { position.longitude?.let { it1 -> LatLng(it.toDouble(), it1.toDouble()) } }

        try {
            viewModel.getStoreCustomer(dbServices.findBearerToken(), startPosition)

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

//            if(it == null || viewModel.storeLiveData.value?.isEmpty() == true){
//                currentView.vc_empty.visibility = View.VISIBLE
//            }

            Timber.d("SHOW_DATA")
            val list = recyclerView
            list.invalidate()

            val adapters = dbServices.user.role?.let { it1 ->
                MarketAdapter(it, it1, {}){
                    val bundle = Bundle()
                    it.toko_id?.let { it1 -> bundle.putString("id", it1.toString()) }
                    val intent = Intent(view?.context, DetailMarket::class.java)
                    intent.putExtras(bundle)
                    view?.context?.startActivity(intent)
                }
            }

//            context?.let { it1 -> AppUtils.showAlert(it1, "Show DATA") }
            list.layoutManager =  GridLayoutManager(activity, 2)
            adapters?.notifyDataSetChanged()
            list.adapter = adapters
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): MarketFragment = MarketFragment()
    }
}