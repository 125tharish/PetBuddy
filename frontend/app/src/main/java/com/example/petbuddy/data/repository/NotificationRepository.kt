package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.Notification
import com.example.petbuddy.data.model.NotificationResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException

class NotificationRepository {
    private val apiService: ApiService = RetrofitClient.api
    private val gson: Gson = GsonBuilder().setLenient().create()

    suspend fun getLostPetNotifications(userId: Int): Result<List<Notification>> {
        return try {
            val response = apiService.getLostPetNotifications(userId)
            if (response.isSuccessful && response.body() != null) {
                // Get the raw response body as string
                val responseBodyString = response.body()!!.string()
                
                // Handle empty response
                if (responseBodyString.isEmpty()) {
                    return Result.failure(Exception("Empty response from server"))
                }
                
                // Trim whitespace
                var cleanedResponse = responseBodyString.trim()
                
                // Remove BOM if present
                if (cleanedResponse.startsWith("\uFEFF")) {
                    cleanedResponse = cleanedResponse.substring(1).trim()
                }
                
                // Find the first '{' character in case there's a prefix
                val jsonStartIndex = cleanedResponse.indexOf('{')
                if (jsonStartIndex == -1) {
                    // No JSON object found, try to find array
                    val arrayStartIndex = cleanedResponse.indexOf('[')
                    if (arrayStartIndex == -1) {
                        // No valid JSON structure found
                        val preview = if (cleanedResponse.length > 100) {
                            cleanedResponse.substring(0, 100) + "..."
                        } else {
                            cleanedResponse
                        }
                        return Result.failure(Exception("No valid JSON found in response. Response: $preview"))
                    } else {
                        // Found array, extract it
                        val arrayEndIndex = cleanedResponse.lastIndexOf(']')
                        if (arrayEndIndex > arrayStartIndex) {
                            cleanedResponse = cleanedResponse.substring(arrayStartIndex, arrayEndIndex + 1)
                        }
                    }
                } else {
                    // Found object, extract it
                    val jsonEndIndex = cleanedResponse.lastIndexOf('}')
                    if (jsonEndIndex > jsonStartIndex) {
                        cleanedResponse = cleanedResponse.substring(jsonStartIndex, jsonEndIndex + 1)
                    } else {
                        // Malformed JSON
                        cleanedResponse = cleanedResponse.substring(jsonStartIndex)
                    }
                }
                
                // Handle case where response might be double-encoded (JSON string within JSON string)
                val jsonToParse = if (cleanedResponse.startsWith("\"") && cleanedResponse.endsWith("\"")) {
                    // Remove outer quotes and unescape
                    cleanedResponse.substring(1, cleanedResponse.length - 1)
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\")
                        .replace("\\n", "\n")
                        .replace("\\r", "\r")
                        .replace("\\t", "\t")
                } else {
                    cleanedResponse
                }
                
                // Validate JSON structure before parsing
                if (!isValidJson(jsonToParse)) {
                    // If it's not valid JSON, check if it's an error message
                    if (jsonToParse.contains("error", ignoreCase = true)) {
                        // Try to extract error message from response
                        try {
                            val errorResponse = gson.fromJson(jsonToParse, Map::class.java) as Map<*, *>
                            val errorMsg = errorResponse["message"] as? String
                            if (errorMsg != null) {
                                return Result.failure(Exception(errorMsg))
                            }
                        } catch (e: Exception) {
                            // Ignore parsing error, use default message
                        }
                        return Result.failure(Exception("Server error. Please check your connection and try again."))
                    }
                    // Check for PHP errors or warnings
                    if (jsonToParse.contains("Warning:", ignoreCase = true) || 
                        jsonToParse.contains("Fatal error:", ignoreCase = true) ||
                        jsonToParse.contains("Parse error:", ignoreCase = true) ||
                        jsonToParse.contains("Notice:", ignoreCase = true)) {
                        return Result.failure(Exception("Server error detected. Please check server configuration."))
                    }
                    if (jsonToParse.contains("main", ignoreCase = true) && jsonToParse.length < 50) {
                        // Likely a PHP error or debug output
                        return Result.failure(Exception("Server configuration error. Please ensure the PHP file is properly configured."))
                    }
                    // Return generic error for invalid JSON with preview
                    val preview = if (jsonToParse.length > 100) {
                        jsonToParse.substring(0, 100) + "..."
                    } else {
                        jsonToParse
                    }
                    return Result.failure(Exception("Invalid response from server. Please try again later."))
                }
                
                // Try to parse the JSON
                val notificationResponse = try {
                    gson.fromJson(jsonToParse, NotificationResponse::class.java)
                } catch (e: JsonSyntaxException) {
                    // If parsing fails, check if it's a simple error response
                    if (jsonToParse.contains("error") || jsonToParse.contains("main") || jsonToParse.length < 20) {
                        return Result.failure(Exception("Server error. Please try again later."))
                    }
                    
                    // Try parsing as a simple object with just status
                    try {
                        val simpleResponse = gson.fromJson(jsonToParse, Map::class.java) as Map<*, *>
                        if (simpleResponse["status"] == "success") {
                            // Return empty list if status is success but no notifications
                            return Result.success(emptyList())
                        } else {
                            val message = simpleResponse["message"] as? String ?: "Failed to get notifications"
                            return Result.failure(Exception(message))
                        }
                    } catch (e2: Exception) {
                        // If all parsing fails, return user-friendly error
                        return Result.failure(Exception("Unable to load notifications. Please try again later."))
                    }
                }
                
                if (notificationResponse.status == "success") {
                    // Return notifications list or empty list if null
                    Result.success(notificationResponse.notifications ?: emptyList())
                } else {
                    Result.failure(Exception(notificationResponse.message ?: "Failed to get notifications"))
                }
            } else {
                val errorBody = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error reading response"
                }
                val errorMsg = when (response.code()) {
                    404 -> "Notifications endpoint not found. Please check server configuration."
                    500 -> "Server error. Please try again later."
                    else -> "Failed to get notifications: ${response.code()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Connection timeout. Please check your network connection."))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure the server is running."))
        } catch (e: JsonSyntaxException) {
            Result.failure(Exception("JSON parsing error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: "Unable to load notifications"}"))
        }
    }
    
    /**
     * Validates if a string is a valid JSON structure
     */
    private fun isValidJson(jsonString: String): Boolean {
        if (jsonString.isEmpty()) return false
        
        val trimmed = jsonString.trim()
        
        // Must start with { or [
        if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
            return false
        }
        
        // Must end with } or ]
        if (!trimmed.endsWith("}") && !trimmed.endsWith("]")) {
            return false
        }
        
        // Check for basic JSON structure - must have at least one key-value pair for objects
        if (trimmed.startsWith("{")) {
            // Remove outer braces
            val content = trimmed.substring(1, trimmed.length - 1).trim()
            // Empty object {} is valid
            if (content.isEmpty()) return true
            // Must contain at least one colon for key-value pairs
            if (!content.contains(":")) return false
        }
        
        // Try to parse with Gson to validate
        return try {
            gson.fromJson(trimmed, Any::class.java)
            true
        } catch (e: Exception) {
            false
        }
    }
}

