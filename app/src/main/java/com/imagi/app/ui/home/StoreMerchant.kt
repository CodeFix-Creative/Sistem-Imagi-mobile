package com.imagi.app.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
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
import com.imagi.app.DetailMarket
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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class StoreMerchant : AppCompatActivity() , HasSupportFragmentInjector {

    lateinit var id : String;

    @Inject
    lateinit var frahmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices

    lateinit var progress : ProgressBar
    lateinit var listReview : RecyclerView

    private val pickImage = 100
    private val PERMISSION_REQUEST_CODE = 200
    private var imageUri: Uri? = null

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

        progress = findViewById(R.id.progressBarHome)
        listReview = findViewById(R.id.rvMarket)
        if (checkPermission()) {
            //main logic or main code

            // . write your main code to execute, It will execute if the permission is already given.

        } else {
            requestPermission();
        }

        fab.setOnClickListener {
            vc_dialog_form.visibility = View.VISIBLE
        }

        vc_merchant_photo.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        vc_btn_save.setOnClickListener {
            if(validate(
                    vc_merchant_name.text.toString(),
                    vc_merchant_phone.text.toString(),
                    vc_merchant_address.text.toString()
                )){

                var map = HashMap<String, RequestBody>()
                toRequestBody(id)?.let { it1 -> map.put("pedagang_id", it1) }
                map["nama_toko"] = toRequestBody(vc_merchant_name.text.toString())
                map["no_telp"] = toRequestBody(vc_merchant_phone.text.toString())
                map["alamat_toko"] = toRequestBody(vc_merchant_address.text.toString())
                map["latitude"] = toRequestBody("44.968046")
                map["longitude"] = toRequestBody("-94.420307")
                map["facebook"] = toRequestBody("-")
                map["twitter"] = toRequestBody("-")
                map["instagram"] = toRequestBody("-")
                map["website"] = toRequestBody("-")
                val file = File(imageUri?.path)
//                val file1 = File(file.absolutePath)
//                val requestFile =
//                    RequestBody.create(MediaType.parse("multipart/form-data"), file1)
//                val body = MultipartBody.Part.createFormData("image", file1.name, requestFile)
                val body = imageUri?.let { it1 -> prepareFilePart(file.name, it1) }
                if(imageUri!=null){

//                    var file = File(imageUri!!.path)
//                    var fileBody = RequestBody.create(MediaType.parse("image/png"), file)
//                    map.put("foto", fileBody)

//                    MultipartBody.Part
                }
                Timber.d("data : ${map.toString()}")

                viewModel.postStore(
                    dbServices.findBearerToken(), map, body
                )
//                viewModel.postStore(
//                    dbServices.findBearerToken(), StoreForm(
//                        pedagang_id = id,
//                        nama_toko = vc_merchant_name.text.toString(),
//                        no_telp = vc_merchant_phone.text.toString(),
//                        alamat_toko = vc_merchant_address.text.toString(),
//                        latitude = "44.968046",
//                        longitude = "-94.420307"
//                    ), null
//                )
            }
        }

        if(intent.extras != null)
        {
            val bundle = intent.extras
            id = bundle?.getString("id")!!
        }else{
            Timber.d("FAIL_GET_DATA")
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

                // main logic
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        AlertDialog.Builder(this).setMessage("Permission camera diperlukan")
                            .setPositiveButton("OK", {dialogInterface, i->
                                requestPermission()
                            })
                            .create().show()
                    }
                }
            }
        }
    }



    private fun checkPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            false
        } else true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    fun prepareFilePart(name: String, fileUri: Uri) : MultipartBody.Part {
        var file : File = File(uriPathHelper.getPath(this, fileUri))

        var body = RequestBody.create(MediaType.parse(contentResolver.getType(fileUri)), file)

        return MultipartBody.Part.createFormData(name, file.name, body)
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
            val list = listReview
            list.invalidate()

            val adapters = MarketAdapter(it) {
                val bundle = Bundle()
                bundle.putString("id", id)
                val intent = Intent(this, DetailMarket::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            list.layoutManager = GridLayoutManager(this, 2)
            adapters.notifyDataSetChanged()
            list.adapter = adapters
        })
    }
}