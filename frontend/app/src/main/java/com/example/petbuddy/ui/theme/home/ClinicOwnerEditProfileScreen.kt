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
import com.example.petbuddy.data.repository.ClinicProfileRepository
import kotlinx.coroutines.launch

@Composable
fun ClinicOwnerEditProfileScreen(
    onBack: () -> Unit = {},
    onProfilePictureClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val profileRepository = remember { ClinicProfileRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    val clinicUserId = userSession.getUserId()
    
    var doctorNameText by remember { mutableStateOf("") }
    var clinicNameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var phoneText by remember { mutableStateOf("") }
    var addressText by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var profileLoaded by remember { mutableStateOf(false) }
    
    // Fetch profile data on screen load
    LaunchedEffect(clinicUserId) {
        if (clinicUserId != -1 && !profileLoaded) {
            isLoading = true
            errorMessage = null
            
            profileRepository.getClinicProfile(clinicUserId)
                .onSuccess { profile ->
                    doctorNameText = profile.full_name
                    clinicNameText = profile.clinic_name
                    emailText = profile.email
                    phoneText = profile.phone
                    addressText = profile.address
                    profileLoaded = true
                    isLoading = false
                }
                .onFailure { exception ->
                    errorMessage = exception.message ?: "Failed to load profile"
                    isLoading = false
                }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F3))
    ) {
        // Header
        ClinicEditProfileHeaderSection(
            onBack = onBack
        )
        
        // Scrollable Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Error Message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = error,
                        color = Color(0xFFD32F2F),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Success Message
            successMessage?.let { success ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = success,
                        color = Color(0xFF2E7D32),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Loading Indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color(0xFF4A90E2)
                    )
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
                        .background(Color(0xFF4A90E2), CircleShape)
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
            
            // Input Fields (only show if profile is loaded or not loading)
            if (!isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                // Doctor Name Field
                Column {
                    Text(
                        text = "Doctor Name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2B2B2B),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = doctorNameText,
                        onValueChange = { doctorNameText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4A90E2),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                // Clinic Name Field
                Column {
                    Text(
                        text = "Clinic Name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2B2B2B),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = clinicNameText,
                        onValueChange = { clinicNameText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4A90E2),
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
                        color = Color(0xFF2B2B2B),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = emailText,
                        onValueChange = { emailText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4A90E2),
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
                        color = Color(0xFF2B2B2B),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = phoneText,
                        onValueChange = { phoneText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4A90E2),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                // Address Field
                Column {
                    Text(
                        text = "Address",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2B2B2B),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = addressText,
                        onValueChange = { addressText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4A90E2),
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
            
            // Bottom padding to ensure content is not hidden behind button
            Spacer(modifier = Modifier.height(80.dp))
        }
        
        // Save Changes Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF8F3))
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    if (clinicUserId == -1) {
                        errorMessage = "User session not found. Please login again."
                        return@Button
                    }
                    
                    // Validation
                    if (doctorNameText.trim().isEmpty()) {
                        errorMessage = "Doctor name is required"
                        return@Button
                    }
                    if (clinicNameText.trim().isEmpty()) {
                        errorMessage = "Clinic name is required"
                        return@Button
                    }
                    if (emailText.trim().isEmpty()) {
                        errorMessage = "Email is required"
                        return@Button
                    }
                    
                    isSaving = true
                    errorMessage = null
                    successMessage = null
                    
                    coroutineScope.launch {
                        profileRepository.updateClinicProfile(
                            clinicUserId = clinicUserId,
                            fullName = doctorNameText.trim(),
                            clinicName = clinicNameText.trim(),
                            email = emailText.trim(),
                            phone = phoneText.trim(),
                            address = addressText.trim()
                        )
                            .onSuccess {
                                isSaving = false
                                successMessage = "Profile updated successfully!"
                                // Update UserSession with new name and email
                                userSession.saveUser(
                                    userId = clinicUserId,
                                    name = doctorNameText.trim(),
                                    email = emailText.trim(),
                                    role = "clinic_owner"
                                )
                                // Clear success message after 2 seconds and navigate back
                                kotlinx.coroutines.delay(1500)
                                onBack()
                            }
                            .onFailure { exception ->
                                isSaving = false
                                errorMessage = exception.message ?: "Failed to update profile"
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                ),
                enabled = !isSaving && !isLoading && profileLoaded
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
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

@Composable
fun ClinicEditProfileHeaderSection(
    onBack: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4A90E2))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Edit Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

