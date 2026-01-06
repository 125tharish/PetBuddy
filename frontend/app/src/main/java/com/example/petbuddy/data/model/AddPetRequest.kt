package com.example.petbuddy.data.model

data class AddPetRequest(
    val user_id: Int,
    val pet_name: String,
    val pet_type: String,
    val breed: String? = null,
    val color: String? = null,
    val age: String? = null,
    val microchip_id: String? = null
)

