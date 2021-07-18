package com.imagi.app.ui.review

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.imagi.app.FeedbackActivity
import com.imagi.app.R
import com.imagi.app.adapter.ReviewAdapter
import com.imagi.app.model.ReplayForm
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.util.AppUtils
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_review_list.*
import timber.log.Timber
import javax.inject.Inject

class ReviewActivity : AppCompatActivity(), HasSupportFragmentInjector {



    @Inject
    lateinit var frahmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices

    lateinit var id : String
    lateinit var idReview : String
    lateinit var progress : ProgressBar
    lateinit var listReview : RecyclerView
    lateinit var fab : FloatingActionButton
    lateinit var refresh: SwipeRefreshLayout
    lateinit var data: String

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
        fab = findViewById(R.id.fab)
        refresh = findViewById<SwipeRefreshLayout>(R.id.refresh)

        vc_btn_close.setOnClickListener {
            vc_dialog_form.visibility = View.GONE
        }

        fab.setOnClickListener {
            Timber.d("CLICK_FEEDBACK")
            val bundle = Bundle()
            bundle.putString("id", id)
//            bundle.putString("data", data)
            val intent = Intent(this, FeedbackActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        vc_btn_save.setOnClickListener {
            Timber.d("BALASAN : ${vc_et_replay.text.toString()}")
            if(validate(vc_et_replay.text.toString())){
                viewModel.postReplay(dbServices.findBearerToken(), dbServices.user.id_pedagang.toString(),
                    ReplayForm(
                        balasan = vc_et_replay.text.toString(),
                        review_id = idReview
                    )
                )
            }
        }

        refresh.setOnRefreshListener {
            viewModel.getReview(dbServices.findBearerToken(),id)
            Handler().postDelayed(Runnable {
                try{
                    refresh.isRefreshing = false
                }catch (e:Exception){
                    Timber.d("${e.message}")
                }
            }, 1000)

        }

        if(intent.extras != null)
        {
            val bundle = intent.extras
            id = bundle?.getString("id")!!
//            if(bundle.containsKey("data")){
//                this.data = bundle?.getString("data")!!
//            }
        }else{
            Timber.d("FAIL_GET_DATA")
        }

        observerViewModel()

    }

    private fun validate(replay:String) : Boolean {
        return if(TextUtils.isEmpty(replay)){
            AppUtils.showAlert(this, "Mohon mengisi balasan anada")
            false
        }else{
            true
        }
    }

    private fun observerViewModel(){

        if(dbServices.user.role == "Pedagang"){
            fab.visibility = View.GONE;
        }
        Timber.d("data_kosong ${viewModel.reviewLiveData.value?.isEmpty()}")

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

        viewModel.code.observe(this, {
            if(it == 201){
                viewModel.getReview(dbServices.findBearerToken(),id)
                vc_dialog_form.visibility = View.GONE
            }
        })

        viewModel.reviewLiveData.observe(this, {
//            if(it == null || viewModel.reviewLiveData.value?.isEmpty() == true){
//                vc_empty_review.visibility = View.VISIBLE
//            }
            val list = listReview
            list.invalidate()

            val adapters = ReviewAdapter(it){
                this.idReview = it.id_review.toString()
                if(dbServices.user.role == "Pedagang"){
                    vc_dialog_form.visibility = View.VISIBLE
                    vc_customer_name.setText(it.nama_customer)
                }
            }

            list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            adapters.notifyDataSetChanged()
            list.adapter = adapters
        })
    }
}