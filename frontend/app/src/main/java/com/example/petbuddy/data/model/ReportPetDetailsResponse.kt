package com.example.petbuddy.data.model

data class ReportPetDetailsResponse(
    val status: Boolean,
    val message: String? = null,
    val lost_id: Int? = null
)

