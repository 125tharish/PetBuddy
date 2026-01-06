package com.example.petbuddy.data.repository

import com.example.petbuddy.data.api.ApiService
import com.example.petbuddy.data.api.RetrofitClient
import com.example.petbuddy.data.model.*

class CommunityRepository {
    private val apiService: ApiService = RetrofitClient.api

    suspend fun getCommunityPosts(): Result<CommunityPostResponse> {
        return try {
            val response = apiService.getCommunityPosts()
            if (response.isSuccessful && response.body() != null) {
                val postResponse = response.body()!!
                if (postResponse.status) {
                    Result.success(postResponse)
                } else {
                    Result.failure(Exception("Failed to fetch posts"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to fetch posts: ${response.code()} - $errorBody"))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Connection timeout. Please check:\n1. XAMPP Apache is running\n2. Your device and computer are on the same network\n3. The IP address is correct"))
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

    suspend fun addCommunityPost(userId: Int, content: String): Result<AddPostResponse> {
        return try {
            val response = apiService.addCommunityPost(userId, content)
            if (response.isSuccessful && response.body() != null) {
                val addPostResponse = response.body()!!
                if (addPostResponse.status) {
                    Result.success(addPostResponse)
                } else {
                    Result.failure(Exception(addPostResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to post: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Unable to post"
            Result.failure(Exception(errorMsg))
        }
    }

    suspend fun togglePostLike(postId: Int, userId: Int): Result<ToggleLikeResponse> {
        return try {
            val response = apiService.togglePostLike(postId, userId)
            if (response.isSuccessful && response.body() != null) {
                val likeResponse = response.body()!!
                if (likeResponse.status) {
                    Result.success(likeResponse)
                } else {
                    Result.failure(Exception("Failed to toggle like"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to toggle like: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: "Unable to toggle like"}"))
        }
    }

    suspend fun getPostComments(postId: Int): Result<PostCommentsResponse> {
        return try {
            val response = apiService.getPostComments(postId)
            if (response.isSuccessful && response.body() != null) {
                val commentsResponse = response.body()!!
                if (commentsResponse.status) {
                    Result.success(commentsResponse)
                } else {
                    Result.failure(Exception("Failed to fetch comments"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to fetch comments: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: "Unable to fetch comments"}"))
        }
    }

    suspend fun addPostComment(postId: Int, userId: Int, comment: String): Result<AddCommentResponse> {
        return try {
            val response = apiService.addPostComment(postId, userId, comment)
            if (response.isSuccessful && response.body() != null) {
                val addCommentResponse = response.body()!!
                if (addCommentResponse.status) {
                    Result.success(addCommentResponse)
                } else {
                    Result.failure(Exception(addCommentResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to add comment: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: "Unable to add comment"}"))
        }
    }

    suspend fun createCommunityPostNotification(
        userId: Int,
        postId: Int? = null,
        content: String? = null
    ): Result<Unit> {
        return try {
            val response = apiService.createCommunityPostNotification(
                userId = userId,
                postId = postId,
                content = content
            )
            if (response.isSuccessful && response.body() != null) {
                try {
                    val responseBody = response.body()!!.string()
                    android.util.Log.d("CommunityRepository", "Community post notification created: $responseBody")
                } catch (e: Exception) {
                    android.util.Log.e("CommunityRepository", "Error reading response body: ${e.message}")
                }
                Result.success(Unit)
            } else {
                val errorBody = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error reading response: ${e.message}"
                }
                android.util.Log.e("CommunityRepository", "Failed to create community post notification: ${response.code()} - $errorBody")
                // Don't fail the whole operation if notification creation fails
                Result.success(Unit)
            }
        } catch (e: Exception) {
            android.util.Log.e("CommunityRepository", "Error creating community post notification: ${e.message}", e)
            // Don't fail the whole operation if notification creation fails
            Result.success(Unit)
        }
    }
}

