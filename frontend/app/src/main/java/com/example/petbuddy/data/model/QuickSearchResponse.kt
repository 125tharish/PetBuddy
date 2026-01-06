package com.example.petbuddy.data.model

data class QuickSearchResponse(
    val status: Boolean,
    val pets: List<QuickSearchPet>? = null
)

