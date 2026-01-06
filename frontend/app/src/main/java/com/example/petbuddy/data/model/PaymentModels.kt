package com.example.petbuddy.data.model

data class PaymentResponse(
    val status: Boolean,
    val message: String,
    val payment_id: Int? = null,
    val service_name: String? = null,
    val appointment_date: String? = null,
    val appointment_time: String? = null,
    val amount: String? = null,
    val payment_method: String? = null,
    val receipt_url: String? = null
)

data class PaymentRequest(
    val userId: Int,
    val serviceName: String,
    val appointmentDate: String,
    val appointmentTime: String,
    val amount: String,
    val paymentMethod: String,
    val cardLast4: String
)

