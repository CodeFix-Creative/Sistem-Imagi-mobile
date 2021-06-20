package com.imagi.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.imagi.app.model.User
import com.imagi.app.model.UserResponse
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.ui.login.LoginActivity
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber
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
    lateinit var refreshProfile : SwipeRefreshLayout

    lateinit var safeUser : User
    lateinit var authKey : String
    val resource = context?.resources
    val _userResult = MutableLiveData<UserResponse>()
    val userResult: LiveData<UserResponse> = _userResult


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
        observerViewModel()

    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)
        dbServices = DbServices(getContext())

        try{
            viewModel.getProfile(dbServices.findBearerToken(), dbServices.getUser().id.toString())
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

    private fun callMyProfile() : Boolean {
        val sharedPreferences: SharedPreferences =
            (this.activity?.getSharedPreferences("user", Context.MODE_PRIVATE) ?: Log.d(
                "ADA_USER",
                "TES"
            )) as SharedPreferences
//        Log.d("ADA_USER", "TES")
//        Log.d("ADA_USER", "${sharedPreferences.contains("currentUser")}")
        if(sharedPreferences.contains("currentUser")
        ){
            val gson = Gson()
            val user = sharedPreferences.getString("currentUser", "")
            this.authKey = sharedPreferences.getString("authorization", "").toString()
            this.safeUser  = gson.fromJson<User>(user, User::class.java)
            if (authKey != null) {
                return true
            }
        }
        return false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val userRepository: UserRepository = UserRepository()
//
//        userResult.observe(this@ProfilePage, Observer {
//            val userResult = it ?: return@Observer
//
//        })

//        if(refreshProfile.isNotEmpty()){
//            if(refreshProfile?.isRefreshing){
//                if(callMyProfile()){
//                    userRepository.getDetailUser(safeUser.id, authKey){
////                    Log.d("OBSERVER" , "BERHASIL")
//                        Log.d("- 1", "${it}")
//                        if(it?.code == 200){
//                            loading?.visibility = View.GONE
////                        Log.d("- 2" , "BERHASIL")
//                            address?.setText(it.data.alamat)
//                            name?.setText(it.data.nama)
//                            phone?.setText(it.data.no_telp)
//                            nameHighlight.setText(it.data.nama)
//                            frame?.visibility = View.VISIBLE
//                        }
//                        if(it?.code == 401){
//                            Toast.makeText(context, "Unauthorized", Toast.LENGTH_LONG).show()
//                        }
//                    }
//                }
//            }
//        }



//        val sharedPreferences: SharedPreferences =
//            (this.activity?.getSharedPreferences("user", Context.MODE_PRIVATE) ?: Log.d(
//                "ADA_USER",
//                "TES"
//            )) as SharedPreferences
////        Log.d("ADA_USER", "TES")
////        Log.d("ADA_USER", "${sharedPreferences.contains("currentUser")}")
//        if(sharedPreferences.contains(Constant.SP_USER)
//        ){
//            val gson = Gson()
//            val userId = sharedPreferences.getString(Constant.SP_USER, "")
//            val auth = sharedPreferences.getString(Constant.SP_TOKEN, "")
//            val currentUser = gson.fromJson<User>(userId, User::class.java)
//            if (auth != null) {
//                userRepository.getDetailUser(currentUser.id, auth){
////                    Log.d("OBSERVER" , "BERHASIL")
//                    Log.d("- 1", "${it}")
//                    if(it?.code == 200){
//                        loading?.visibility = View.GONE
////                        Log.d("- 2" , "BERHASIL")
//                        address?.setText(it.data.alamat)
//                        name?.setText(it.data.nama)
//                        phone?.setText(it.data.no_telp)
//                        nameHighlight.setText(it.data.nama)
//                        frame?.visibility = View.VISIBLE
//                    }
//                    if(it?.code == 401){
//                        Toast.makeText(context, "Unauthorized", Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//        }
    }

}