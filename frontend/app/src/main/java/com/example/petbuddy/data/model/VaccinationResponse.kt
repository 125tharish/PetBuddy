package com.example.petbuddy.data.model

data class VaccinationResponse(
    val status: Boolean,
    val message: String? = null,
    val vaccinations: List<Vaccination>? = null
)

