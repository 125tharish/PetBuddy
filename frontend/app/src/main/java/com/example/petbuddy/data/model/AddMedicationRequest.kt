package com.example.petbuddy.data.model

data class AddMedicationRequest(
    val user_id: Int,
    val pet_id: Int,
    val pet_name: String,
    val medication_name: String,
    val dosage_time: String,
    val frequency: String
)

