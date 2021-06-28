package com.imagi.app.model

import com.google.gson.annotations.SerializedName

data class ReviewResponse (
    @field:SerializedName("success")
    val success : Boolean ? = false,

    @field:SerializedName("message")
    val message : String ? = null,

    @field:SerializedName("code")
    val code : Int ? = null,

    @field:SerializedName("data")
    val data : List<Review> = listOf()
)

data class ReviewPostResponse (
    @field:SerializedName("success")
    val success : Boolean ? = false,

    @field:SerializedName("message")
    val message : String ? = null,

    @field:SerializedName("code")
    val code : Int ? = null,

    @field:SerializedName("data")
    val data : Review? = null
)

data class Review(
    @field:SerializedName("id_review")
    val id_review: Int ? = null,

    @field:SerializedName("id_customer")
    val id_customer: Int ? = null,

    @field:SerializedName("nama_customer")
    val nama_customer:String? = null,

    @field:SerializedName("review")
    val review:String? = null,

    @field:SerializedName("balasan")
    val balasan:Replay? = null,

    @field:SerializedName("created_at")
    val created_at:String? = null,

    @field:SerializedName("updated_at")
    val updated_at:String? = null,
)

data class Replay(
    @field:SerializedName("id_balasan_review")
    val id_balasan_review: Int ? = null,

    @field:SerializedName("nama_pedagang")
    val nama_pedagang: String ? = null,

    @field:SerializedName("balasan")
    val balasan: String ? = null,
)

data class ReviewForm(
    @field:SerializedName("toko_id")
    var toko_id: Int ? = null,

    @field:SerializedName("review")
    var review: String ? = null,
)