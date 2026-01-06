package com.example.petbuddy.ui.theme.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.ProfileRepository
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    onBack: () -> Unit = {},
    onProfilePictureClick: () -> Unit = {},
    onSaveChanges: () -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val profileRepository = remember { ProfileRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var nameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var phoneText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    // Fetch profile on screen load
    LaunchedEffect(Unit) {
        val userId = userSession.getUserId()
        if (userId != -1) {
            isLoading = true
            errorMessage = null
            
            profileRepository.getProfile(userId).onSuccess { profile ->
                nameText = profile.name
                emailText = profile.email
                phoneText = profile.phone ?: ""
                isLoading = false
            }.onFailure { exception ->
                errorMessage = exception.message
                isLoading = false
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
                text = "Edit Profile",
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
            Spacer(modifier = Modifier.height(24.dp))
            
            // Error Message
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
            
            // Success Message
            successMessage?.let {
                Text(
                    text = it,
                    color = Color(0xFF22C55E),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
            
            // Loading Indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF8A50))
                }
            }
            
            // Profile Picture Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Large Profile Picture Circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color(0xFFFF8A50), CircleShape)
                        .clickable { onProfilePictureClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Input Fields
            if (!isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                // Name Field
                Column {
                    Text(
                        text = "Name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = nameText,
                        onValueChange = { nameText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color(0xFFFF8A50),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                // Email Field
                Column {
                    Text(
                        text = "Email",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = emailText,
                        onValueChange = { emailText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color(0xFFFF8A50),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                // Phone Field
                Column {
                    Text(
                        text = "Phone",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = phoneText,
                        onValueChange = { phoneText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color(0xFFFF8A50),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Save Changes Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    val trimmedName = nameText.trim()
                    val trimmedEmail = emailText.trim()
                    val trimmedPhone = phoneText.trim()
                    
                    if (trimmedName.isEmpty() || trimmedEmail.isEmpty()) {
                        errorMessage = "Name and email are required"
                        successMessage = null
                        return@Button
                    }
                    
                    if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
                        errorMessage = "Please enter a valid email address"
                        successMessage = null
                        return@Button
                    }
                    
                    errorMessage = null
                    successMessage = null
                    isSaving = true
                    
                    val userId = userSession.getUserId()
                    if (userId != -1) {
                        coroutineScope.launch {
                            profileRepository.updateProfile(
                                userId = userId,
                                name = trimmedName,
                                email = trimmedEmail,
                                phone = if (trimmedPhone.isNotEmpty()) trimmedPhone else null
                            ).onSuccess {
                                isSaving = false
                                successMessage = "Profile updated successfully!"
                                // Update UserSession with new name and email
                                userSession.saveUser(
                                    userId = userId,
                                    name = trimmedName,
                                    email = trimmedEmail,
                                    role = userSession.getUserRole()
                                )
                                // Call onSaveChanges after a short delay
                                kotlinx.coroutines.delay(1000)
                                onSaveChanges()
                            }.onFailure { exception ->
                                isSaving = false
                                errorMessage = exception.message
                            }
                        }
                    } else {
                        isSaving = false
                        errorMessage = "User not logged in"
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
                enabled = !isSaving && !isLoading
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Save Changes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

