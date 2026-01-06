package com.example.petbuddy.data.repository

import android.net.Uri
import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.Pet
import com.example.petbuddy.data.model.PetResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PetRepository {
    private val apiService: ApiService = RetrofitClient.api

    suspend fun getMyPets(userId: Int): Result<List<Pet>> {
        return try {
            val response = apiService.getMyPets(userId)
            if (response.isSuccessful && response.body() != null) {
                val petResponse = response.body()!!
                if (petResponse.status && petResponse.pets != null) {
                    Result.success(petResponse.pets)
                } else {
                    Result.failure(Exception(petResponse.message ?: "Failed to fetch pets"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to fetch pets: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun addPet(
        userId: Int,
        petName: String,
        petType: String,
        breed: String? = null,
        color: String? = null,
        age: String? = null,
        microchipId: String? = null,
        photoUri: Uri? = null,
        context: android.content.Context
    ): Result<PetResponse> {
        return try {
            // Create RequestBody for text fields
            val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val petNameBody = petName.toRequestBody("text/plain".toMediaTypeOrNull())
            val petTypeBody = petType.toRequestBody("text/plain".toMediaTypeOrNull())
            val breedBody = breed?.toRequestBody("text/plain".toMediaTypeOrNull())
            val colorBody = color?.toRequestBody("text/plain".toMediaTypeOrNull())
            val ageBody = age?.toRequestBody("text/plain".toMediaTypeOrNull())
            val microchipIdBody = microchipId?.toRequestBody("text/plain".toMediaTypeOrNull())
            
            // Create MultipartBody.Part for image file
            val imagePart: MultipartBody.Part? = photoUri?.let { uri ->
                try {
                    val file: File? = when {
                        uri.scheme == "file" -> {
                            // Direct file path (from camera)
                            File(uri.path ?: "")
                        }
                        uri.scheme == "content" -> {
                            // Content URI (from gallery)
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val tempFile = File(context.cacheDir, "pet_image_${System.currentTimeMillis()}.jpg")
                            inputStream?.use { input ->
                                tempFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            tempFile
                        }
                        else -> null
                    }
                    
                    file?.let { f ->
                        if (f.exists() && f.length() > 0) {
                            // Create request body from file
                            val requestFile = f.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            MultipartBody.Part.createFormData("pet_image", f.name, requestFile)
                        } else {
                            null
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            
            val response = apiService.addPet(
                userId = userIdBody,
                petName = petNameBody,
                petType = petTypeBody,
                breed = breedBody,
                color = colorBody,
                age = ageBody,
                microchipId = microchipIdBody,
                petImage = imagePart
            )
            
            if (response.isSuccessful && response.body() != null) {
                val petResponse = response.body()!!
                if (petResponse.status) {
                    Result.success(petResponse)
                } else {
                    Result.failure(Exception(petResponse.message ?: "Failed to add pet"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to add pet: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Cannot connect to server. Please check your network connection."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Connection refused. Please ensure XAMPP Apache is running."))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun createPetProfileNotification(
        userId: Int,
        petId: Int? = null,
        petName: String,
        petType: String,
        breed: String? = null
    ): Result<Unit> {
        return try {
            val response = apiService.createPetProfileNotification(
                userId = userId,
                petId = petId,
                petName = petName,
                petType = petType,
                breed = breed
            )
            if (response.isSuccessful && response.body() != null) {
                try {
                    val responseBody = response.body()!!.string()
                    android.util.Log.d("PetRepository", "Pet profile notification created: $responseBody")
                } catch (e: Exception) {
                    android.util.Log.e("PetRepository", "Error reading response body: ${e.message}")
                }
                Result.success(Unit)
            } else {
                val errorBody = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error reading response: ${e.message}"
                }
                android.util.Log.e("PetRepository", "Failed to create pet profile notification: ${response.code()} - $errorBody")
                // Don't fail the whole operation if notification creation fails
                Result.success(Unit)
            }
        } catch (e: Exception) {
            android.util.Log.e("PetRepository", "Error creating pet profile notification: ${e.message}", e)
            // Don't fail the whole operation if notification creation fails
            Result.success(Unit)
        }
    }
}

