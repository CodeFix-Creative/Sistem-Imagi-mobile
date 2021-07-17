package com.imagi.app.ui.home

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil.setContentView
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
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_store_merhcnat.*
import kotlinx.android.synthetic.main.activity_store_merhcnat.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class StoreMerchantRetail : Fragment() , HasSupportFragmentInjector {

    lateinit var id : String;

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var frahmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices
    lateinit var currentview:View

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

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myInflatedView: View = inflater.inflate(R.layout.activity_store_merhcnat, container, false)
        this.currentview = myInflatedView
            dbServices = DbServices(activity)
        dbServices.mContext = activity
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)
        dbServices.mContext  = context
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        progress = myInflatedView?.findViewById(R.id.progressBarHome)
        listReview = myInflatedView.findViewById(R.id.rvMarket)

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

        myInflatedView.fab.setOnClickListener {
            myInflatedView.vc_dialog_form.visibility = View.VISIBLE
            myInflatedView.fab.visibility = View.GONE
//            bg_main.setBackgroundColor(R.color.blackSoft)
            this.onClick = !onClick
        }

        myInflatedView.vc_merchant_photo.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        myInflatedView.vc_btn_close.setOnClickListener {
//            bg_main.setBackgroundColor(Color.parseColor("#FFFFFFF"))
            myInflatedView.vc_dialog_form.visibility = View.GONE
            myInflatedView.fab.visibility = View.VISIBLE
        }

        myInflatedView.vc_btn_save.setOnClickListener {
            if(validate(
                    myInflatedView.vc_merchant_name.text.toString(),
                    myInflatedView.vc_merchant_phone.text.toString(),
                    myInflatedView.vc_merchant_address.text.toString()
                )){

                var map = HashMap<String, RequestBody>()
                toRequestBody(id)?.let { it1 -> map.put("pedagang_id", it1) }
                map["nama_toko"] = toRequestBody(myInflatedView.vc_merchant_name.text.toString())
                map["no_telp"] = toRequestBody(myInflatedView.vc_merchant_phone.text.toString())
                map["alamat_toko"] = toRequestBody(myInflatedView.vc_merchant_address.text.toString())
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

                Timber.d("DATA_LATITUDE : $latitude")
                Timber.d("DATA_LONGITUDE : $longitude")
                if(body!=null) {
                    viewModel.postStore(
                        dbServices.findBearerToken(), map, body
                    )
                }else{
                    viewModel.postStoreWithoutImage(
                        dbServices.findBearerToken(), map
                    )
                }
            }
        }


        if(dbServices.user.role == "Pedagang"){
            this.id = dbServices.user.id_pedagang.toString()
        }

        observerViewModel();

        return myInflatedView
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show()
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        this.latitude = location?.latitude
                        this.longitude = location?.longitude
                        Timber.d("GET_LAST_LOCATION")
                    }
                // main logic
            } else {
                Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity?.let {
                            ContextCompat.checkSelfPermission(
                                it,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        }
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        AlertDialog.Builder(activity).setMessage("Permission camera diperlukan")
                            .setPositiveButton("OK", { dialogInterface, i ->
                                requestPermission()
                            })
                            .create().show()
                    }

                    if (activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {
                        AlertDialog.Builder(activity).setMessage("Permission akses lokasi diperlukan")
                            .setPositiveButton("OK", { dialogInterface, i ->
                                requestPermission()
                            })
                            .create().show()
                    }
                    if (activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {
                        AlertDialog.Builder(activity).setMessage("Permission akses lokasi diperlukan")
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
        return if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } != PackageManager.PERMISSION_GRANTED) {
            false
        } else if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            false
        }else if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
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
        activity?.let {
            ActivityCompat.requestPermissions(
                it, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    fun prepareFilePart(name: String, fileUri: Uri) : MultipartBody.Part {
        var file : File = File(activity?.let { uriPathHelper.getPath(it, fileUri) })

        Log.d("FILENAME", "${file.name}")
        var body = RequestBody.create(MediaType.parse(activity?.contentResolver?.getType(fileUri)), file)

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
            activity?.let { AppUtils.showAlert(it, "Mohon melengkapi nama toko") }
            false
        } else if(TextUtils.isEmpty(phone)){
            activity?.let { AppUtils.showAlert(it, "Mohon melengkapi nomor telepon toko") }
            false
        }else if(TextUtils.isEmpty(address)){
            activity?.let { AppUtils.showAlert(it, "Mohon melengkapi alamat toko") }
            false
        } else {
            true
        }
    }

    private fun observerViewModel(){

        if(dbServices.user.role == "Pedagang"){
            currentview.fab.visibility = View.VISIBLE
        }

        viewModel.getStoreMerchant(dbServices.findBearerToken(), id)

        activity?.let {
            viewModel.isShowLoader.observe(it, {
                if (it) {
                    progress.visibility = View.VISIBLE
                    listReview.visibility = View.GONE
                } else {
                    progress.visibility = View.GONE
                    listReview.visibility = View.VISIBLE
                }
            })
        }

        activity?.let {
            viewModel.errorMessage.observe(it, {
                activity?.let { it1 -> AppUtils.showAlert(it1, it) };
            })
        }

        activity?.let {
            viewModel.storeLiveData.observe(it, {
                val list = listReview
                list.invalidate()

                val adapters = MarketAdapter(it) {
                    val bundle = Bundle()
                    bundle.putString("id", it.toko_id.toString())
                    val intent = Intent(activity, DetailMarket::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }

                list.layoutManager = GridLayoutManager(activity, 2)
                adapters.notifyDataSetChanged()
                list.adapter = adapters
            })
        }

        activity?.let {
            viewModel.code.observe(it, {
                if (it == 201) {
                    vc_dialog_form.visibility = View.GONE
                    fab.visibility = View.VISIBLE
                    viewModel.getStoreMerchant(dbServices.findBearerToken(), id)
                }
                if (it == 422) {
                    AppUtils.showAlert(
                        requireActivity(), "Gambar yang ada upload terlalu besar, mohon menggunkkan gambar" +
                            "dengan ukuran yang lebih kecil")
                }
            })
        }
    }

    companion object {
        fun newInstance(): ProfilePage = ProfilePage()
    }
}