package com.imagi.app.network

import com.google.gson.GsonBuilder
import com.imagi.app.model.*
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.annotations.NotNull
import retrofit2.Response
import retrofit2.http.*


var gson = GsonBuilder()
    .setLenient()
    .create()

interface ImagiApiService {

    @Headers("Content-Type: application/json")
    @POST("login")
    fun login(@Body form: @NotNull UserLogin):
            Single<Response<UserResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("users/self")
    fun getUser(@Header("Authorization", ) token: String) :
            Single<Response<UserResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("toko")
    fun getStore(@Header("Authorization") token: String):
        Single<Response<StoreResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("toko/{id}")
    fun getStoreDetail(@Header("Authorization") token: String, @Path("id") id: String):
        Single<Response<StoreDetailResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("pedagang")
    fun getMerchant(@Header("Authorization") token : String):
            Single<Response<MerchantResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("review/{id}")
    fun getAllReviewStore(@Header("Authorization") token: String, @Path("id") id: String):
            Single<Response<ReviewResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @POST("review/{id}")
    fun postReview(@Header("Authorization") token: String,
                   @Path("id") id: String,
                   @Body req: ReviewForm
    ):
            Single<Response<ReviewPostResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @POST("review/{id}/reply")
    fun postReplay(@Header("Authorization") token: String,
                   @Path("id") id: String,
                   @Body req: ReplayForm
    ):
            Single<Response<ReplayPostResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("barang/toko/{id}")
    fun getProductStore(@Header("Authorization") token: String,
        @Path ("id") id: String) :
            Single<Response<ProductListenResponse>>
    @Multipart
    @Headers(
        "Accept: application/json"
    )
    @POST("users/self")
    fun postProfile(
        @Header("Authorization") token: String,
        @PartMap form: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file :MultipartBody.Part?
    ):
            Single<Response<UserResponse>>

    @Multipart
    @Headers(
        "Accept: application/json"
    )
    @POST("users/self")
    fun postProfileWithoutImage(
        @Header("Authorization") token: String,
        @PartMap form: Map<String, @JvmSuppressWildcards RequestBody>,
    ):
            Single<Response<UserResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("toko/pedagang/{id}")
    fun getStoreByMerchant(@Header("Authorization") token: String, @Path("id") id: String):
            Single<Response<UserResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("barang/search")
    fun getSearchGlobalProduct(@Header("Authorization") token: String,
                        @Query("nama") searchValue: String?,
                        @Query("min") minPrice: String?,
                        @Query("max") maxPrice: String?) :
            Single<Response<ProductListenResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @POST("barang")
    fun postProduct(
        @Header("Authorization") token: String,
        @Body form: @NotNull ProductForm):
            Single<Response<ProductResponse>>


    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @PUT("barang/{id}")
    fun putProduct(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body form: @NotNull ProductForm):
            Single<Response<ProductResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("barang/{id}")
    fun getProduct(@Header("Authorization") token: String, @Path("id") id: String):
            Single<Response<ProductResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @DELETE("barang/{id}/forceDelete")
    fun deleteProduct(@Header("Authorization") token: String, @Path("id") id: String):
            Single<Response<ProductResponse>>

    @Multipart
    @Headers(
        "Accept: application/json",
    )
    @POST("toko")
    fun postStore(
        @Header("Authorization") token: String,
        @PartMap form: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file :MultipartBody.Part?
        ):
            Single<Response<StoreDetailResponse>>

    @Multipart
    @Headers(
        "Accept: application/json",
    )
    @POST("toko")
    fun postStoreWithoutImage(
        @Header("Authorization") token: String,
        @PartMap form: Map<String, @JvmSuppressWildcards RequestBody>,
        ):
            Single<Response<StoreDetailResponse>>

    @Multipart
    @Headers(
        "Accept: application/json"
    )
    @POST("toko/{id}")
    fun putStore(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @PartMap form: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file :MultipartBody.Part):
            Single<Response<StoreDetailResponse>>

    @Multipart
    @Headers(
        "Accept: application/json"
    )
    @POST("toko/{id}")
    fun putStoreWithoutFfle(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @PartMap form: Map<String, @JvmSuppressWildcards RequestBody>):
            Single<Response<StoreDetailResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @DELETE("toko/{id}")
    fun deleteStore(
        @Header("Authorization") token: String,
        @Path("id") id: String,):
            Single<Response<StoreDetailResponse>>


}
