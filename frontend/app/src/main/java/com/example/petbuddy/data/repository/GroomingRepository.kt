package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.GroomingService
import com.example.petbuddy.data.model.GroomingServiceResponse

class GroomingRepository {
    private val apiService: ApiService = RetrofitClient.api

    suspend fun getGroomingServices(): Result<List<GroomingService>> {
        return try {
            val response = apiService.getGroomingServices()
            if (response.isSuccessful && response.body() != null) {
                val serviceResponse = response.body()!!
                if (serviceResponse.status == "success" && serviceResponse.data != null) {
                    Result.success(serviceResponse.data)
                } else {
                    Result.failure(Exception(serviceResponse.message ?: "Failed to fetch grooming services"))
                }
            } else {
                val errorBody = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error reading response body: ${e.message}"
                }
                Result.failure(Exception("Failed to fetch grooming services: ${response.code()} - $errorBody"))
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
}

