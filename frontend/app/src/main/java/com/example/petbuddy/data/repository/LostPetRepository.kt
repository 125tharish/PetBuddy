package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.ReportPetDetailsResponse
import com.example.petbuddy.data.model.UpdateIdentificationResponse

class LostPetRepository {
    private val apiService: ApiService = RetrofitClient.api

    suspend fun reportPetDetails(
        userId: Int,
        petType: String,
        petName: String,
        breed: String,
        age: String? = null,
        weight: String? = null,
        primaryColor: String? = null,
        description: String? = null
    ): Result<ReportPetDetailsResponse> {
        return try {
            val response = apiService.reportPetDetails(
                userId = userId,
                petType = petType,
                petName = petName,
                breed = breed,
                age = age,
                weight = weight,
                primaryColor = primaryColor,
                description = description
            )
            if (response.isSuccessful && response.body() != null) {
                val reportResponse = response.body()!!
                if (reportResponse.status) {
                    Result.success(reportResponse)
                } else {
                    Result.failure(Exception(reportResponse.message ?: "Failed to report pet details"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Report failed: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Connection timeout. Please check:\n1. XAMPP Apache is running\n2. Your device and computer are on the same network\n3. The IP address is correct"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection and IP address."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: "Unable to connect to server"}"))
        }
    }

    suspend fun createLostPetNotifications(
        lostId: Int,
        userId: Int,
        petName: String,
        petType: String,
        breed: String,
        location: String? = null,
        lostDate: String? = null,
        ownerName: String? = null
    ): Result<Unit> {
        return try {
            val response = apiService.createLostPetNotifications(
                lostId = lostId,
                userId = userId,
                petName = petName,
                petType = petType,
                breed = breed,
                location = location,
                lostDate = lostDate,
                ownerName = ownerName
            )
            if (response.isSuccessful) {
                // Try to read the response body to check if notifications were created
                val responseBody = response.body()?.string()
                android.util.Log.d("LostPetRepository", "Notification response: $responseBody")
                Result.success(Unit)
            } else {
                val errorBody = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error reading response: ${e.message}"
                }
                android.util.Log.e("LostPetRepository", "Failed to create notifications: ${response.code()} - $errorBody")
                // Don't fail the whole operation if notification creation fails
                // Just log it and continue
                Result.success(Unit)
            }
        } catch (e: java.net.UnknownHostException) {
            android.util.Log.e("LostPetRepository", "Network error creating notifications: ${e.message}")
            Result.success(Unit) // Don't fail the whole operation
        } catch (e: java.net.ConnectException) {
            android.util.Log.e("LostPetRepository", "Connection error creating notifications: ${e.message}")
            Result.success(Unit) // Don't fail the whole operation
        } catch (e: Exception) {
            android.util.Log.e("LostPetRepository", "Error creating notifications: ${e.message}", e)
            // Don't fail the whole operation if notification creation fails
            // Just log it and continue
            Result.success(Unit)
        }
    }

    suspend fun updateIdentification(
        lostId: Int,
        hasMicrochip: Boolean,
        microchipNumber: String? = null,
        hasCollar: Boolean,
        collarDescription: String? = null,
        hasIdTag: Boolean,
        idTagText: String? = null
    ): Result<UpdateIdentificationResponse> {
        return try {
            val response = apiService.updateIdentification(
                lostId = lostId,
                hasMicrochip = if (hasMicrochip) 1 else 0,
                microchipNumber = microchipNumber,
                hasCollar = if (hasCollar) 1 else 0,
                collarDescription = collarDescription,
                hasIdTag = if (hasIdTag) 1 else 0,
                idTagText = idTagText
            )
            if (response.isSuccessful && response.body() != null) {
                val updateResponse = response.body()!!
                if (updateResponse.status) {
                    Result.success(updateResponse)
                } else {
                    Result.failure(Exception(updateResponse.message ?: "Failed to update identification"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Update failed: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Connection timeout. Please check:\n1. XAMPP Apache is running\n2. Your device and computer are on the same network\n3. The IP address is correct"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection and IP address."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: "Unable to connect to server"}"))
        }
    }
}

