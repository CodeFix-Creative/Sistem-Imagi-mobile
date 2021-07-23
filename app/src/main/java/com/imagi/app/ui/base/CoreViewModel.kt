package com.imagi.app.ui.base

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imagi.app.model.*
import com.imagi.app.network.DataManager
import com.imagi.app.util.AppUtils
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject

class CoreViewModel @Inject constructor(private val dataManager: DataManager) : ViewModel() {

    val userLiveData: MutableLiveData<User> = MutableLiveData()
    var token : MutableLiveData<String> = MutableLiveData()
    val isShowLoader: MutableLiveData<Boolean> = MutableLiveData()
    val  errorMessage: MutableLiveData<String> = MutableLiveData()
    val storeLiveData: MutableLiveData<List<Store>> = MutableLiveData()
    val merchantLiveData: MutableLiveData<List<User>> = MutableLiveData()
    val storeDetailLiveData: MutableLiveData<Store> = MutableLiveData()
    val reviewDetailLiveData: MutableLiveData<Review> = MutableLiveData()
    val reviewLiveData: MutableLiveData<List<Review>> = MutableLiveData()
    var productLiveData: MutableLiveData<List<Product>> = MutableLiveData()
    val product: MutableLiveData<Product> = MutableLiveData()
    val code: MutableLiveData<Int> = MutableLiveData()
    val localMarkerLiveData: MutableLiveData<List<LocalMarker>> = MutableLiveData()
    var data: ArrayList<LocalMarker> = ArrayList<LocalMarker>()
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
    fun getStoreByMap(token:String){
        isShowLoader.value = true
        dataManager.getStore(token)
            .subscribe ({ result ->
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_STORE")
                    val res = result.body()
                    if(res?.code == 200){
                        storeLiveData.value = res.data
                        for(it in res.data) {
                            data.add(LocalMarker(
                                latitude = it.latitude?.toDoubleOrNull(),
                                longitude = it.longitude?.toDoubleOrNull(),
                                name = it.nama_toko,
                                id = it.toko_id.toString()
                            ))
                        }
                        localMarkerLiveData.postValue(data)
                    }
                    isShowLoader.value = false
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

    @Suppress("CheckResult")
    fun getStoreDetail(token:String, id:String){
        isShowLoader.value = true
        Timber.d("GET_DATA_DETAIL_STORE")
        dataManager.getDetailStore(token, id)
            .subscribe({ result->
                isShowLoader.value = false
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_DATA_DETAIL_STORE")
                    val res = result?.body()

                    if(res?.code == 200){
                        storeDetailLiveData.value = res?.data
                    }
                }else{
                    errorMessage.value = "["+result.code()+"] sedang terjadi kendala. Cek kembali nanti"
                }

            },{
                error->
                    isShowLoader.value = false
                    errorMessage.value = error.message
            })
    }


    @Suppress("CheckResult")
    fun getReview(token:String, id:String){
        isShowLoader.value = true
        Timber.d("GET_DATA_REVIEW")
        dataManager.getAllReview(token, id)
            .subscribe({ result->
                isShowLoader.value = false
                this.code.value = result.code()
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_DATA_REVIEW")
                    val res = result?.body()

                    if(res?.code == 200){
                        reviewLiveData.value = res?.data
                    }
                }else{
                    if(result.code() != 404){
                        errorMessage.value =
                            "[" + result.code() + "] sedang terjadi kendala. Cek kembali nanti"
                    }
                }

            },{
                error->
                    isShowLoader.value = false
                    errorMessage.value = error.message
            })
    }

    @Suppress("CheckResult")
    fun postReview(token:String, id:String, review:ReviewForm, context: Context){
        isShowLoader.value = true
        Timber.d("POST_DATA_REVIEW")
        dataManager.postReview(token, id, review)
            .subscribe({ result->
                isShowLoader.value = false
                if(result.isSuccessful){
                    Timber.d("SUCCESS_POST_DATA_REVIEW")
                    val res = result?.body()

                    if(res?.code == 200){
                        reviewDetailLiveData.value = res?.data
                        AppUtils.showAlert(context, "Review Berhasil Ditambahkan")
                    }
                }else{
                    errorMessage.value = "["+result.code()+"] sedang terjadi kendala. Cek kembali nanti"
                }

            },{
                error->
                    isShowLoader.value = false
                    errorMessage.value = error.message
            })
    }

    @Suppress("CheckResult")
    fun getStoreProduct(token:String, id: String){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT")
        dataManager.getProductStore(token, id)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_PRODUCT ${isShowLoader.value}")
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_PRODUCT")
                    val res = result.body()

                    if(res?.code == 200){
                        productLiveData.value = res?.data
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

    @Suppress("CheckResult")
    fun getGlobalSearch(token:String, query: String, queryMin:String?, queryMax:String?,){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT")
        dataManager.getProductSearch(token, query, queryMin, queryMax)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_PRODUCT ${isShowLoader.value}")
                this.code.value = result.code()
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_PRODUCT")
                    val res = result.body()

                    if(res?.code == 200){
                        this.productLiveData.value = res?.data
                    }

                }else{
                    this.productLiveData = MutableLiveData<List<Product>>()
                    Timber.d("DATA PRODUCT ${productLiveData.value?.isNotEmpty()}")
                    errorMessage.value = "["+result.code()+"] Barang tidak ditemukan"
                }
            },
                { error->
                    isShowLoader.value = false
                    errorMessage.value = error?.message
                })
    }

    @Suppress("CheckResult")
    fun putProfile(token:String, content:Map<String, RequestBody>, file:MultipartBody.Part){
        isShowLoader.value = true
        Timber.d("PUT_DATA_USER")
        dataManager.putProfile(token, content, file)
            .subscribe({ result->
                isShowLoader.value = false
                this.code.value = result.code()
                if(result.isSuccessful){
                    Timber.d("SUCCESS_PUT_DATA_USER")
                    val res = result?.body()

                    if(res?.code == 200){
                        userLiveData.value = res?.data
                    }
                }else{
                    errorMessage.value = "["+result.code()+"] sedang terjadi kendala. Cek kembali nanti"
                }

            },{
                    error->
                isShowLoader.value = false
                errorMessage.value = error.message
            })
    }

    @Suppress("CheckResult")
    fun putProfileWithoutImage(token:String, content:Map<String, RequestBody>){
        isShowLoader.value = true
        Timber.d("PUT_DATA_USER")
        dataManager.putProfileWithoutImage(token, content)
            .subscribe({ result->
                isShowLoader.value = false
                this.code.value = result.code()
                if(result.isSuccessful){
                    Timber.d("SUCCESS_PUT_DATA_USER")
                    val res = result?.body()

                    if(res?.code == 200){
                        userLiveData.value = res?.data
                    }
                }else{
                    errorMessage.value = "["+result.code()+"] sedang terjadi kendala. Cek kembali nanti"
                }

            },{
                    error->
                isShowLoader.value = false
                errorMessage.value = error.message
            })
    }

    @Suppress("CheckResult")
    fun getStoreMerchant(token:String, id: String){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT_STORE")
        dataManager.getStoreMerchant(token, id)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_PRODUCT ${isShowLoader.value}")
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_STORE")
                    val res = result.body()

                    if(res?.code == 200){
                        storeLiveData.value = res?.data?.toko
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

    @Suppress("CheckResult")
    fun postProduct(token:String, productForm: ProductForm){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT_STORE")
        dataManager.postProduct(token, productForm)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_PRODUCT ${isShowLoader.value}")
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_STORE")
                    val res = result.body()
                    this.code.value = res?.code
                    if(res?.code == 201){
                        product.value = res?.data
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

    @Suppress("CheckResult")
    fun putProduct(token:String, id:String, productForm: ProductForm){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT_STORE")
        dataManager.putProduct(token, id, productForm)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_PRODUCT ${isShowLoader.value}")
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_STORE")
                    val res = result.body()
                    this.code.value = res?.code
                    if(res?.code == 200){
                        product.value = res?.data
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

    @Suppress("CheckResult")
    fun getProduct(token:String, id:String){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT_STORE")
        dataManager.getProduct(token, id)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_PRODUCT ${isShowLoader.value}")
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_STORE")
                    val res = result.body()

                    if(res?.code == 200){
                        product.value = res?.data
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

    @Suppress("CheckResult")
    fun deleteProduct(token:String, id:String){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT_STORE")
        dataManager.deleteProduct(token, id)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_PRODUCT ${isShowLoader.value}")
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_STORE")
                    val res = result.body()
                    this.code.value = res?.code
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
    fun postReplay(token:String,id:String, content:ReplayForm){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT_STORE")
        dataManager.postReplay(token, id, content)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_PRODUCT ${isShowLoader.value}")
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_STORE")
                    val res = result.body()
                    this.code.value = res?.code
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
    fun postStore(token:String, content:Map<String, RequestBody>, image: MultipartBody.Part?){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT_STORE")
        dataManager.postStore(token,content, image!!)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_PRODUCT ${isShowLoader.value}")
                this.code.value = result.code()
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_STORE")
                    val res = result.body()
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
    fun postStoreWithoutImage(token:String, content:Map<String, RequestBody>){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT_STORE")
        dataManager.postStoreWithImage(token,content)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_PRODUCT ${isShowLoader.value}")
                this.code.value = result.code()
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_STORE")
                    val res = result.body()
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
    fun putStore(token:String,id:String, content:Map<String, RequestBody>, file:MultipartBody.Part){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT_STORE")
        dataManager.putStore(token, id, content, file)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_PRODUCT ${isShowLoader.value}")
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_STORE")
                    val res = result.body()
                    this.code.value = res?.code
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
    fun putStoreWithoutImage(token:String,id:String, content:Map<String, RequestBody>){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT_STORE")
        dataManager.putStoreWithoutImage(token, id, content)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_PRODUCT ${isShowLoader.value}")
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_STORE")
                    val res = result.body()
                    this.code.value = res?.code
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
    fun deleteStore(token:String,id:String){
        isShowLoader.value = true
        Timber.d("GET_DATA_MERCHANT_STORE")
        dataManager.deleteStore(token, id)
            .subscribe ({ result ->
                isShowLoader.value = false
                Timber.d("TRY_GET_PRODUCT ${isShowLoader.value}")
                if(result.isSuccessful){
                    Timber.d("SUCCESS_GET_STORE")
                    val res = result.body()
                    this.code.value = res?.code
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