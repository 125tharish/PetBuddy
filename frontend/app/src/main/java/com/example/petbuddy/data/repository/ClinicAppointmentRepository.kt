package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.ClinicAppointmentResponse

class ClinicAppointmentRepository {
    private val apiService: ApiService = RetrofitClient.api

    suspend fun getClinicAppointments(
        clinicUserId: Int,
        type: String = "all"
    ): Result<ClinicAppointmentResponse> {
        return try {
            val response = apiService.getClinicAppointments(clinicUserId, type)
            if (response.isSuccessful && response.body() != null) {
                val appointmentResponse = response.body()!!
                if (appointmentResponse.status) {
                    Result.success(appointmentResponse)
                } else {
                    Result.failure(Exception("Failed to fetch appointments"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to fetch appointments: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Connection timeout. Please check:\n1. XAMPP Apache is running\n2. Your device and computer are on the same network\n3. The IP address is correct (10.163.250.54)"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection and IP address."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: com.google.gson.JsonSyntaxException) {
            Result.failure(Exception("Server response error. Please check if the server is running correctly."))
        } catch (e: java.io.IOException) {
            Result.failure(Exception("Network error. Please check your internet connection."))
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Unable to connect to server"
            val userFriendlyMsg = if (errorMsg.contains("JsonReader") || errorMsg.contains("malformed JSON")) {
                "Server response error. Please check if the server is running correctly."
            } else {
                "Network error: $errorMsg"
            }
            Result.failure(Exception(userFriendlyMsg))
        }
    }
}

