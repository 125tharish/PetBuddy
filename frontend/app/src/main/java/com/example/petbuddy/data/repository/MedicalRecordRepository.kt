package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.MedicalRecord
import com.example.petbuddy.data.model.MedicalRecordResponse

class MedicalRecordRepository {
    private val apiService: ApiService = RetrofitClient.api

    suspend fun getMedicalRecords(userId: Int, petId: Int): Result<List<MedicalRecord>> {
        return try {
            val response = apiService.getMedicalRecords(userId, petId)
            if (response.isSuccessful && response.body() != null) {
                val medicalRecordResponse = response.body()!!
                if (medicalRecordResponse.status && medicalRecordResponse.records != null) {
                    Result.success(medicalRecordResponse.records)
                } else {
                    Result.failure(Exception(medicalRecordResponse.message ?: "Failed to fetch medical records"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to fetch medical records: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun uploadMedicalRecord(
        userId: Int,
        petId: Int,
        title: String,
        fileUrl: String
    ): Result<MedicalRecordResponse> {
        return try {
            val response = apiService.uploadMedicalRecord(userId, petId, title, fileUrl)
            if (response.isSuccessful && response.body() != null) {
                val medicalRecordResponse = response.body()!!
                if (medicalRecordResponse.status) {
                    Result.success(medicalRecordResponse)
                } else {
                    Result.failure(Exception(medicalRecordResponse.message ?: "Failed to upload medical record"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to upload medical record: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}

