package com.example.petbuddy.ui.theme.home

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petbuddy.data.model.Nutrition
import com.example.petbuddy.data.model.Pet
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.NutritionRepository
import com.example.petbuddy.data.repository.PetRepository
import kotlinx.coroutines.launch

@Composable
fun NutritionMealPlanScreen(
    petName: String = "",
    onBack: () -> Unit = {},
    onMealClick: (String) -> Unit = {},
    onFoodClick: () -> Unit = {},
    onDailyPlanClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val petRepository = remember { PetRepository() }
    val nutritionRepository = remember { NutritionRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var nutritionData by remember { mutableStateOf<Nutrition?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var petId by remember { mutableStateOf<Int?>(null) }
    
    // Function to fetch nutrition data
    fun fetchNutritionData(isRefresh: Boolean = false) {
        val userId = userSession.getUserId()
        if (userId == -1) {
            errorMessage = "Please login first"
            isLoading = false
            isRefreshing = false
            return
        }
        
        if (petName.isEmpty()) {
            errorMessage = "Pet name is required"
            isLoading = false
            isRefreshing = false
            return
        }
        
        if (isRefresh) {
            isRefreshing = true
        } else {
            isLoading = true
        }
        errorMessage = null
        
        coroutineScope.launch {
            // First, get pet_id from pet name
            val petsResult = petRepository.getMyPets(userId)
            petsResult.onSuccess { pets ->
                val pet = pets.find { it.pet_name == petName }
                if (pet != null) {
                    petId = pet.pet_id
                    // Now fetch nutrition data
                    val nutritionResult = nutritionRepository.getPetNutrition(pet.pet_id)
                    isLoading = false
                    isRefreshing = false
                    nutritionResult.onSuccess { nutrition ->
                        nutritionData = nutrition
                        errorMessage = null
                    }.onFailure { exception ->
                        errorMessage = exception.message
                        nutritionData = null
                    }
                } else {
                    isLoading = false
                    isRefreshing = false
                    errorMessage = "Pet not found"
                    nutritionData = null
                }
            }.onFailure { exception ->
                isLoading = false
                isRefreshing = false
                errorMessage = exception.message
                nutritionData = null
            }
        }
    }
    
    // Fetch nutrition data from backend on initial load
    LaunchedEffect(petName) {
        fetchNutritionData(isRefresh = false)
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
                text = "Nutrition & Meal Plan",
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
            
            // Loading or Error State
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFF8A50))
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage ?: "Error loading nutrition data",
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                    }
                }
                nutritionData == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No nutrition data available",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    // Add Plan Button - Navigate to add nutrition plan screen
                    Button(
                        onClick = { 
                            onDailyPlanClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF8A50)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Plan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Daily Plan Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Title with Info Icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Daily Plan",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = nutritionData?.daily_cups ?: "N/A",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Meal Entries - Parse breakfast and dinner
                            nutritionData?.breakfast?.let { breakfast ->
                                val (amount, time) = parseMealString(breakfast)
                                MealEntry(
                                    mealName = "Breakfast",
                                    amount = amount,
                                    time = time,
                                    onClick = { onMealClick("Breakfast") }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            nutritionData?.dinner?.let { dinner ->
                                val (amount, time) = parseMealString(dinner)
                                MealEntry(
                                    mealName = "Dinner",
                                    amount = amount,
                                    time = time,
                                    onClick = { onMealClick("Dinner") }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Current Food Card
                    nutritionData?.current_food?.let { currentFood ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onFoodClick() },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Current Food",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currentFood,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun MealEntry(
    mealName: String,
    amount: String,
    time: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = mealName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Text(
            text = "$amount • $time",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

// Helper function to parse meal string (format: "1 cup • 8:00 AM" or just "1 cup")
private fun parseMealString(mealString: String): Pair<String, String> {
    return if (mealString.contains("•")) {
        val parts = mealString.split("•").map { it.trim() }
        if (parts.size >= 2) {
            Pair(parts[0], parts[1])
        } else {
            Pair(mealString, "")
        }
    } else {
        Pair(mealString, "")
    }
}

