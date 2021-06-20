package com.imagi.app.network

import com.imagi.app.model.UserLogin
import com.imagi.app.model.UserResponse
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataManager
@Inject constructor(private  val api: ImagiApiService) {

    fun postLogin(data:UserLogin) : Single<retrofit2.Response<UserResponse>>{
        var data = api.login(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        Timber.d("RESULT_API_LOGIN")
        var resultSet:String = ""
        data.subscribe {
            result-> resultSet = result.message().toString()
        }

        Timber.d(resultSet)

        return data
    }

    fun getProfile(token:String, id:String) : Single<retrofit2.Response<UserResponse>>{
        var data = api.getUser(token, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        Timber.d("RESULT_API_PROFILE")
        var resultSet:String = ""
        data.subscribe {
                result-> resultSet = result.message().toString()
        }

        Timber.d(resultSet)

        return data

    }

}