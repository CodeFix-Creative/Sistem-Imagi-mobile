package com.imagi.app

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.imagi.app.adapter.ProductAdapter
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.ui.market.ActivityProductDetail
import com.imagi.app.ui.review.ReviewActivity
import com.imagi.app.util.AppUtils
import com.imagi.app.util.URIPathHelper
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_store_merhcnat.*
import kotlinx.android.synthetic.main.detail_market_fragment.*
import kotlinx.android.synthetic.main.detail_market_fragment.vc_btn_close
import kotlinx.android.synthetic.main.detail_market_fragment.vc_dialog_form
import kotlinx.android.synthetic.main.detail_market_fragment.vc_merchant_address
import kotlinx.android.synthetic.main.detail_market_fragment.vc_merchant_lat
import kotlinx.android.synthetic.main.detail_market_fragment.vc_merchant_long
import kotlinx.android.synthetic.main.detail_market_fragment.vc_merchant_phone
import kotlinx.android.synthetic.main.detail_market_fragment.vc_merchant_photo
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class DetailMarket : AppCompatActivity(), HasSupportFragmentInjector {

    lateinit var id : String;

    @Inject
    lateinit var frahmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var fusedLocationClient: FusedLocationProviderClient

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
    private val pickImage = 100
    private val PERMISSION_REQUEST_CODE = 200
    private var imageUri: Uri? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var body: MultipartBody.Part? = null
    private var onEdit :Boolean = false

    val uriPathHelper = URIPathHelper()

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
       return frahmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.detail_market_fragment)
        dbServices = DbServices(this)
        dbServices.mContext = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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

        vc_merchant_photo.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        buttonEdit.setOnClickListener {
            this.onEdit = true
            vc_dialog_form.visibility = View.VISIBLE
            vc_add_product.visibility = View.GONE
            et_merchant_name.setText(viewModel.storeDetailLiveData.value?.nama_toko)
            vc_merchant_phone.setText(viewModel.storeDetailLiveData.value?.no_telp)
            vc_merchant_address.setText(viewModel.storeDetailLiveData.value?.alamat_toko)
            Glide.with(vc_merchant_photo)
                .load(Uri.parse("${viewModel.storeDetailLiveData.value?.path_foto}"))
                .placeholder(R.drawable.ic_launcher_background)
                .into(vc_merchant_photo)
            vc_merchant_long.setText(viewModel.storeDetailLiveData.value?.longitude)
            vc_merchant_lat.setText(viewModel.storeDetailLiveData.value?.latitude)

        }

        vc_btn_close.setOnClickListener {
            vc_dialog_form.visibility = View.GONE
            vc_add_product.visibility = View.VISIBLE
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Timber.d("ONSTART")
                    Timber.d("GET_LAST_LOCATION : ${location.latitude}")
                    Timber.d("GET_LAST_LOCATION : ${location.longitude}")
                }
                this.latitude = location?.latitude
                this.longitude = location?.longitude
            }

        if (checkPermission()) {

        } else {
            requestPermission();
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

        buttonLocation.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            val bundle = Bundle()
            bundle.putString("latitude", viewModel.storeDetailLiveData.value?.latitude.toString())
            bundle.putString("longitude", viewModel.storeDetailLiveData.value?.longitude.toString())
            intent.putExtras(bundle)
            startActivity(intent)
        }

        findViewById<RelativeLayout>(R.id.vc_btn_save).setOnClickListener {
            if(validate(
                    et_merchant_name.text.toString(),
                    vc_merchant_phone.text.toString(),
                    vc_merchant_address.text.toString()
                )){

                if(vc_merchant_lat.text.toString()!=""){
                    this.latitude = vc_merchant_lat.text.toString().toDouble()
                }
                if(vc_merchant_long.text.toString()!=""){
                    this.longitude = vc_merchant_long.text.toString().toDouble()
                }

                var map = HashMap<String, RequestBody>()
                toRequestBody(id)?.let { it1 -> map.put("pedagang_id", it1) }
                map["nama_toko"] = toRequestBody(et_merchant_name.text.toString().trim())
                map["no_telp"] = toRequestBody(vc_merchant_phone.text.toString().trim())
                map["alamat_toko"] = toRequestBody(vc_merchant_address.text.toString().trim())
                map["latitude"] = toRequestBody("$latitude")
                map["longitude"] = toRequestBody("$longitude")
                map["facebook"] = toRequestBody("-")
                map["twitter"] = toRequestBody("-")
                map["instagram"] = toRequestBody("-")
                map["website"] = toRequestBody("-")
                if(imageUri!=null) {
                    val file = File(imageUri?.path)
                    body = imageUri?.let { it1 -> prepareFilePart(file.name, it1) }
                }

                Timber.d("GAMBAR :")
                Timber.d("DATA_LONGITUDE : $longitude")
                if (body != null) {
                    viewModel.putStore(
                        dbServices.findBearerToken(), id, map, body!!
                    )
                } else{
                    viewModel.putStoreWithoutImage(dbServices.findBearerToken(), id, map)
                }
            }
        }


    }

    private fun checkPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            false
        } else if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            false
        }else if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            false
        } else{
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    this.latitude = location?.latitude
                    this.longitude = location?.longitude
                    Timber.d("GET_LAST_LOCATION")
                }
            true
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    fun prepareFilePart(name: String, fileUri: Uri) : MultipartBody.Part {
        var file : File = File(uriPathHelper.getPath(this, fileUri))

        Log.d("FILENAME", "${file.name}")
        var body = RequestBody.create(MediaType.parse(contentResolver.getType(fileUri)), file)

        return MultipartBody.Part.createFormData("foto", file.name, body)
    }

    fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), value)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == pickImage){
            imageUri = data?.data
            vc_merchant_photo.setImageURI(imageUri)
        }
    }

    private fun  validate(
        name: String,
        phone: String,
        address: String,
    ) : Boolean {
        return  if(TextUtils.isEmpty(name)){
            AppUtils.showAlert(this, "Mohon melengkapi nama toko")
            false
        } else if(TextUtils.isEmpty(phone)){
            AppUtils.showAlert(this, "Mohon melengkapi nomor telepon toko")
            false
        }else if(TextUtils.isEmpty(address)){
            AppUtils.showAlert(this, "Mohon melengkapi alamat toko")
            false
        } else {
            true
        }
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

            Glide.with(img_market)
                .load(Uri.parse("${it.path_foto}"))
                .placeholder(R.drawable.market_2)
                .into(img_market)
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
            if(it == 200 && !onEdit){
                AlertDialog.Builder(this).setMessage("Produk berhasil dihapus")
                    .setPositiveButton("OK", {dialogInterface, i-> viewModel.getStoreProduct(dbServices.findBearerToken(), id)})
                    .create().show()
            }
            if(it == 200 && onEdit){
                AlertDialog.Builder(this).setMessage("Data toko berhasil diperbaharui")
                    .setPositiveButton("OK", {dialogInterface, i->
                        vc_dialog_form.visibility = View.GONE
                        vc_add_product.visibility = View.VISIBLE
                        viewModel.getStoreDetail(dbServices.findBearerToken(),id)
                        viewModel.getStoreProduct(dbServices.findBearerToken(), id)})
                    .create().show()
            }
        })
    }

}