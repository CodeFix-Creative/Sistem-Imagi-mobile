package com.imagi.app.ui.market

import android.app.AlertDialog
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.imagi.app.R
import com.imagi.app.model.ProductForm
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.ui.review.ReviewActivity
import com.imagi.app.util.AppUtils
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_product_detail.*
import timber.log.Timber
import javax.inject.Inject


class ActivityProductDetail : AppCompatActivity(), HasSupportFragmentInjector {


    var id : String = ""
    var idProduct : String = ""
    var unit : String = ""

    @Inject
    lateinit var frahmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices
    private lateinit var spinner : Spinner

    lateinit var progress : ProgressBar

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return frahmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_product_detail)
//        binding = ActivityProductDetailBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        dbServices = DbServices(this)
        dbServices.mContext = this
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)

        progress = findViewById(R.id.progressBarHome)
        progress.visibility = View.GONE
        if(intent.extras != null)
        {
            val bundle = intent.extras
            id = bundle?.getString("id")!!
            if(bundle.containsKey("id_product")){
                Timber.d("SUCCESS_GET_PRODUCT_ID")
                this.idProduct = bundle.getString("id_product")!!
            }
            if(bundle.containsKey("unit")){
                Timber.d("SUCCESS_GET_UNIT")
                this.unit = bundle.getString("unit")!!
            }
        }else{
            Timber.d("FAIL_GET_DATA")
        }

//        val units = resources.getStringArray(R.array.unit_list)
//        val adapter = ArrayAdapter(this, R.layout.item_unit, units)
//        binding.vcUnitAuto.setAdapter(adapter)

        spinner = findViewById(R.id.unit_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.unit_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter.setDropDownViewResource(R.layout.item_unit)
            // Apply the adapter to the spinner
            if(unit.isNotEmpty()){
                adapter.getPosition(unit)
            }
            spinner.adapter = adapter
        }

        vc_btn_add.setOnClickListener {
            if(validate(vc_name.text.toString(), vc_price.text.toString(), unit_spinner.selectedItem.toString())){
                viewModel.postProduct(dbServices.findBearerToken(), ProductForm(
                    toko_id = id,
                    nama_barang = vc_name.text.toString(),
                    harga = vc_price.text.toString(),
                    satuan = unit_spinner.selectedItem.toString()
                ))
            }
        }


        observerViewModel()

        vc_btn_change.setOnClickListener {
            if(validate(vc_name.text.toString(), vc_price.text.toString(), unit_spinner.selectedItem.toString())){
                viewModel.putProduct(dbServices.findBearerToken(), idProduct, ProductForm(
                    toko_id = id,
                    nama_barang = vc_name.text.toString(),
                    harga = vc_price.text.toString(),
                    satuan = unit_spinner.selectedItem.toString()
                ))
            }
        }


    }

    private fun validate(name:String, price:String, unit:String) : Boolean {
        return if(TextUtils.isEmpty(name)){
            AppUtils.showAlert(this, "Mohon mengisi nama barang anda")
            false
        }else if(TextUtils.isEmpty(price)){
            AppUtils.showAlert(this, "Mohon mengisi harga barang anda")
            false
        }else if(TextUtils.isEmpty(unit)){
            AppUtils.showAlert(this, "Mohon mimilih satuan barang anda")
            false
        }else{
            true
        }
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
    }


    private fun observerViewModel(){
        if(idProduct.isNotEmpty() && idProduct!="") {
            vc_btn_change.visibility = View.VISIBLE
            vc_btn_add.visibility = View.GONE
            viewModel.getProduct(dbServices.findBearerToken(), idProduct)
        }else{
            vc_btn_change.visibility = View.GONE
            vc_btn_add.visibility = View.VISIBLE
        }

        viewModel.isShowLoader.observe(this, {
            if (it) {
                progress.visibility = View.VISIBLE
            } else {
                progress.visibility = View.GONE
            }
        })

        viewModel.errorMessage.observe(this, {
            AppUtils.showAlert(this, it);
        })

        viewModel.product.observe(this, {
            vc_name.setText(it.nama_barang)
            vc_price.setText(it.harga_rp?.substring(4,it.harga_rp.length)?.replace(".", ""))
        })
        viewModel.code.observe(this, {
            if(it == 201){
                AlertDialog.Builder(this).setMessage("Barang berhasil ditambahkan")
                    .setPositiveButton("OK", {dialogInterface, i-> finish()})
                    .create().show()
            }
            if(it == 200){
                AlertDialog.Builder(this).setMessage("Barang berhasil disimpan")
                    .setPositiveButton("OK", {dialogInterface, i-> finish()})
                    .create().show()
            }
        })



    }
}