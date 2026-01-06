package com.example.petbuddy.data.model

data class ClinicAppointmentResponse(
    val status: Boolean,
    val appointments: List<ClinicAppointment>
)

data class ClinicAppointment(
    val appointment_id: Int,
    val pet_id: Int,
    val pet_name: String,
    val pet_breed: String,
    val owner_name: String,
    val owner_email: String,
    val service_name: String,
    val appointment_date: String,
    val appointment_time: String,
    val status: String,
    val status_color: String
)

