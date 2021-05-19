package com.imagi.app

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.gson.Gson
import com.imagi.app.data.UserRepository
import com.imagi.app.model.User

class MainProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_profile)

        val userRepository: UserRepository = UserRepository()

        val sharedPreferences: SharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)

        Log.d("ADA_USER", "${sharedPreferences.contains("user")}")

        if(sharedPreferences.contains("user")
        ){
            val gson = Gson()
            val name = findViewById<TextView>(R.id.nameOfUser)
            val address = findViewById<TextView>(R.id.address)
            val userId = sharedPreferences.getString("currentUser","")
            val auth = sharedPreferences.getString("authorization","")
            val currentUser = gson.fromJson<User>(userId, User::class.java)
            if (auth != null) {
                userRepository.getDetailUser(currentUser.id, auth){
                    if(it?.code == 200){
                        name.text = it.data.nama
                        address.text = it.data.alamat
                    }
                }
            }
        }

    }
}