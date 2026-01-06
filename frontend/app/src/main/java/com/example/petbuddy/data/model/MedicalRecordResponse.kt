package com.example.petbuddy.data.model

data class MedicalRecordResponse(
    val status: Boolean,
    val message: String? = null,
    val records: List<MedicalRecord>? = null,
    val file_url: String? = null
)

