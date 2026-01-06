package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.Vaccination
import com.example.petbuddy.data.model.VaccinationResponse

class VaccinationRepository {
    private val apiService: ApiService = RetrofitClient.api

    suspend fun getVaccinations(
        userId: Int,
        petId: Int,
        petName: String
    ): Result<List<Vaccination>> {
        return try {
            val response = apiService.getVaccinations(userId, petId, petName)
            if (response.isSuccessful && response.body() != null) {
                val vaccinationResponse = response.body()!!
                if (vaccinationResponse.status && vaccinationResponse.vaccinations != null) {
                    Result.success(vaccinationResponse.vaccinations)
                } else {
                    Result.failure(Exception(vaccinationResponse.message ?: "Failed to fetch vaccinations"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to fetch vaccinations: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun addVaccination(
        userId: Int,
        petId: Int,
        petName: String,
        vaccineName: String,
        lastDate: String,
        nextDate: String
    ): Result<VaccinationResponse> {
        return try {
            val response = apiService.addVaccination(userId, petId, petName, vaccineName, lastDate, nextDate)
            if (response.isSuccessful && response.body() != null) {
                val vaccinationResponse = response.body()!!
                if (vaccinationResponse.status) {
                    Result.success(vaccinationResponse)
                } else {
                    Result.failure(Exception(vaccinationResponse.message ?: "Failed to add vaccination"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to add vaccination: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun createVaccinationNotification(
        userId: Int,
        petId: Int,
        petName: String,
        vaccineName: String,
        nextDate: String? = null
    ): Result<Unit> {
        return try {
            val response = apiService.createVaccinationNotification(
                userId = userId,
                petId = petId,
                petName = petName,
                vaccineName = vaccineName,
                nextDate = nextDate
            )
            if (response.isSuccessful && response.body() != null) {
                try {
                    val responseBody = response.body()!!.string()
                    android.util.Log.d("VaccinationRepository", "Vaccination notification created: $responseBody")
                } catch (e: Exception) {
                    android.util.Log.e("VaccinationRepository", "Error reading response body: ${e.message}")
                }
                Result.success(Unit)
            } else {
                val errorBody = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error reading response: ${e.message}"
                }
                android.util.Log.e("VaccinationRepository", "Failed to create vaccination notification: ${response.code()} - $errorBody")
                // Don't fail the whole operation if notification creation fails
                Result.success(Unit)
            }
        } catch (e: Exception) {
            android.util.Log.e("VaccinationRepository", "Error creating vaccination notification: ${e.message}", e)
            // Don't fail the whole operation if notification creation fails
            Result.success(Unit)
        }
    }
}

