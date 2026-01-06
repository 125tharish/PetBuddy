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
import com.example.petbuddy.data.repository.ProfileRepository
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onMyPets: () -> Unit = {},
    onSettings: () -> Unit = {},
    onFAQs: () -> Unit = {},
    onAbout: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val profileRepository = remember { ProfileRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var petsCount by remember { mutableStateOf(0) }
    var postsCount by remember { mutableStateOf(0) }
    var helpedCount by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Fetch profile and stats on screen load
    LaunchedEffect(Unit) {
        val userId = userSession.getUserId()
        if (userId != -1) {
            isLoading = true
            errorMessage = null
            
            var profileLoaded = false
            var statsLoaded = false
            
            // Fetch user profile
            profileRepository.getUserProfile(userId).onSuccess { profile ->
                userName = profile.name
                userEmail = profile.email
                profileLoaded = true
                if (statsLoaded) {
                    isLoading = false
                }
            }.onFailure { exception ->
                errorMessage = exception.message
                profileLoaded = true
                if (statsLoaded) {
                    isLoading = false
                }
            }
            
            // Fetch user stats
            profileRepository.getUserStats(userId).onSuccess { stats ->
                petsCount = stats.pets
                postsCount = stats.posts
                helpedCount = stats.helped
                statsLoaded = true
                if (profileLoaded) {
                    isLoading = false
                }
            }.onFailure { exception ->
                // Don't show error for stats failure if profile loaded successfully
                if (!profileLoaded) {
                    errorMessage = exception.message
                }
                statsLoaded = true
                if (profileLoaded) {
                    isLoading = false
                }
            }
        } else {
            errorMessage = "User not logged in"
            isLoading = false
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
                text = "Profile",
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
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF8A50))
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Error loading profile",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            } else {
                // User Information Banner (Orange)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFF8A50))
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Icon
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // User Name and Email
                        Column {
                            Text(
                                text = userName.ifEmpty { "User" },
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = userEmail.ifEmpty { "email@example.com" },
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
                
                // Statistics Card (Partially overlaid on banner)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-24).dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatisticItem(number = "$petsCount", label = "Pets")
                        ProfileStatisticItem(number = "$postsCount", label = "Posts")
                        ProfileStatisticItem(number = "$helpedCount", label = "Helped")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Action List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ProfileActionItem(
                    icon = Icons.Default.Info, // Pencil icon placeholder
                    iconColor = Color.Gray,
                    text = "Edit Profile",
                    onClick = onEditProfile
                )
                ProfileActionItem(
                    icon = Icons.Default.FavoriteBorder,
                    iconColor = Color.Gray,
                    text = "My Pets",
                    onClick = onMyPets
                )
                ProfileActionItem(
                    icon = Icons.Default.Info, // Gear icon placeholder
                    iconColor = Color.Gray,
                    text = "Settings",
                    onClick = onSettings
                )
                ProfileActionItem(
                    icon = Icons.Default.Help,
                    iconColor = Color(0xFF4A90E2),
                    text = "FAQ's",
                    onClick = onFAQs
                )
                ProfileActionItem(
                    icon = Icons.Default.Info,
                    iconColor = Color(0xFFFF8A50),
                    text = "About",
                    onClick = onAbout
                )
                ProfileActionItem(
                    icon = Icons.Default.Info, // Logout icon placeholder
                    iconColor = Color.Red,
                    text = "Logout",
                    onClick = onLogout
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileStatisticItem(
    number: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ProfileActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (text == "Logout") Color.Red else Color.Black
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

