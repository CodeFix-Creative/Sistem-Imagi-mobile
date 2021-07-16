package com.imagi.app.model

import com.google.gson.annotations.SerializedName

data class Store (

    @field:SerializedName("toko_id")
    val toko_id: Int? = null,

    @field:SerializedName("nama_toko")
    val nama_toko: String? = null,

    @field:SerializedName("alamat_toko")
    val alamat_toko: String? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null,

    @field:SerializedName("no_telp")
    val no_telp: String? = null,

    @field:SerializedName("status")
    var status: String? = null,

    @field:SerializedName("pedagang")
    val pedagang: User? = null,

    @field:SerializedName("path_foto")
    val path_foto: String? = null,


)

data class StoreResponse(

    @field:SerializedName("success")
    val success : Boolean ? = false,

    @field:SerializedName("message")
    val message : String ? = null,

    @field:SerializedName("code")
    val code : Int ? = null,

    @field:SerializedName("data")
    val data : List<Store> = listOf()
)

data class StoreDetailResponse(

    @field:SerializedName("success")
    val success : Boolean ? = false,

    @field:SerializedName("message")
    val message : String ? = null,

    @field:SerializedName("code")
    val code : Int ? = null,

    @field:SerializedName("data")
    val data : Store ? = null
)

data class StoreForm(
    @field:SerializedName("pedagang_id")
    val pedagang_id : String ? = null,

    @field:SerializedName("nama_toko")
    val nama_toko : String ? = null,

    @field:SerializedName("no_telp")
    val no_telp : String ? = null,

    @field:SerializedName("alamat_toko")
    val alamat_toko : String ? = null,

    @field:SerializedName("latitude")
    val latitude : String ? = null,

    @field:SerializedName("longitude")
    val longitude : String ? = null,

    @field:SerializedName("foto")
    val foto : String ? = null,

    @field:SerializedName("facebook")
    val facebook : String  = "-",

    @field:SerializedName("twitter")
    val twitter : String  = "-",

    @field:SerializedName("instagram")
    val instagram : String  = "-",

    @field:SerializedName("website")
    val website : String = "-",

    )