package com.imagi.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.adapter.MarketAdapter
import com.imagi.app.adapter.ProductAdapter
import com.imagi.app.network.DbServices
import com.imagi.app.network.Market
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.ui.market.DetailMarketFragment
import com.imagi.app.util.AppUtils
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.detail_market_fragment.*
import kotlinx.android.synthetic.main.fragment_market.*
import timber.log.Timber
import javax.inject.Inject

class DetailMarket : AppCompatActivity(), HasSupportFragmentInjector {

    lateinit var id : String;

    lateinit var buttonFeedback : Button

    @Inject
    lateinit var frahmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory:ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices

    lateinit var progress :ProgressBar
    lateinit var mediaData : LinearLayout
    lateinit var merchantName : TextView
    lateinit var merchantAddress : TextView
    lateinit var listProduct : RecyclerView

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
       return frahmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.detail_market_fragment)
        dbServices = DbServices(this)
        dbServices.mContext = this

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)

        (this as AppCompatActivity).supportActionBar?.title = "Detail Produk"
        (this as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (this as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        progress = findViewById(R.id.progressBarHome)
        mediaData = findViewById(R.id.vc_detail_market)
        merchantName = findViewById(R.id.vc_merchant_name)
        merchantAddress = findViewById(R.id.vc_address)
        listProduct = findViewById(R.id.rvProduct)

        if(intent.extras != null)
        {
            val bundle = intent.extras
            id = bundle?.getString("id")!!
        }else{
            Timber.d("FAIL_GET_DATA")
        }

        observerViewModel()

        buttonFeedback = findViewById(R.id.buttonFeedbackToMarket)
        buttonFeedback.setOnClickListener {
            Timber.d("CLICK_FEEDBACK")
            val intent = Intent(this, FeedbackActivity::class.java)
            startActivity(intent)
        }

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, DetailMarketFragment.newInstance(id))
//                .commitNow()
//        }


    }


    private fun observerViewModel(){
        viewModel.getStoreDetail(dbServices.findBearerToken(),id)
        viewModel.getStoreProduct(dbServices.findBearerToken(), id)

        viewModel.isShowLoader.observe(this, {
            if(it){
                progress.visibility = View.VISIBLE
                mediaData.visibility = View.GONE
            }else{
                progress.visibility = View.GONE
                mediaData.visibility = View.VISIBLE
            }
        })

        viewModel.errorMessage.observe(this, {
            AppUtils.showAlert(this, it);
        })

        viewModel.storeDetailLiveData.observe(this, {
            merchantName.text = it.nama_toko
            merchantAddress.text = it.alamat_toko
        })

        viewModel.productLiveData.observe(this, {
            val list = listProduct
            list.invalidate()

            val adapters = ProductAdapter(it){}

            list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            adapters.notifyDataSetChanged()
            list.adapter = adapters
        })
    }

}