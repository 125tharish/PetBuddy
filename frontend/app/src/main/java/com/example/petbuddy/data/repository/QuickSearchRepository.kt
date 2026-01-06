package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.QuickSearchResponse

class QuickSearchRepository {
    private val apiService: ApiService = RetrofitClient.api

    suspend fun searchPets(
        searchQuery: String? = null,
        status: String? = null
    ): Result<QuickSearchResponse> {
        return try {
            val response = apiService.quickSearch(searchQuery, status)
            if (response.isSuccessful && response.body() != null) {
                val searchResponse = response.body()!!
                if (searchResponse.status) {
                    Result.success(searchResponse)
                } else {
                    Result.failure(Exception("Search failed"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Search failed: ${response.code()} - $errorBody"))
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

