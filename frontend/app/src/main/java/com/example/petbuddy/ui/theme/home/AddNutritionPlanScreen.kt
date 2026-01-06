package com.example.petbuddy.ui.theme.home

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.NutritionRepository
import com.example.petbuddy.data.repository.PetRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun AddNutritionPlanScreen(
    petName: String = "",
    onBack: () -> Unit = {},
    onPlanAdded: () -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val petRepository = remember { PetRepository() }
    val nutritionRepository = remember { NutritionRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var dailyCups by remember { mutableStateOf("") }
    var breakfastAmount by remember { mutableStateOf("") }
    var breakfastTime by remember { mutableStateOf("") }
    var dinnerAmount by remember { mutableStateOf("") }
    var dinnerTime by remember { mutableStateOf("") }
    var currentFood by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var petId by remember { mutableStateOf<Int?>(null) }
    
    // Get pet_id from pet name and load existing data
    LaunchedEffect(petName) {
        val userId = userSession.getUserId()
        if (userId != -1 && petName.isNotEmpty()) {
            coroutineScope.launch {
                val petsResult = petRepository.getMyPets(userId)
                petsResult.onSuccess { pets ->
                    val pet = pets.find { it.pet_name == petName }
                    if (pet != null) {
                        petId = pet.pet_id
                        // Load existing nutrition data if available
                        val nutritionResult = nutritionRepository.getPetNutrition(pet.pet_id)
                        nutritionResult.onSuccess { nutrition ->
                            dailyCups = nutrition.daily_cups ?: ""
                            currentFood = nutrition.current_food ?: ""
                            // Parse breakfast and dinner
                            nutrition.breakfast?.let { breakfast ->
                                if (breakfast.contains("•")) {
                                    val parts = breakfast.split("•").map { it.trim() }
                                    if (parts.size >= 2) {
                                        breakfastAmount = parts[0]
                                        breakfastTime = parts[1]
                                    } else {
                                        breakfastAmount = breakfast
                                    }
                                } else {
                                    breakfastAmount = breakfast
                                }
                            }
                            nutrition.dinner?.let { dinner ->
                                if (dinner.contains("•")) {
                                    val parts = dinner.split("•").map { it.trim() }
                                    if (parts.size >= 2) {
                                        dinnerAmount = parts[0]
                                        dinnerTime = parts[1]
                                    } else {
                                        dinnerAmount = dinner
                                    }
                                } else {
                                    dinnerAmount = dinner
                                }
                            }
                        }
                    } else {
                        errorMessage = "Pet not found"
                    }
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
                text = "Add Nutrition Plan",
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
            
            // Pet Name Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFFF8A50),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Pet: $petName",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Error Message
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
            
            // Daily Cups
            OutlinedTextField(
                value = dailyCups,
                onValueChange = {
                    dailyCups = it
                    errorMessage = null
                },
                label = { Text("Daily Cups *") },
                placeholder = { Text("e.g., 2 cups per day") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8A50),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFFFF8A50),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Breakfast Section
            Text(
                text = "Breakfast",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = breakfastAmount,
                    onValueChange = {
                        breakfastAmount = it
                        errorMessage = null
                    },
                    label = { Text("Amount") },
                    placeholder = { Text("e.g., 1 cup") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8A50),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFFFF8A50),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = breakfastTime,
                    onValueChange = {
                        breakfastTime = it
                        errorMessage = null
                    },
                    label = { Text("Time") },
                    placeholder = { Text("e.g., 8:00 AM") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8A50),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFFFF8A50),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Dinner Section
            Text(
                text = "Dinner",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = dinnerAmount,
                    onValueChange = {
                        dinnerAmount = it
                        errorMessage = null
                    },
                    label = { Text("Amount") },
                    placeholder = { Text("e.g., 1 cup") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8A50),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFFFF8A50),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = dinnerTime,
                    onValueChange = {
                        dinnerTime = it
                        errorMessage = null
                    },
                    label = { Text("Time") },
                    placeholder = { Text("e.g., 6:00 PM") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8A50),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFFFF8A50),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Current Food
            OutlinedTextField(
                value = currentFood,
                onValueChange = {
                    currentFood = it
                    errorMessage = null
                },
                label = { Text("Current Food") },
                placeholder = { Text("e.g., Blue Buffalo Life Protection Formula") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8A50),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFFFF8A50),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Save Plan Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    val userId = userSession.getUserId()
                    if (userId == -1) {
                        errorMessage = "Please login first"
                        return@Button
                    }
                    
                    if (petId == null) {
                        errorMessage = "Pet not found"
                        return@Button
                    }
                    
                    if (dailyCups.isBlank()) {
                        errorMessage = "Daily cups is required"
                        return@Button
                    }
                    
                    errorMessage = null
                    isLoading = true
                    
                    // Format breakfast and dinner strings
                    val breakfast = if (breakfastAmount.isNotBlank()) {
                        if (breakfastTime.isNotBlank()) {
                            "$breakfastAmount • $breakfastTime"
                        } else {
                            breakfastAmount
                        }
                    } else {
                        ""
                    }
                    
                    val dinner = if (dinnerAmount.isNotBlank()) {
                        if (dinnerTime.isNotBlank()) {
                            "$dinnerAmount • $dinnerTime"
                        } else {
                            dinnerAmount
                        }
                    } else {
                        ""
                    }
                    
                    coroutineScope.launch {
                        val result = nutritionRepository.updateNutritionPlan(
                            petId = petId!!,
                            dailyCups = dailyCups.trim(),
                            breakfast = breakfast,
                            dinner = dinner,
                            currentFood = currentFood.trim()
                        )
                        isLoading = false
                        
                        result.onSuccess {
                            // Create notification
                            coroutineScope.launch {
                                try {
                                    val notificationResult = nutritionRepository.createNutritionNotification(
                                        userId = userId,
                                        petId = petId!!,
                                        petName = petName,
                                        dailyCups = dailyCups.trim()
                                    )
                                    notificationResult.onSuccess {
                                        android.util.Log.d("AddNutritionPlanScreen", "Notification created successfully")
                                    }.onFailure { e ->
                                        android.util.Log.e("AddNutritionPlanScreen", "Failed to create notification: ${e.message}")
                                    }
                                    // Small delay to ensure notification is created
                                    delay(300)
                                    onPlanAdded()
                                } catch (e: Exception) {
                                    android.util.Log.e("AddNutritionPlanScreen", "Error creating notification: ${e.message}", e)
                                    onPlanAdded() // Still navigate even if notification fails
                                }
                            }
                        }.onFailure { exception ->
                            errorMessage = exception.message ?: "Failed to save nutrition plan"
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
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Plan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

