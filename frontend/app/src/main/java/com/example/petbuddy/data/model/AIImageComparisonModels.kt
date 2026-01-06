package com.example.petbuddy.data.model

data class ImageComparisonRequest(
    val image1_base64: String,
    val image2_base64: String? = null,
    val user_id: Int? = null
)

data class ImageComparisonResponse(
    val status: String,
    val message: String? = null,
    val similarity_score: Float? = null,
    val confidence: Float? = null,
    val matches: List<PetMatch>? = null
)

data class PetMatch(
    val lost_pet_id: Int,
    val pet_name: String,
    val pet_type: String,
    val breed: String?,
    val similarity: Float,
    val image_url: String? = null,
    val owner_name: String? = null,
    val location: String? = null
)

