package com.imagi.app.network

import com.google.gson.GsonBuilder
import com.imagi.app.model.UserLogin
import com.imagi.app.model.UserResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.jetbrains.annotations.NotNull
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*


//private const val BASE_URL = "https://android-kotlin-fun-mars-server.appspot.com/"

private const val BASE_URL = "http://192.168.1.22:8000/api/v1/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

var gson = GsonBuilder()
    .setLenient()
    .create()

private  val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

var retrofitV2 = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

interface ImagiApiService {
    @GET("realestate")
    suspend fun getProperties():
            List<MarsProperty>

    @Headers("Content-Type: application/json")
    @POST("login")
    fun login(@Body form: @NotNull UserLogin):
            Call<UserResponse>


    @GET("users/{id}")
    fun getUser(@Header("Authorization") token: String, @Path("id") id: String) :
            Call<UserResponse>
}



object ImageApi{
    val retrofitService : ImagiApiService by lazy {
        retrofit.create(ImagiApiService::class.java)
    }

    val retrofitServiceGson : ImagiApiService by lazy {
        retrofitV2.create(ImagiApiService::class.java)
    }
}