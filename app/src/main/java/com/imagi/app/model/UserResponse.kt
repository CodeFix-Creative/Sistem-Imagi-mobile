package com.imagi.app.model

data class UserResponse(
    val code: Int,
    val `data`: User,
    val message: String,
    val success: Boolean,
    val token: String
)