package com.imagi.app.data

import android.util.Log
import com.imagi.app.data.model.LoggedInUser
import com.imagi.app.model.UserLogin
import com.imagi.app.model.UserResponse
import com.imagi.app.network.ImageApi
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(form : UserLogin, onResult: (UserResponse?)-> Unit) {
        // handle login
        ImageApi.retrofitService.login(form).enqueue(
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

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}