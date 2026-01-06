package com.example.petbuddy.data.model

data class MedicationResponse(
    val status: Boolean,
    val message: String? = null,
    val medications: List<Medication>? = null
)

