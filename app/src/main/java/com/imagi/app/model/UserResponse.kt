package com.imagi.app.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val code: Int,
    val token: String,

    @field:SerializedName("data")
    val data: User? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)