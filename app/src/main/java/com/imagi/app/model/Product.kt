package com.imagi.app.model

import com.google.gson.annotations.SerializedName

data class Product(

    @field:SerializedName("id_toko")
    val id: Int? = null,

    @field:SerializedName("nama_barang")
    val nama_barang: String? = null,

    @field:SerializedName("id_barang")
    val id_barang: Int? = null,

    @field:SerializedName("harga_rp")
    val harga_rp: String? = null,

    @field:SerializedName("satuan")
    val satuan: String? = null,

    @field:SerializedName("created_at")
    val created_at: String? = null,

)

data class ProductResponse(
    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: Product? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class ProductListenResponse(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: List<Product>? = listOf(),

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)


data class ProductForm(

    @field:SerializedName("toko_id")
    val toko_id: String? = null,

    @field:SerializedName("nama_barang")
    val nama_barang: String? = null,

    @field:SerializedName("harga")
    val harga: String? = null,

    @field:SerializedName("satuan")
    val satuan: String? = null

)
