package com.example.petbuddy.data.model

data class Nutrition(
    val daily_cups: String?,
    val breakfast: String?,
    val dinner: String?,
    val current_food: String?
)

data class NutritionResponse(
    val status: String,
    val message: String? = null,
    val pet_type: String? = null,
    val data: Nutrition? = null
)

data class UpdateNutritionResponse(
    val status: String,
    val message: String? = null
)

