package com.imagi.app

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
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
import com.imagi.app.ui.market.ActivityProductDetail
import com.imagi.app.ui.market.DetailMarketFragment
import com.imagi.app.ui.review.ReviewActivity
import com.imagi.app.util.AppUtils
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.detail_market_fragment.*
import kotlinx.android.synthetic.main.detail_market_fragment.view.*
import kotlinx.android.synthetic.main.fragment_market.*
import kotlinx.android.synthetic.main.item_product.*
import timber.log.Timber
import javax.inject.Inject

class DetailMarket : AppCompatActivity(), HasSupportFragmentInjector {

    lateinit var id : String;

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
    lateinit var buttonReview : Button
    lateinit var buttonDelete : RelativeLayout

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

//        (this as AppCompatActivity).supportActionBar?.title = "Detail Produk"
//        (this as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        (this as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        progress = findViewById(R.id.progressBarHome)
        mediaData = findViewById(R.id.vc_detail_market)
        merchantName = findViewById(R.id.vc_merchant_name)
        merchantAddress = findViewById(R.id.vc_address)
        listProduct = findViewById(R.id.rvProduct)
        buttonReview = findViewById(R.id.buttonFeedbackToMarket)

        if(intent.extras != null)
        {
            val bundle = intent.extras
            id = bundle?.getString("id")!!
        }else{
            Timber.d("FAIL_GET_DATA")
        }

        observerViewModel()

        buttonReview.setOnClickListener {
            Timber.d("CLICK_FEEDBACK")
            val bundle = Bundle()
            bundle.putString("id", id)
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        vc_add_product.setOnClickListener {
            val intent = Intent(this, ActivityProductDetail::class.java)
            val bundle = Bundle()
            bundle.putString("id", viewModel.storeDetailLiveData.value?.toko_id.toString())
            intent.putExtras(bundle)
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

            val adapters = dbServices.user.role?.let { it1 ->
                ProductAdapter(it, it1, {
                    AlertDialog.Builder(this).setMessage("Apakah anda yakin ingin menghapus produk ini ?")
                        .setCancelable(true)
                        .setNegativeButton("Tidak"){dialogInterface, i ->}
                        .setPositiveButton("Ya") { dialogInterface, i ->
                            viewModel.deleteProduct(
                                dbServices.findBearerToken(),
                                it.id_barang.toString()
                            )
                        }
                        .create().show()
                }){
                    val intent = Intent(this, ActivityProductDetail::class.java)
                    val bundle = Bundle()
                    bundle.putString("id", viewModel.storeDetailLiveData.value?.toko_id.toString())
                    bundle.putString("id_product", it.id_barang.toString())
                    bundle.putString("unit", it.satuan.toString())
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }

            list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            adapters?.notifyDataSetChanged()
            list.adapter = adapters
        })

        viewModel.code.observe(this, {
            if(it == 200){
                AlertDialog.Builder(this).setMessage("Produk berhasil dihapus")
                    .setPositiveButton("OK", {dialogInterface, i-> viewModel.getStoreProduct(dbServices.findBearerToken(), id)})
                    .create().show()
            }
        })
    }

}