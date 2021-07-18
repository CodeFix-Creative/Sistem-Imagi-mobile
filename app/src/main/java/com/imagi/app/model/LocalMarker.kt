package com.imagi.app.model

import com.google.gson.annotations.SerializedName

data class LocalMarker (

    @field:SerializedName("latitude")
    val latitude : Double ? = null,

    @field:SerializedName("longitude")
    val longitude : Double ? = null,

    @field:SerializedName("name")
    val name : String ? = null,

    @field:SerializedName("id")
    val id : String ? = null,
)