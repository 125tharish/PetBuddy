package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.Nutrition
import com.example.petbuddy.data.model.NutritionResponse
import com.example.petbuddy.data.model.UpdateNutritionResponse

class NutritionRepository {
    private val apiService: ApiService = RetrofitClient.api

    suspend fun getPetNutrition(petId: Int): Result<Nutrition> {
        return try {
            val response = apiService.getPetNutrition(petId)
            if (response.isSuccessful && response.body() != null) {
                val nutritionResponse = response.body()!!
                if (nutritionResponse.status == "success" && nutritionResponse.data != null) {
                    Result.success(nutritionResponse.data)
                } else {
                    Result.failure(Exception(nutritionResponse.message ?: "Failed to fetch nutrition data"))
                }
            } else {
                val errorBody = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error reading response body: ${e.message}"
                }
                Result.failure(Exception("Failed to fetch nutrition: ${response.code()} - $errorBody"))
            }
        } catch (e: com.google.gson.JsonSyntaxException) {
            Result.failure(Exception("Invalid JSON response from server. Please check server configuration."))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: e.javaClass.simpleName}"))
        }
    }

    suspend fun updateNutritionPlan(
        petId: Int,
        dailyCups: String,
        breakfast: String,
        dinner: String,
        currentFood: String
    ): Result<UpdateNutritionResponse> {
        return try {
            val response = apiService.updateNutritionPlan(
                petId = petId,
                dailyCups = dailyCups,
                breakfast = breakfast,
                dinner = dinner,
                currentFood = currentFood
            )
            if (response.isSuccessful && response.body() != null) {
                val updateResponse = response.body()!!
                if (updateResponse.status == "success") {
                    Result.success(updateResponse)
                } else {
                    Result.failure(Exception(updateResponse.message ?: "Failed to update nutrition plan"))
                }
            } else {
                val errorBody = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error reading response body: ${e.message}"
                }
                Result.failure(Exception("Failed to update nutrition plan: ${response.code()} - $errorBody"))
            }
        } catch (e: com.google.gson.JsonSyntaxException) {
            Result.failure(Exception("Invalid JSON response from server. Please check server configuration."))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: e.javaClass.simpleName}"))
        }
    }

    suspend fun createNutritionNotification(
        userId: Int,
        petId: Int,
        petName: String,
        dailyCups: String? = null
    ): Result<Unit> {
        return try {
            val response = apiService.createNutritionNotification(
                userId = userId,
                petId = petId,
                petName = petName,
                dailyCups = dailyCups
            )
            if (response.isSuccessful && response.body() != null) {
                try {
                    val responseBody = response.body()!!.string()
                    android.util.Log.d("NutritionRepository", "Nutrition notification created: $responseBody")
                } catch (e: Exception) {
                    android.util.Log.e("NutritionRepository", "Error reading response body: ${e.message}")
                }
                Result.success(Unit)
            } else {
                val errorBody = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error reading response: ${e.message}"
                }
                android.util.Log.e("NutritionRepository", "Failed to create nutrition notification: ${response.code()} - $errorBody")
                // Don't fail the whole operation if notification creation fails
                Result.success(Unit)
            }
        } catch (e: Exception) {
            android.util.Log.e("NutritionRepository", "Error creating nutrition notification: ${e.message}", e)
            // Don't fail the whole operation if notification creation fails
            Result.success(Unit)
        }
    }
}

