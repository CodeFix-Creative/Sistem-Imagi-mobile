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
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.imagi.app.data.UserRepository
import com.imagi.app.model.User
import com.imagi.app.model.UserResponse
import com.imagi.app.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_profile_page.*


class ProfilePage : Fragment() {

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
        return myInflatedView
//        return inflater.inflate(R.layout.fragment_profile_page, container, false)
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
        buttonLogout.setBackgroundColor(R.color.red)

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
            val sharedPreferences: SharedPreferences? = this.activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
            sharedPreferences?.edit()?.remove("currentUser")?.apply()
            sharedPreferences?.edit()?.remove("authorization")?.apply()
            val intent = Intent(inflateView?.context,LoginActivity::class.java)
            startActivity(intent)

        }
        frame?.visibility = View.GONE
        loading?.visibility = View.VISIBLE
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

        val userRepository: UserRepository = UserRepository()

        userResult.observe(this@ProfilePage, Observer {
            val userResult = it ?: return@Observer

        })

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
            val userId = sharedPreferences.getString("currentUser", "")
            val auth = sharedPreferences.getString("authorization", "")
            val currentUser = gson.fromJson<User>(userId, User::class.java)
            if (auth != null) {
                userRepository.getDetailUser(currentUser.id, auth){
//                    Log.d("OBSERVER" , "BERHASIL")
                    Log.d("- 1", "${it}")
                    if(it?.code == 200){
                        loading?.visibility = View.GONE
//                        Log.d("- 2" , "BERHASIL")
                        address?.setText(it.data.alamat)
                        name?.setText(it.data.nama)
                        phone?.setText(it.data.no_telp)
                        nameHighlight.setText(it.data.nama)
                        frame?.visibility = View.VISIBLE
                    }
                    if(it?.code == 401){
                        Toast.makeText(context, "Unauthorized", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}