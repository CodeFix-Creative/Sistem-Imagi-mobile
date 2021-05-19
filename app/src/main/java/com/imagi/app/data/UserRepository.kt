package com.imagi.app.data

import android.content.SharedPreferences
import android.util.Log
import com.imagi.app.model.User
import com.imagi.app.model.UserResponse
import com.imagi.app.network.ImageApi
import org.jetbrains.annotations.NotNull
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class UserRepository {

    var user: User? = null
        private set


    init {
        user = null
    }

    fun getDetailUser(@NotNull id:Int, @NotNull authToken : String, onResult: (UserResponse?)->Unit){

        Log.d("Try Get user detail" , "GET_USER_DETAIL")
        ImageApi.retrofitService.getUser("Bearer $authToken", id).enqueue(
            object : Callback, retrofit2.Callback<UserResponse>{
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    onResult(response.body())
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    onResult(null)
                    Log.d("API_CAUSE", t.cause.toString())
                    Log.d("API_MESSAGE", t.message.toString())
                }

            }
        )
    }
}