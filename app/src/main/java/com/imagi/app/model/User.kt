package com.imagi.app.model

import com.google.gson.annotations.SerializedName

data class User(

    @field:SerializedName("user_id")
    val user_id: Int? = null,

    @field:SerializedName("nama")
    val nama: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("alamat")
    val alamat: String? = null,

    @field:SerializedName("foto")
    var foto: String? = null,

    @field:SerializedName("no_telp")
    var no_telp: String? = null,

)