package com.example.petbuddy.data.model

data class ProfileResponse(
    val status: Boolean,
    val message: String? = null,
    val profile: Profile? = null
)

