package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.Profile
import com.example.petbuddy.data.model.ProfileResponse
import com.example.petbuddy.data.model.ProfileStats
import com.example.petbuddy.data.model.UserProfile

class ProfileRepository {
    private val apiService: ApiService = RetrofitClient.api

    suspend fun getProfile(userId: Int): Result<Profile> {
        return try {
            val response = apiService.getProfile(userId)
            if (response.isSuccessful && response.body() != null) {
                val profileResponse = response.body()!!
                if (profileResponse.status && profileResponse.profile != null) {
                    Result.success(profileResponse.profile)
                } else {
                    Result.failure(Exception(profileResponse.message ?: "Failed to get profile"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Get profile failed: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Connection timeout. Please check:\n1. XAMPP Apache is running\n2. Your device and computer are on the same network\n3. The IP address is correct (10.163.250.54)"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection and IP address."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: "Unable to connect to server"}"))
        }
    }

    suspend fun updateProfile(userId: Int, name: String, email: String, phone: String?): Result<Boolean> {
        return try {
            val response = apiService.updateProfile(userId, name, email, phone)
            if (response.isSuccessful && response.body() != null) {
                val profileResponse = response.body()!!
                if (profileResponse.status) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(profileResponse.message ?: "Failed to update profile"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Update profile failed: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Connection timeout. Please check:\n1. XAMPP Apache is running\n2. Your device and computer are on the same network\n3. The IP address is correct (10.163.250.54)"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection and IP address."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: "Unable to connect to server"}"))
        }
    }

    suspend fun getUserProfile(userId: Int): Result<UserProfile> {
        return try {
            val response = apiService.getUserProfile(userId)
            if (response.isSuccessful && response.body() != null) {
                val profileResponse = response.body()!!
                if (profileResponse.status && profileResponse.user != null) {
                    Result.success(profileResponse.user)
                } else {
                    Result.failure(Exception(profileResponse.message ?: "Failed to get user profile"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Get user profile failed: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Connection timeout. Please check:\n1. XAMPP Apache is running\n2. Your device and computer are on the same network\n3. The IP address is correct (10.163.250.54)"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection and IP address."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: "Unable to connect to server"}"))
        }
    }

    suspend fun getUserStats(userId: Int): Result<ProfileStats> {
        return try {
            val response = apiService.getUserStats(userId)
            if (response.isSuccessful && response.body() != null) {
                val statsResponse = response.body()!!
                if (statsResponse.status && statsResponse.pets != null && statsResponse.posts != null && statsResponse.helped != null) {
                    Result.success(
                        ProfileStats(
                            pets = statsResponse.pets,
                            posts = statsResponse.posts,
                            helped = statsResponse.helped
                        )
                    )
                } else {
                    Result.failure(Exception(statsResponse.message ?: "Failed to get user stats"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Get user stats failed: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Connection timeout. Please check:\n1. XAMPP Apache is running\n2. Your device and computer are on the same network\n3. The IP address is correct (10.163.250.54)"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection and IP address."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: "Unable to connect to server"}"))
        }
    }
}

