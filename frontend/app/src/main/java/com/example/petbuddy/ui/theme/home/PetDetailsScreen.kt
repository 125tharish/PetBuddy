package com.example.petbuddy.ui.theme.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.LostPetRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PetDetailsScreen(
    onBack: () -> Unit = {},
    onContinue: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val repository = remember { LostPetRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var selectedPetType by remember { mutableStateOf("Dog") }
    var petName by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var primaryColor by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
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
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Pet Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(40.dp)) // Balance the back button
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
            
            // Tell us about your pet section
            Text(
                text = "Tell us about your pet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Detailed information helps us match sightings faster.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Pet Type Selection
            Text(
                text = "Pet Type",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PetTypeButton(
                    emoji = "🐕",
                    text = "Dog",
                    isSelected = selectedPetType == "Dog",
                    onClick = { selectedPetType = "Dog" },
                    modifier = Modifier.weight(1f)
                )
                PetTypeButton(
                    emoji = "🐱",
                    text = "Cat",
                    isSelected = selectedPetType == "Cat",
                    onClick = { selectedPetType = "Cat" },
                    modifier = Modifier.weight(1f)
                )
                PetTypeButton(
                    emoji = "🐾",
                    text = "Other",
                    isSelected = selectedPetType == "Other",
                    onClick = { selectedPetType = "Other" },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Pet Name (Required)
            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                label = { Text("Pet Name *") },
                placeholder = { Text("e.g., Max") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8A50),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Breed (Required)
            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Breed *") },
                placeholder = { Text("e.g., Golden Retriever") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8A50),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Age and Weight (Side by Side)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    placeholder = { Text("e.g., 3 years") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8A50),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight") },
                    placeholder = { Text("e.g., 60 lbs") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8A50),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Primary Color
            OutlinedTextField(
                value = primaryColor,
                onValueChange = { primaryColor = it },
                label = { Text("Primary Color") },
                placeholder = { Text("e.g., Golden") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8A50),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Unique Features & Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Unique Features & Description") },
                placeholder = { Text("Describe any unique markings, scars, or distinguishing features...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8A50),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Error Message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFD32F2F),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Success Message
            successMessage?.let { success ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = success,
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF2E7D32),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Continue Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    // Validation
                    if (petName.trim().isEmpty()) {
                        errorMessage = "Pet name is required"
                        return@Button
                    }
                    if (breed.trim().isEmpty()) {
                        errorMessage = "Breed is required"
                        return@Button
                    }
                    
                    val userId = userSession.getUserId()
                    if (userId == -1) {
                        errorMessage = "Please login to continue"
                        return@Button
                    }
                    
                    errorMessage = null
                    isLoading = true
                    
                    coroutineScope.launch {
                        val result = repository.reportPetDetails(
                            userId = userId,
                            petType = selectedPetType,
                            petName = petName.trim(),
                            breed = breed.trim(),
                            age = age.trim().takeIf { it.isNotEmpty() },
                            weight = weight.trim().takeIf { it.isNotEmpty() },
                            primaryColor = primaryColor.trim().takeIf { it.isNotEmpty() },
                            description = description.trim().takeIf { it.isNotEmpty() }
                        )
                        
                        isLoading = false
                        result.onSuccess { response ->
                            successMessage = response.message ?: "Pet details saved successfully"
                            // Store lost_id in navigation state for next screen
                            val lostId = response.lost_id
                            if (lostId != null) {
                                // Create notifications for all users
                                coroutineScope.launch {
                                    repository.createLostPetNotifications(
                                        lostId = lostId,
                                        userId = userId,
                                        petName = petName.trim(),
                                        petType = selectedPetType,
                                        breed = breed.trim(),
                                        location = null, // Can be updated later
                                        lostDate = null, // Can be updated later
                                        ownerName = null // Will be fetched from server
                                    )
                                }
                                // Navigate to next screen after a short delay
                                delay(500)
                                onContinue(lostId)
                            } else {
                                errorMessage = "Failed to get lost pet ID"
                            }
                        }.onFailure { exception ->
                            errorMessage = exception.message ?: "Failed to save pet details"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8A50)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 6.dp
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Continue",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PetTypeButton(
    emoji: String,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (isSelected) Color(0xFFFF8A50) else Color.Gray
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) Color(0xFFFF8A50) else Color(0xFFE0E0E0)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color(0xFFFF8A50) else Color.Gray
            )
        }
    }
}

