package com.example.petbuddy.ui.theme.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.CommunityRepository
import kotlinx.coroutines.launch

@Composable
fun CommunityScreen(
    onBack: () -> Unit = {},
    onShareStory: () -> Unit = {},
    onPostClick: (String) -> Unit = {},
    onLikeClick: (String) -> Unit = {},
    onCommentClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val communityRepository = remember { CommunityRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var posts by remember { mutableStateOf<List<com.example.petbuddy.data.model.CommunityPost>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var userId by remember { mutableStateOf<Int?>(null) }
    
    // Get user ID
    LaunchedEffect(Unit) {
        userId = userSession.getUserId()
    }
    
    // Fetch posts
    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        communityRepository.getCommunityPosts()
            .onSuccess { response ->
                posts = response.communityPosts
                isLoading = false
            }
            .onFailure { exception ->
                errorMessage = exception.message
                isLoading = false
            }
    }
    
    // Refresh posts function
    fun refreshPosts() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            communityRepository.getCommunityPosts()
                .onSuccess { response ->
                    posts = response.communityPosts
                    isLoading = false
                }
                .onFailure { exception ->
                    errorMessage = exception.message
                    isLoading = false
                }
        }
    }
    
    // Toggle like function
    fun toggleLike(postId: Int) {
        userId?.let { uid ->
            coroutineScope.launch {
                communityRepository.togglePostLike(postId, uid)
                    .onSuccess {
                        // Refresh posts to get updated like count
                        refreshPosts()
                    }
                    .onFailure { exception ->
                        errorMessage = exception.message
                    }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Community",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Share Your Story Button
            Button(
                onClick = onShareStory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8A50)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share Your Story",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Error Message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Loading Indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF8A50))
                }
            } else {
                // Community Posts
                if (posts.isEmpty() && errorMessage == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No posts yet. Be the first to share!",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    posts.forEach { post ->
                        CommunityPostCard(
                            post = post,
                            onClick = { onPostClick(post.userName) },
                            onLikeClick = { toggleLike(post.postId) },
                            onCommentClick = { onCommentClick(post.userName) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CommunityPostCard(
    post: com.example.petbuddy.data.model.CommunityPost,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Author Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFF8A50), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.userInitial,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Author Name and Time
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = post.userName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = post.timeAgo,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Post Content
            Text(
                text = post.content,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            // Post Image if available
            post.imageUrl?.let { imageUrl ->
                Spacer(modifier = Modifier.height(12.dp))
                // Note: You can add AsyncImage here to display the image
                // For now, we'll just show a placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Image: $imageUrl",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Interaction Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Like Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLikeClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.likes}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                // Comment Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onCommentClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info, // Comment icon placeholder
                        contentDescription = "Comment",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.comments}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

