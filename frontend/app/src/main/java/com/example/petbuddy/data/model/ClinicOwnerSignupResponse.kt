package com.example.petbuddy.data.model

data class ClinicOwnerSignupResponse(
    val status: Boolean,
    val message: String,
    val clinic_user_id: Int? = null
)

data class ClinicOwnerLoginResponse(
    val status: Boolean,
    val message: String,
    val clinic_user_id: Int? = null,
    val full_name: String? = null,
    val email: String? = null
)

data class ClinicProfile(
    val clinic_user_id: Int,
    val full_name: String,
    val clinic_name: String,
    val email: String,
    val phone: String,
    val address: String
)

data class ClinicProfileResponse(
    val status: String,
    val message: String? = null,
    val profile: ClinicProfile? = null
)

data class UpdateClinicProfileResponse(
    val status: String,
    val message: String
)

