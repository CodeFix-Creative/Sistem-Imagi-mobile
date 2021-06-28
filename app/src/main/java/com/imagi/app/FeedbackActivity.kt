package com.imagi.app

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imagi.app.adapter.ProductAdapter
import com.imagi.app.model.ReviewForm
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.util.AppUtils
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject

class FeedbackActivity : AppCompatActivity(), HasSupportFragmentInjector {

    lateinit var id : String;

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    lateinit var progress : ProgressBar
    lateinit var mediaData : LinearLayout
    lateinit var merchantName : TextView
    lateinit var buttonSend : Button
    lateinit var review : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        dbServices = DbServices(this)
        dbServices.mContext = this

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)

        progress = findViewById(R.id.progressBarHome)
        mediaData = findViewById(R.id.vc_media_data)
        merchantName = findViewById(R.id.vc_merchant_name)
        buttonSend = findViewById(R.id.vc_btn_review)
        review = findViewById(R.id.contentFeedback)

        buttonSend.setOnClickListener {
            if(validateFormReview(review.text.toString())){
                viewModel.postReview(dbServices.findBearerToken(), id, ReviewForm(
                    review = review?.text.toString(),
                    toko_id = id.toInt()
                ),
                    context = this
                )
                finish()
            }
        }

        if(intent.extras != null)
        {
            val bundle = intent.extras
            id = bundle?.getString("id")!!
        }else{
            Timber.d("FAIL_GET_DATA")
        }

        observerViewModel()
    }

    private fun validateFormReview(content: String) : Boolean{
        return if(TextUtils.isEmpty(content)){
            AppUtils.showAlert(this, "Mohon mengisi data review toko")
            false
        }else{
            true
        }
    }

    private fun observerViewModel(){
        viewModel.getStoreDetail(dbServices.findBearerToken(),id)

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
        })


    }
}