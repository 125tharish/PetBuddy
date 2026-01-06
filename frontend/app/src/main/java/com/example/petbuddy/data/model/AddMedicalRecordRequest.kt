package com.example.petbuddy.data.model

data class AddMedicalRecordRequest(
    val user_id: Int,
    val pet_id: Int,
    val title: String,
    val file_url: String
)

