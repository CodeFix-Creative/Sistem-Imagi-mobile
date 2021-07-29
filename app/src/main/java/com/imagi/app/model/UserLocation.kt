package com.imagi.app.model

import com.google.gson.annotations.SerializedName

data class UserLocation (

    @field:SerializedName("latitude")
    val latitude : String ? = null,

    @field:SerializedName("longitude")
    val longitude : String ? = null,
)