package com.example.petbuddy.data.model

data class ClinicPatientResponse(
    val status: Boolean,
    val total_patients: Int,
    val patients: List<ClinicPatient>
)

data class ClinicPatient(
    val pet_id: Int,
    val pet_name: String,
    val pet_type: String?,
    val breed: String?,
    val age: String?,
    val owner_name: String,
    val total_visits: Int,
    val last_visit: String?,
    val next_visit: String?
)

