package com.example.petbuddy.data.model

data class UpdateProfileRequest(
    val user_id: Int,
    val name: String,
    val email: String,
    val phone: String? = null
)

