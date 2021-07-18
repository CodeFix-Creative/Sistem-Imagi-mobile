package com.imagi.app.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.imagi.app.DetailMarket
import com.imagi.app.ProfilePage
import com.imagi.app.R
import com.imagi.app.adapter.MarketAdapter
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.util.AppUtils
import com.imagi.app.util.URIPathHelper
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_store_merhcnat.*
import kotlinx.android.synthetic.main.activity_store_merhcnat.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class StoreMerchant : AppCompatActivity() , HasSupportFragmentInjector {

    lateinit var id : String;

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var frahmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices

    lateinit var progress : ProgressBar
    lateinit var listReview : RecyclerView
    var onClick : Boolean = false

    private val pickImage = 100
    private val PERMISSION_REQUEST_CODE = 200
    private var imageUri: Uri? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var body: MultipartBody.Part? = null

    val uriPathHelper = URIPathHelper()

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return frahmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        dbServices = DbServices(this)
        dbServices.mContext = this
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_merhcnat)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        progress = findViewById(R.id.progressBarHome)
        listReview = findViewById(R.id.rvMarket)


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
            //main logic or main code

            // . write your main code to execute, It will execute if the permission is already given.

        } else {
            requestPermission();
        }

        fab.visibility = View.GONE

        if(intent.extras != null)
        {
            val bundle = intent.extras
            id = bundle?.getString("id")!!
        }else{
            Timber.d("FAIL_GET_DATA")
        }

        if(dbServices.user.role == "Pedagang"){
            this.id = dbServices.user.id_pedagang.toString()
        }

        observerViewModel();

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        this.latitude = location?.latitude
                        this.longitude = location?.longitude
                        Timber.d("GET_LAST_LOCATION")
                    }
                // main logic
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        AlertDialog.Builder(this).setMessage("Permission camera diperlukan")
                            .setPositiveButton("OK", { dialogInterface, i ->
                                requestPermission()
                            })
                            .create().show()
                    }

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        AlertDialog.Builder(this).setMessage("Permission akses lokasi diperlukan")
                            .setPositiveButton("OK", { dialogInterface, i ->
                                requestPermission()
                            })
                            .create().show()
                    }
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        AlertDialog.Builder(this).setMessage("Permission akses lokasi diperlukan")
                            .setPositiveButton("OK", { dialogInterface, i ->
                                requestPermission()
                            })
                            .create().show()
                    }
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

    private fun validate(
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

        if(dbServices.user.role == "Pedagang"){
            fab.visibility = View.VISIBLE
        }

        viewModel.getStoreMerchant(dbServices.findBearerToken(), id)

        viewModel.isShowLoader.observe(this, {
            if (it) {
                progress.visibility = View.VISIBLE
                listReview.visibility = View.GONE
            } else {
                progress.visibility = View.GONE
                listReview.visibility = View.VISIBLE
            }
        })

        viewModel.errorMessage.observe(this, {
            AppUtils.showAlert(this, it);
        })

        viewModel.storeLiveData.observe(this, {

            if(it == null || viewModel.storeLiveData.value?.isEmpty() == true){
                vc_empty.visibility = View.VISIBLE
            }

            val list = listReview
            list.invalidate()

            val adapters = MarketAdapter(it) {
                val bundle = Bundle()
                bundle.putString("id", it.toko_id.toString())
                val intent = Intent(this, DetailMarket::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            list.layoutManager = GridLayoutManager(this, 2)
            adapters.notifyDataSetChanged()
            list.adapter = adapters
        })

        viewModel.code.observe(this, {
            if (it == 201) {
                vc_dialog_form.visibility = View.GONE
                fab.visibility = View.VISIBLE
                viewModel.getStoreMerchant(dbServices.findBearerToken(), id)
            }
            if (it == 422) {
                AppUtils.showAlert(this, "Gambar yang ada upload terlalu besar, mohon menggunkkan gambar" +
                        "dengan ukuran yang lebih kecil")
            }
        })
    }

    companion object {
        fun newInstance(): ProfilePage = ProfilePage()
    }
}