package com.imagi.app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.imagi.app.R
import com.imagi.app.adapter.ProductAdapter
import com.imagi.app.adapter.ReviewAdapter
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.util.AppUtils
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_search_page.*
import javax.inject.Inject

class SearchActivityPage : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices

    lateinit var progress : ProgressBar
    lateinit var textEmpty : TextView
    lateinit var textSearch : EditText
    lateinit var btnSearch : ImageView
    lateinit var btnSearchAll : LinearLayout
    lateinit var list : RecyclerView
    var minPrice : String = ""
    var maxPrice : String = ""


    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        dbServices = DbServices(this)
        dbServices.mContext = this

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)

        setContentView(R.layout.activity_search_page)
        progress = findViewById(R.id.progressBarHome)
        list = this.findViewById(R.id.vc_product)
        textSearch = findViewById(R.id.vc_search)
        textEmpty = findViewById(R.id.vc_empty_product)
        btnSearch =  findViewById(R.id.vc_btn_search)
        btnSearchAll = findViewById(R.id.vc_btn_searchAll)

        btnSearch.setOnClickListener {
            if(vc_minPrice.text!=null){
                this.minPrice = vc_minPrice.text.toString()
            }
            if(vc_maxPrice.text!=null){
                this.maxPrice = vc_minPrice.text.toString()
            }
            var url = "${textSearch.text.toString()}}&min=${minPrice}&max=${maxPrice}"
            viewModel.getGlobalSearch(
                dbServices.findBearerToken(),
                url
            )
        }

        btnSearchAll.setOnClickListener {
            if(vc_minPrice.text!=null){
                this.minPrice = vc_minPrice.text.toString()
            }
            if(vc_maxPrice.text!=null){
                this.maxPrice = vc_minPrice.text.toString()
            }
            var url = "nama=${textSearch.text.toString()}}&min=$minPrice&max=$maxPrice"
            viewModel.getGlobalSearch(
                dbServices.findBearerToken(),
                url
            )
        }


        observerViewModel()

    }

    private fun observerViewModel(){

        viewModel.isShowLoader.observe(this, {
            if(it){
                progress.visibility = View.VISIBLE
                list.visibility = View.GONE
            }else{
                progress.visibility = View.GONE
                list.visibility = View.VISIBLE
            }
        })

        viewModel.errorMessage.observe(this, {
            AppUtils.showAlert(this, it);
        })

        viewModel.productLiveData.observe(this, {
            if(it.isNotEmpty()){
                textEmpty.visibility = View.GONE
            }else{
                textEmpty.visibility = View.VISIBLE
            }

            val listResult = list
            listResult.invalidate()

            val adapters = ProductAdapter(it,"", {}){}

            listResult.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            adapters.notifyDataSetChanged()
            listResult.adapter = adapters
        })
    }
}