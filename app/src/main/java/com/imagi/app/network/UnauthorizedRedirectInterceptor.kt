package com.imagi.app.network

import android.content.Intent
import com.imagi.app.ui.login.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response

class UnauthorizedRedirectInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val appContext = ImagiApp.applicationContext()
        val dbService = DbServices(appContext)
        val response = chain.proceed(chain.request())
        if(response.code() == 401){
            dbService.logout()
            val intent = Intent(appContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            appContext.startActivity(intent)
        }
        return response
    }


}