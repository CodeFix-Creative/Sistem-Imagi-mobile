package com.imagi.app.network

import android.content.Intent
import android.util.Log
import com.imagi.app.ImagiApp
import com.imagi.app.ui.login.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response

class UnauthorizedRedirectInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val appContext = ImagiApp.applicationContext()
        val dbService = DbServices(appContext)
        val response = chain.proceed(chain.request())
        Log.d("LOG_RESPONSE_CODE", "${response.code()}")
        Log.d("LOG_RESPONSE", "${response.body()}")
        if(response.code() == 401){
            dbService.logout()
            val intent = Intent(appContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            appContext.startActivity(intent)
        }
        return response
    }


}