package com.example.petbuddy.data.model

data class ClinicOwnerSendCodeResponse(
    val status: Boolean,
    val message: String
)

data class ClinicOwnerVerifyCodeResponse(
    val status: Boolean,
    val message: String
)

data class ClinicOwnerResetPasswordResponse(
    val status: Boolean,
    val message: String
)

