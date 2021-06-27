package com.imagi.app.ui.review

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import timber.log.Timber
import javax.inject.Inject

class ReviewActivity : AppCompatActivity(), HasSupportFragmentInjector {

    lateinit var id : String;

    @Inject
    lateinit var frahmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices

    lateinit var progress : ProgressBar
    lateinit var listReview : RecyclerView

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return frahmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        dbServices = DbServices(this)
        dbServices.mContext = this
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        progress = findViewById(R.id.progressBarHome)
        listReview = findViewById(R.id.vc_review_list)

        if(intent.extras != null)
        {
            val bundle = intent.extras
            id = bundle?.getString("id")!!
        }else{
            Timber.d("FAIL_GET_DATA")
        }

        observerViewModel()

    }

    private fun observerViewModel(){
        viewModel.getReview(dbServices.findBearerToken(),id)

        viewModel.isShowLoader.observe(this, {
            if(it){
                progress.visibility = View.VISIBLE
                listReview.visibility = View.GONE
            }else{
                progress.visibility = View.GONE
                listReview.visibility = View.VISIBLE
            }
        })

        viewModel.errorMessage.observe(this, {
            AppUtils.showAlert(this, it);
        })

        viewModel.reviewLiveData.observe(this, {
            val list = listReview
            list.invalidate()

            val adapters = ReviewAdapter(it){}

            list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            adapters.notifyDataSetChanged()
            list.adapter = adapters
        })
    }
}