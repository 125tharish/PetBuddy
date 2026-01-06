package com.example.petbuddy.data.model

data class Medication(
    val medication_name: String,
    val dosage_time: String,
    val frequency: String,
    val reminder_enabled: Boolean = true
)

