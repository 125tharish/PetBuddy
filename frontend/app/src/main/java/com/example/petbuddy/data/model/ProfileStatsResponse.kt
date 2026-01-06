package com.example.petbuddy.data.model

data class ProfileStatsResponse(
    val status: Boolean,
    val message: String? = null,
    val pets: Int? = null,
    val posts: Int? = null,
    val helped: Int? = null
)

