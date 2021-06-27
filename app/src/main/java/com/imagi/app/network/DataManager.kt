package com.imagi.app.network

import com.imagi.app.model.*
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
       return api.login(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getProfile(token:String, id:String) : Single<retrofit2.Response<UserResponse>>{
        var data = api.getUser(token)
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

    fun getStore(token:String) : Single<retrofit2.Response<StoreResponse>>{
        return api.getStore(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getMerchant(token:String) : Single<retrofit2.Response<MerchantResponse>>{
        return api.getMerchant(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getDetailStore(token:String, id:String) : Single<retrofit2.Response<StoreDetailResponse>>{
        return api.getStoreDetail(token, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllReview(token: String, id: String) :Single<retrofit2.Response<ReviewResponse>>{
        return api.getAllReviewStore(token, id)
            .subscribeOn(
                Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun postReview(token: String, id: String, review: ReviewForm) :Single<retrofit2.Response<ReviewPostResponse>>{
        return api.postReview(token, id, review)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getProductStore(token:String, id:String) : Single<retrofit2.Response<ProductListenResponse>>{
        return api.getProductStore(token, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}