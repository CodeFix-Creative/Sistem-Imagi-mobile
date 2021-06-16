package com.imagi.app.ui.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
                    if (res?.success == true) {
                        res?.data.let {
                            userLiveData.value = it
                        }
                        token.value = res?.token
                    } else {
                        errorMessage.value = res?.message
                    }

                }else{
                    errorMessage.value = "["+result.code()+"] sedang terjadi kendala. Cek kembali nanti"
                }

            }, {
                error->
                    isShowLoader.value = false
                    errorMessage.value = "Email dan Password tidak valid. Mohon cek kembali"
            })
    }
}