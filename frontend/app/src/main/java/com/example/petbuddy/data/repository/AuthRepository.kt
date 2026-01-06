package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.*

class AuthRepository {
    private val apiService: ApiService = RetrofitClient.api

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(email, password)
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                if (loginResponse.status) {
                    Result.success(loginResponse)
                } else {
                    Result.failure(Exception(loginResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Login failed: ${response.code()} - $errorBody"))
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
            // Filter out technical JSON parsing errors for user-friendly messages
            val userFriendlyMsg = if (errorMsg.contains("JsonReader") || errorMsg.contains("malformed JSON")) {
                "Server response error. Please check if the server is running correctly."
            } else {
                "Network error: $errorMsg"
            }
            Result.failure(Exception(userFriendlyMsg))
        }
    }

    suspend fun signup(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<SignupResponse> {
        return try {
            val response = apiService.signup(name, email, password, confirmPassword)
            if (response.isSuccessful && response.body() != null) {
                val signupResponse = response.body()!!
                if (signupResponse.status == "success") {
                    Result.success(signupResponse)
                } else {
                    Result.failure(Exception(signupResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Signup failed: ${response.code()} - $errorBody"))
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
            // Filter out technical JSON parsing errors for user-friendly messages
            val userFriendlyMsg = if (errorMsg.contains("JsonReader") || errorMsg.contains("malformed JSON")) {
                "Server response error. Please check if the server is running correctly."
            } else {
                "Network error: $errorMsg"
            }
            Result.failure(Exception(userFriendlyMsg))
        }
    }

    suspend fun clinicOwnerSignup(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<ClinicOwnerSignupResponse> {
        return try {
            val response = apiService.clinicOwnerSignup(fullName, email, password, confirmPassword)
            if (response.isSuccessful && response.body() != null) {
                val signupResponse = response.body()!!
                if (signupResponse.status) {
                    Result.success(signupResponse)
                } else {
                    Result.failure(Exception(signupResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Signup failed: ${response.code()} - $errorBody"))
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

    suspend fun clinicOwnerLogin(email: String, password: String): Result<ClinicOwnerLoginResponse> {
        return try {
            val response = apiService.clinicOwnerLogin(email, password)
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                if (loginResponse.status) {
                    Result.success(loginResponse)
                } else {
                    Result.failure(Exception(loginResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Login failed: ${response.code()} - $errorBody"))
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

    suspend fun clinicOwnerSendVerificationCode(email: String): Result<ClinicOwnerSendCodeResponse> {
        return try {
            val response = apiService.clinicOwnerSendVerificationCode(email)
            if (response.isSuccessful && response.body() != null) {
                val codeResponse = response.body()!!
                if (codeResponse.status) {
                    Result.success(codeResponse)
                } else {
                    Result.failure(Exception(codeResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to send code: ${response.code()} - $errorBody"))
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

    suspend fun clinicOwnerVerifyCode(email: String, code: String): Result<ClinicOwnerVerifyCodeResponse> {
        return try {
            val response = apiService.clinicOwnerVerifyCode(email, code)
            if (response.isSuccessful && response.body() != null) {
                val verifyResponse = response.body()!!
                if (verifyResponse.status) {
                    Result.success(verifyResponse)
                } else {
                    Result.failure(Exception(verifyResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Verification failed: ${response.code()} - $errorBody"))
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

    suspend fun clinicOwnerResetPassword(email: String, password: String): Result<ClinicOwnerResetPasswordResponse> {
        return try {
            val response = apiService.clinicOwnerResetPassword(email, password)
            if (response.isSuccessful && response.body() != null) {
                val resetResponse = response.body()!!
                if (resetResponse.status) {
                    Result.success(resetResponse)
                } else {
                    Result.failure(Exception(resetResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Password reset failed: ${response.code()} - $errorBody"))
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

    suspend fun sendVerificationCode(email: String): Result<SendCodeResponse> {
        return try {
            val response = apiService.sendVerificationCode(email)
            if (response.isSuccessful && response.body() != null) {
                val codeResponse = response.body()!!
                if (codeResponse.status) {
                    Result.success(codeResponse)
                } else {
                    Result.failure(Exception(codeResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to send code: ${response.code()} - $errorBody"))
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

    suspend fun verifyCode(email: String, code: String): Result<VerifyCodeResponse> {
        return try {
            val response = apiService.verifyCode(email, code)
            if (response.isSuccessful && response.body() != null) {
                val verifyResponse = response.body()!!
                if (verifyResponse.status) {
                    Result.success(verifyResponse)
                } else {
                    Result.failure(Exception(verifyResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Verification failed: ${response.code()} - $errorBody"))
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

    suspend fun resetPassword(email: String, password: String): Result<ResetPasswordResponse> {
        return try {
            val response = apiService.resetPassword(email, password)
            if (response.isSuccessful && response.body() != null) {
                val resetResponse = response.body()!!
                if (resetResponse.status) {
                    Result.success(resetResponse)
                } else {
                    Result.failure(Exception(resetResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Password reset failed: ${response.code()} - $errorBody"))
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

