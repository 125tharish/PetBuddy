package com.example.petbuddy.data.model

import com.google.gson.annotations.SerializedName

data class CommunityPostResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("community_posts") val communityPosts: List<CommunityPost>
)

data class CommunityPost(
    @SerializedName("post_id") val postId: Int,
    @SerializedName("user_name") val userName: String,
    @SerializedName("user_initial") val userInitial: String,
    @SerializedName("time_ago") val timeAgo: String,
    @SerializedName("content") val content: String,
    @SerializedName("likes") val likes: Int,
    @SerializedName("comments") val comments: Int,
    @SerializedName("image_url") val imageUrl: String? = null
)

data class AddPostResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("image_url") val imageUrl: String? = null
)

data class ToggleLikeResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("action") val action: String // "liked" or "unliked"
)

data class PostCommentsResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("comments") val comments: List<PostComment>
)

data class PostComment(
    @SerializedName("comment_id") val commentId: Int,
    @SerializedName("user_name") val userName: String,
    @SerializedName("comment") val comment: String,
    @SerializedName("time") val time: String
)

data class AddCommentResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String
)

