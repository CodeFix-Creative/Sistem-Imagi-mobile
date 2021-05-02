package com.imagi.app.network
import com.squareup.moshi.Json

data class MarsProperty (

    @Json(name="id") val id : String,
    @Json(name="price") val price : Double,
    @Json(name="type") val type : String,
    @Json(name = "img_src") val img_src : String

)