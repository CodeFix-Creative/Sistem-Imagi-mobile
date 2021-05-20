package com.imagi.app

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.imagi.app.data.UserRepository
import com.imagi.app.model.User
import com.imagi.app.model.UserResponse
import kotlinx.android.synthetic.main.fragment_profile_page.*


class ProfilePage : Fragment() {

    lateinit var address : EditText
    lateinit var name : EditText
    lateinit var phone : EditText
    lateinit var password : EditText
    lateinit var nameHighlight : TextView
    lateinit var loading : ProgressBar
    lateinit var frame : RelativeLayout
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
        frame?.visibility = View.GONE
        loading?.visibility = View.VISIBLE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userRepository: UserRepository = UserRepository()

        userResult.observe(this@ProfilePage, Observer {
            val userResult = it ?: return@Observer

            if (userResult.code == 200) {

            }
        })


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