package com.example.petbuddy.data.model

data class AddVaccinationRequest(
    val user_id: Int,
    val pet_id: Int,
    val pet_name: String,
    val vaccine_name: String,
    val last_date: String,
    val next_date: String
)

