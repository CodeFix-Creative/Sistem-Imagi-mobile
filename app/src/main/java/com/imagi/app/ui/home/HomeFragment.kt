package com.imagi.app.ui.home

//import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.imagi.app.MarketFragment
import com.imagi.app.R
import com.imagi.app.adapter.MerchantAdapter
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.ui.home.dummy.DummyContent
import com.imagi.app.util.AppUtils
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_item_list.*
import kotlinx.android.synthetic.main.fragment_layout_home.*
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class HomeFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices

    private var columnCount = 1

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)
        dbServices = DbServices(getContext())

        try{
            viewModel.getMerchant(dbServices.findBearerToken())
        }catch (e:Exception){
            Timber.d("Error: ${e.message}")
        }

        observeViewModel()
    }


    private fun observeViewModel(){
        viewModel.isShowLoader.observe(this, {
            if(it){
                progressBarHome.visibility = View.VISIBLE
                list.visibility = View.GONE
            }else{
                progressBarHome.visibility = View.GONE
                list.visibility = View.VISIBLE
            }
        })

        viewModel.errorMessage.observe(this, {
            view?.let { it1-> AppUtils.showAlert(it1.context, it) }
        })

        viewModel.merchantLiveData.observe(this, {
            Timber.d("SHOE_DATA_MERCHANT")
            val list = list
            list.invalidate()

            val adapters = MerchantAdapter(it){}

            list.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapters?.notifyDataSetChanged()
            list.adapter = adapters
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_layout_home, container, false)

        // Set the adapter
//        if (view is RecyclerView) {
//            with(view) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
//                adapter =  MyItemRecyclerViewAdapter(DummyContent.ITEMS)
//            }
//        }
        return view
    }


    companion object {
        fun newInstance(): MarketFragment = MarketFragment()
    }
}