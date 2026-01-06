package com.example.petbuddy.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.ImageComparisonRequest
import com.example.petbuddy.data.model.ImageComparisonResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AIImageComparisonRepository {
    private val apiService: ApiService = RetrofitClient.api
    private val gson: Gson = GsonBuilder().setLenient().create()
    
    /**
     * Compare uploaded image with lost pet images in database
     */
    suspend fun comparePetImage(
        context: Context,
        imageUri: Uri,
        userId: Int? = null
    ): Result<ImageComparisonResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Convert image to base64
                val imageBase64 = imageUriToBase64(context, imageUri)
                    ?: return@withContext Result.failure(Exception("Failed to process image"))
                
                // Call API
                val response = apiService.comparePetImage(
                    imageBase64 = imageBase64,
                    userId = userId
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    
                    // Handle case where response might be a string instead of JSON object
                    val responseBodyString = responseBody.string()
                    
                    // Handle empty response
                    if (responseBodyString.isEmpty()) {
                        return@withContext Result.failure(Exception("Empty response from server"))
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
                            return@withContext Result.failure(Exception("No valid JSON found in response. Response: $preview"))
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
                    
                    // Try to parse the JSON
                    val comparisonResponse = try {
                        gson.fromJson(jsonToParse, ImageComparisonResponse::class.java)
                    } catch (e: JsonSyntaxException) {
                        // If parsing fails, check if it's an error message
                        if (jsonToParse.contains("error", ignoreCase = true) ||
                            jsonToParse.contains("main", ignoreCase = true) ||
                            jsonToParse.length < 20) {
                            return@withContext Result.failure(Exception("Server error. Please ensure 'compare_pet_image.php' is properly configured on the server."))
                        }
                        // Return generic error for invalid JSON with more context
                        val preview = if (jsonToParse.length > 50) {
                            jsonToParse.substring(0, 50) + "..."
                        } else {
                            jsonToParse
                        }
                        return@withContext Result.failure(Exception("Invalid response format from server. Response: $preview"))
                    } catch (e: Exception) {
                        return@withContext Result.failure(Exception("Failed to parse server response: ${e.message}"))
                    }
                    
                    if (comparisonResponse.status == "success") {
                        Result.success(comparisonResponse)
                    } else {
                        Result.failure(Exception(comparisonResponse.message ?: "Comparison failed"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    
                    // Handle 404 error specifically
                    if (response.code() == 404) {
                        Result.failure(Exception("Server file not found. Please ensure 'compare_pet_image.php' is uploaded to your server at: C:\\xampp\\htdocs\\pet_buddy\\"))
                    } else if (errorBody.contains("<!DOCTYPE") || errorBody.contains("<html>")) {
                        // HTML error page returned
                        Result.failure(Exception("Server error: File not found (404). Please copy 'compare_pet_image.php' to your server."))
                    } else {
                        Result.failure(Exception("API error: ${response.code()} - ${errorBody.take(100)}"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Convert image URI to base64 string
     */
    private fun imageUriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            // Resize if too large (max 1024px)
            val resizedBitmap = resizeBitmap(bitmap, 1024)
            
            // Compress and convert to base64
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val imageBytes = outputStream.toByteArray()
            android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Resize bitmap to max dimension while maintaining aspect ratio
     */
    private fun resizeBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }
        
        val scale = maxDimension.toFloat() / maxOf(width, height)
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}

