package com.example.petbuddy.ui.theme.home

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.CommunityRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun ShareYourStoryScreen(
    onBack: () -> Unit = {},
    onAddPhoto: () -> Unit = {},
    onPostToCommunity: () -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val communityRepository = remember { CommunityRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var storyText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }
    
    // Gallery permission launcher (for Android 12 and below)
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val pickMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            galleryLauncher.launch(pickMediaRequest)
        }
    }
    
    // Function to open gallery
    fun openGallery() {
        val pickMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        
        // For Android 13+ (API 33+), no permission needed for picking images
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            galleryLauncher.launch(pickMediaRequest)
        } else {
            // For older versions, check and request permission
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            
            if (hasPermission) {
                galleryLauncher.launch(pickMediaRequest)
            } else {
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
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
                text = "Share Your Story",
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
            Spacer(modifier = Modifier.height(16.dp))
            
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
            
            // Success Message
            successMessage?.let { success ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = success,
                        color = Color(0xFF4CAF50),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Text Input Field
            OutlinedTextField(
                value = storyText,
                onValueChange = { 
                    storyText = it
                    errorMessage = null
                    successMessage = null
                },
                placeholder = { Text("Share your pet story, tips, or ask for help...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10,
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Selected Image Preview
            selectedImageUri?.let { uri ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Selected image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Remove image button
                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove image",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Add Photo Button
            OutlinedButton(
                onClick = {
                    openGallery()
                    onAddPhoto()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Add photo",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedImageUri != null) "Change Photo" else "Add Photo",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Post to Community Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    val trimmedStory = storyText.trim()
                    if (trimmedStory.isEmpty()) {
                        errorMessage = "Please enter your story"
                        return@Button
                    }
                    
                    val userId = userSession.getUserId()
                    if (userId == -1) {
                        errorMessage = "Please login to post"
                        return@Button
                    }
                    
                    isLoading = true
                    errorMessage = null
                    successMessage = null
                    
                    coroutineScope.launch {
                        communityRepository.addCommunityPost(userId, trimmedStory)
                            .onSuccess { response ->
                                isLoading = false
                                successMessage = response.message
                                
                                // Create notification
                                try {
                                    val notificationResult = communityRepository.createCommunityPostNotification(
                                        userId = userId,
                                        postId = null,
                                        content = trimmedStory
                                    )
                                    notificationResult.onSuccess {
                                        android.util.Log.d("ShareYourStoryScreen", "Notification created successfully")
                                    }.onFailure { e ->
                                        android.util.Log.e("ShareYourStoryScreen", "Failed to create notification: ${e.message}")
                                    }
                                    // Small delay to ensure notification is created
                                    delay(300)
                                } catch (e: Exception) {
                                    android.util.Log.e("ShareYourStoryScreen", "Error creating notification: ${e.message}", e)
                                }
                                
                                storyText = ""
                                selectedImageUri = null
                                // Navigate back after a short delay
                                delay(1500)
                                onPostToCommunity()
                            }
                            .onFailure { exception ->
                                isLoading = false
                                errorMessage = exception.message ?: "Failed to post"
                            }
                    }
                },
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
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Post to Community",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

