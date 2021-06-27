package com.imagi.app.model

import com.google.gson.annotations.SerializedName

data class MerchantResponse(

    @field:SerializedName("success")
    val success : Boolean ? = false,

    @field:SerializedName("message")
    val message : String ? = null,

    @field:SerializedName("code")
    val code : Int ? = null,

    @field:SerializedName("data")
    val data : List<User> = listOf()
)