package com.example.petbuddy.data.model

data class Vaccination(
    val vaccine_name: String,
    val last_date: String,
    val next_date: String,
    val status: String // CURRENT, DUE_SOON, OVERDUE
)

