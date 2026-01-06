package com.example.petbuddy.data.model

data class GroomingService(
    val service_id: Int,
    val service_name: String,
    val location: String? = null,
    val hours: String? = null,
    val contact: String? = null,
    val rating: Float,
    val total_reviews: Int = 0,
    val min_price: String,
    val max_price: String
) {
    val priceRange: String
        get() = "₹$min_price-$max_price"
}

data class GroomingServiceResponse(
    val status: String,
    val message: String? = null,
    val data: List<GroomingService>? = null
)

