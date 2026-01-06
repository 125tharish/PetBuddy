package com.example.petbuddy.data.model

data class UserProfileResponse(
    val status: Boolean,
    val message: String? = null,
    val user: UserProfile? = null
)

