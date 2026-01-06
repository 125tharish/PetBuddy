package com.example.petbuddy.data.model

data class PetResponse(
    val status: Boolean,
    val message: String? = null,
    val pets: List<Pet>? = null,
    val pet: Pet? = null
)

