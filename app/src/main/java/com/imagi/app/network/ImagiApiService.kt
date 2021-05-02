package com.imagi.app.network

import retrofit2.Call
import retrofit2.Retrofit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://android-kotlin-fun-mars-server.appspot.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private  val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ImagiApiService {
    @GET ("realestate")
    suspend fun getProperties():
            List<MarsProperty>
}


object ImageApi{
    val retrofitService : ImagiApiService by lazy {
        retrofit.create(ImagiApiService::class.java)
    }
}