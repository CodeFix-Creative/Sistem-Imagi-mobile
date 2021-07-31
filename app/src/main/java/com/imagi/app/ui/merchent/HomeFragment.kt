package com.imagi.app.ui.merchent

//import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.imagi.app.MarketFragment
import com.imagi.app.R
import com.imagi.app.adapter.MerchantAdapter
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
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
    private lateinit var btnSearch: LinearLayout

    private var columnCount = 1

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)
        dbServices = DbServices(getContext())
    }


    private fun observeViewModel(){

        try{
            viewModel.getMerchant(dbServices.findBearerToken())
        }catch (e:Exception){
            Timber.d("Error: ${e.message}")
        }

        viewModel.isShowLoader.observe(viewLifecycleOwner, {
            if(it){
                progressBarHome.visibility = View.VISIBLE
                list.visibility = View.GONE
            }else{
                progressBarHome.visibility = View.GONE
                list.visibility = View.VISIBLE
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, {
            view?.let { it1-> AppUtils.showAlert(it1.context, it) }
        })

        viewModel.merchantLiveData.observe(viewLifecycleOwner, {
            Timber.d("SHOE_DATA_MERCHANT")
            val list = list
            list.invalidate()

            val adapters = MerchantAdapter(it){
                val bundle = Bundle()
                it.id_pedagang?.let { it1 -> bundle.putString("id", it1.toString()) }
                val intent = Intent(view?.context, StoreMerchant::class.java)
                intent.putExtras(bundle)
                view?.context?.startActivity(intent)
            }

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
        btnSearch = view.findViewById(R.id.vc_search_bar)
        btnSearch.setOnClickListener {
            var intent = Intent(activity, SearchActivityPage::class.java)
            startActivity(intent)
        }

        observeViewModel()

        return view
    }


    companion object {
        fun newInstance(): MarketFragment = MarketFragment()
    }
}