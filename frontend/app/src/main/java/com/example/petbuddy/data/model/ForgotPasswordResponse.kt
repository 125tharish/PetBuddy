package com.example.petbuddy.data.model

data class SendCodeResponse(
    val status: Boolean,
    val message: String
)

data class VerifyCodeResponse(
    val status: Boolean,
    val message: String
)

data class ResetPasswordResponse(
    val status: Boolean,
    val message: String
)

