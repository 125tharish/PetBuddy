package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.ClinicProfile
import com.example.petbuddy.data.model.UpdateClinicProfileResponse

class ClinicProfileRepository {
    private val apiService: ApiService = RetrofitClient.api

    suspend fun getClinicProfile(clinicUserId: Int): Result<ClinicProfile> {
        return try {
            val response = apiService.getClinicProfile(clinicUserId)
            if (response.isSuccessful && response.body() != null) {
                val profileResponse = response.body()!!
                if (profileResponse.status == "success" && profileResponse.profile != null) {
                    Result.success(profileResponse.profile)
                } else {
                    Result.failure(Exception(profileResponse.message ?: "Failed to get clinic profile"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Get clinic profile failed: ${response.code()} - $errorBody"))
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

    suspend fun updateClinicProfile(
        clinicUserId: Int,
        fullName: String,
        clinicName: String,
        email: String,
        phone: String,
        address: String
    ): Result<Boolean> {
        return try {
            val response = apiService.updateClinicProfile(
                clinicUserId,
                fullName,
                clinicName,
                email,
                phone,
                address
            )
            if (response.isSuccessful && response.body() != null) {
                val updateResponse = response.body()!!
                if (updateResponse.status == "success") {
                    Result.success(true)
                } else {
                    Result.failure(Exception(updateResponse.message ?: "Failed to update clinic profile"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Update clinic profile failed: ${response.code()} - $errorBody"))
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

