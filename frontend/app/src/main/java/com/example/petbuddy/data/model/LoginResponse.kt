package com.example.petbuddy.data.model

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val user_id: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val role: String? = null
)

