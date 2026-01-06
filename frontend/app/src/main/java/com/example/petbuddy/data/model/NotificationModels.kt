package com.example.petbuddy.data.model

data class Notification(
    val notification_id: Int,
    val type: String,
    val title: String,
    val description: String,
    val pet_name: String? = null,
    val pet_type: String? = null,
    val breed: String? = null,
    val location: String? = null,
    val lost_date: String? = null,
    val owner_name: String? = null,
    val timestamp: String,
    val created_at: String,
    val is_unread: Boolean
)

data class NotificationResponse(
    val status: String,
    val message: String? = null,
    val notifications: List<Notification>? = null
)

