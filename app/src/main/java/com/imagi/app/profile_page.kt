package com.imagi.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.imagi.app.model.User
import com.imagi.app.model.UserForm
import com.imagi.app.model.UserResponse
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.ui.login.LoginActivity
import com.imagi.app.util.AppUtils
import com.imagi.app.util.URIPathHelper
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_store_merhcnat.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class ProfilePage : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices

    lateinit var address : EditText
    lateinit var name : EditText
    lateinit var phone : EditText
    lateinit var password : EditText
    lateinit var nameHighlight : TextView
    lateinit var loading : ProgressBar
    lateinit var frame : RelativeLayout
    lateinit var buttonLogout : Button
    lateinit var saveChange : Button
    lateinit var refreshProfile : SwipeRefreshLayout

    lateinit var safeUser : User
    lateinit var authKey : String
    private val pickImage = 100
    private val PERMISSION_REQUEST_CODE = 200
    private var imageUri: Uri? = null
    val resource = context?.resources
    val _userResult = MutableLiveData<UserResponse>()
    val userResult: LiveData<UserResponse> = _userResult
    val uriPathHelper = URIPathHelper()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myInflatedView: View = inflater.inflate(R.layout.fragment_profile_page, container, false)
        initializeFragment(myInflatedView)
        dbServices.mContext  = context
        return myInflatedView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dbServices = DbServices(getContext())

        saveChange.setOnClickListener {
            if(validateForm(name.text.toString(), phone.text.toString(), address.text.toString())){
                Timber.d("TOKEN : ${dbServices.findBearerToken()}")
                viewModel.putProfile(
                    dbServices.findBearerToken(),
                    dbServices.getUser().id_customer.toString(),
                    UserForm(
                        nama = name.text.toString(),
                        no_telp = phone.text.toString(),
                        alamat = address.text.toString()
                    )
                )
            }
        }

//        vc_merchant_photo.setOnClickListener {
//            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//            startActivityForResult(gallery, pickImage)
//        }

        observerViewModel()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == AppCompatActivity.RESULT_OK && requestCode == pickImage){
            imageUri = data?.data
            vc_merchant_photo.setImageURI(imageUri)
        }
    }

    fun prepareFilePart(name: String, fileUri: Uri) : MultipartBody.Part {
        var file : File = File(activity?.let { uriPathHelper.getPath(it, fileUri) })

        Log.d("FILENAME", "${file.name}")
        var body = RequestBody.create(MediaType.parse(activity?.contentResolver?.getType(fileUri)), file)

        return MultipartBody.Part.createFormData("foto", file.name, body)
    }

    private fun validateForm(nama:String, phone:String, address:String,): Boolean {
        return if(TextUtils.isEmpty(nama)){
            activity?.let { AppUtils.showAlert(it, "Mohon mengisi nama user") }
            false
        } else if(TextUtils.isEmpty(phone)){
            activity?.let { AppUtils.showAlert(it, "Mohon mengisi nomor telepon user") }
            false
        }else if(TextUtils.isEmpty(address)){
            activity?.let { AppUtils.showAlert(it, "Mohon mengisi alamat user") }
            false
        }else{
            true
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)
        dbServices = DbServices(getContext())

        try{
            viewModel.getProfile(dbServices.findBearerToken(), dbServices.getUser().user_id.toString())
        }catch (e:Exception){
            Timber.d("${e.message}")
        }

    }

    private fun observerViewModel(){

        viewModel.isShowLoader.observe(viewLifecycleOwner, {
            if(it){
                loading?.visibility = View.VISIBLE
            }else{
                loading?.visibility = View.GONE
            }
        })

        viewModel.userLiveData.observe(viewLifecycleOwner, {
            loading?.visibility = View.GONE
//                        Log.d("- 2" , "BERHASIL")
            address?.setText(viewModel.userLiveData.value?.alamat)
            name?.setText(viewModel.userLiveData.value?.nama)
            phone?.setText(viewModel.userLiveData.value?.no_telp)
            nameHighlight.setText(viewModel.userLiveData.value?.nama)
            frame?.visibility = View.VISIBLE
        })

    }



    companion object {
        fun newInstance(): ProfilePage = ProfilePage()
    }

    private fun initializeFragment(inflateView : View){
        address = inflateView?.findViewById<EditText>(R.id.addressEditProfile)
        name = inflateView?.findViewById<EditText>(R.id.nameEditProfile)
        phone = inflateView?.findViewById<EditText>(R.id.phoneNumberEditProfile)
        password = inflateView?.findViewById<EditText>(R.id.passwordEditProfile)
        nameHighlight = inflateView?.findViewById<TextView>(R.id.nameOfUser)
        loading = inflateView?.findViewById<ProgressBar>(R.id.loadingProfile)
        frame = inflateView?.findViewById<RelativeLayout>(R.id.side_user)
        refreshProfile = inflateView?.findViewById<SwipeRefreshLayout>(R.id.refreshProfile)
        buttonLogout = inflateView?.findViewById(R.id.logout)
        saveChange = inflateView?.findViewById(R.id.editButton)
//        buttonLogout.setBackgroundColor(R.color.red)

        refreshProfile.setOnRefreshListener {
            frame?.visibility = View.GONE
            loading?.visibility = View.VISIBLE
            Handler().postDelayed(Runnable {
                refreshProfile.isRefreshing = false
                frame?.visibility = View.VISIBLE
                loading?.visibility = View.GONE
            }, 1000)
        }

        buttonLogout.setOnClickListener {
            dbServices?.logout()
            val intent = Intent (it.context, LoginActivity::class.java)
            startActivity(intent)

        }

    }

//    private fun callMyProfile() : Boolean {
//        val sharedPreferences: SharedPreferences =
//            (this.activity?.getSharedPreferences("user", Context.MODE_PRIVATE) ?: Log.d(
//                "ADA_USER",
//                "TES"
//            )) as SharedPreferences
////        Log.d("ADA_USER", "TES")
////        Log.d("ADA_USER", "${sharedPreferences.contains("currentUser")}")
//        if(sharedPreferences.contains("currentUser")
//        ){
//            val gson = Gson()
//            val user = sharedPreferences.getString("currentUser", "")
//            this.authKey = sharedPreferences.getString("authorization", "").toString()
//            this.safeUser  = gson.fromJson<User>(user, User::class.java)
//            if (authKey != null) {
//                return true
//            }
//        }
//        return false
//    }



}