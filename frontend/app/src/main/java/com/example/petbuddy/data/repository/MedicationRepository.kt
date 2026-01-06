package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.Medication
import com.example.petbuddy.data.model.MedicationResponse

class MedicationRepository {
    private val apiService: ApiService = RetrofitClient.api

    suspend fun getMedications(userId: Int, petId: Int, petName: String): Result<List<Medication>> {
        return try {
            val response = apiService.getMedications(userId, petId, petName)
            if (response.isSuccessful && response.body() != null) {
                val medicationResponse = response.body()!!
                if (medicationResponse.status && medicationResponse.medications != null) {
                    Result.success(medicationResponse.medications)
                } else {
                    Result.failure(Exception(medicationResponse.message ?: "Failed to fetch medications"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to fetch medications: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun addMedication(
        userId: Int,
        petId: Int,
        petName: String,
        medicationName: String,
        dosageTime: String,
        frequency: String
    ): Result<MedicationResponse> {
        return try {
            val response = apiService.addMedication(userId, petId, petName, medicationName, dosageTime, frequency)
            if (response.isSuccessful && response.body() != null) {
                val medicationResponse = response.body()!!
                if (medicationResponse.status) {
                    Result.success(medicationResponse)
                } else {
                    Result.failure(Exception(medicationResponse.message ?: "Failed to add medication"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to add medication: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun createMedicationNotification(
        userId: Int,
        petId: Int,
        petName: String,
        medicationName: String,
        dosageTime: String? = null,
        frequency: String? = null
    ): Result<Unit> {
        return try {
            val response = apiService.createMedicationNotification(
                userId = userId,
                petId = petId,
                petName = petName,
                medicationName = medicationName,
                dosageTime = dosageTime,
                frequency = frequency
            )
            if (response.isSuccessful && response.body() != null) {
                try {
                    val responseBody = response.body()!!.string()
                    android.util.Log.d("MedicationRepository", "Medication notification created: $responseBody")
                } catch (e: Exception) {
                    android.util.Log.e("MedicationRepository", "Error reading response body: ${e.message}")
                }
                Result.success(Unit)
            } else {
                val errorBody = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error reading response: ${e.message}"
                }
                android.util.Log.e("MedicationRepository", "Failed to create medication notification: ${response.code()} - $errorBody")
                // Don't fail the whole operation if notification creation fails
                Result.success(Unit)
            }
        } catch (e: Exception) {
            android.util.Log.e("MedicationRepository", "Error creating medication notification: ${e.message}", e)
            // Don't fail the whole operation if notification creation fails
            Result.success(Unit)
        }
    }
}

