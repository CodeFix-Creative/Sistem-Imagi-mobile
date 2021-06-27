package com.imagi.app.ui.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imagi.app.model.Store
import com.imagi.app.model.User
import com.imagi.app.model.UserLogin
import com.imagi.app.network.DataManager
import timber.log.Timber
import javax.inject.Inject

class CoreViewModel @Inject constructor(private val dataManager: DataManager) : ViewModel() {

    val userLiveData: MutableLiveData<User> = MutableLiveData()
    var token : MutableLiveData<String> = MutableLiveData()
    val isShowLoader: MutableLiveData<Boolean> = MutableLiveData()
    val  errorMessage: MutableLiveData<String> = MutableLiveData()
    val storeLiveData: MutableLiveData<List<Store>> = MutableLiveData()
    val merchantLiveData: MutableLiveData<List<User>> = MutableLiveData()

    @Suppress("CheckResult")
    fun getProfile(token:String, id:String){
        isShowLoader.value = true
        Timber.d("CALL_PROFILE")
        dataManager.getProfile(token, id)
            .subscribe({result ->
                isShowLoader.value = false
                if(result.isSuccessful) {
                    Timber.d("Success")
                    Log.d("CHECKING","02")
                    val res = result.body()
                    Timber.d("${result.message()}")
                    Log.d("Success","${result.message()}")
                    Log.d("Success","${res?.data?.toString()}")
                    if (res?.success == true) {
                        res?.data.let {
                            userLiveData.value = it
                        }
                    } else {
                        errorMessage.value = res?.message
                    }

                }else{
                    Log.d("ERRROR","01")
                    errorMessage.value = "["+result.code()+"] sedang terjadi kendala. Cek kembali nanti"
                }

            }, {
                    error->
                isShowLoader.value = false
                Log.d("ERRROR","02")
                errorMessage.value = "Email dan Password tidak valid. Mohon cek kembali"
            })
    }

    @Suppress("CheckResult")
    fun postLogin(req: UserLogin){
        isShowLoader.value = true
        Timber.d("CALL_LOGIN")
        dataManager.postLogin(req)
            .subscribe({result ->
                isShowLoader.value = false
                if(result.isSuccessful) {
                    Timber.d("Success")
                    val res = result.body()
                    Timber.d("${result.message()}")
                    Log.d("Success","${result.message()}")
                    Log.d("Success","${res?.data?.email}")
                    if (res?.code == 200 && res?.data!=null) {
                        res?.data?.let {
                            userLiveData.value = res.data
                            Log.d("DATA_USER_API", "${res}")
                        }
                        token.value = res?.token
                    } else {
                        errorMessage.value = res?.message
                    }

                }else{
                    Log.d("ERRROR","01")
                    errorMessage.value = "["+result.code()+"] sedang terjadi kendala. Cek kembali nanti"
                }

            }, {
                error->
                    isShowLoader.value = false
                Log.d("ERRROR","02")
                    errorMessage.value = "Email dan Password tidak valid. Mohon cek kembali"
            })
    }

    @Suppress("CheckResult")
    fun getStore(token:String){
        isShowLoader.value = true
        Timber.d("GET_DATA_STORE")
        dataManager.getStore(token)
            .subscribe ({ result ->
                isShowLoader.value = false
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_STORE")
                    val res = result.body()
                    if(res?.code == 200){
                        storeLiveData.value = res.data
                    }
                    Timber.d("JUMLAH_TOKO ${result.body()?.data?.size}")
                    Timber.d("JUMLAH_TOKO ${storeLiveData.value}")
                }else{
                    errorMessage.value = "["+result.code()+"] sedang terjadi kendala. Cek kembali nanti"
                }
            },
                { error->
                    isShowLoader.value = false
                    errorMessage.value = error?.message
            })
    }

    @Suppress("CheckResult")
    fun getMerchant(token:String){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT")
        dataManager.getMerchant(token)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_MERCHANT ${isShowLoader.value}")
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_MERCHANT")
                    val res = result.body()

                    if(res?.code == 200){
                        merchantLiveData.value = res?.data
                    }

                }else{
                    errorMessage.value = "["+result.code()+"] sedang terjadi kendala. Cek kembali nanti"
                }
            },
                { error->
                    isShowLoader.value = false
                    errorMessage.value = error?.message
            })
    }

}