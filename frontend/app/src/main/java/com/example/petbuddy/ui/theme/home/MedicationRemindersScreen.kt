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
import com.example.petbuddy.data.model.Medication
import com.example.petbuddy.data.model.Pet
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.MedicationRepository
import com.example.petbuddy.data.repository.PetRepository
import kotlinx.coroutines.launch

@Composable
fun MedicationRemindersScreen(
    petName: String = "",
    onBack: () -> Unit = {},
    onMedicationClick: (String) -> Unit = {},
    onAddMedication: () -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val petRepository = remember { PetRepository() }
    val medicationRepository = remember { MedicationRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var medications by remember { mutableStateOf<List<Medication>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var petId by remember { mutableStateOf<Int?>(null) }
    
    // Fetch medications from backend
    LaunchedEffect(petName) {
        val userId = userSession.getUserId()
        if (userId == -1) {
            errorMessage = "Please login first"
            isLoading = false
            return@LaunchedEffect
        }
        
        if (petName.isEmpty()) {
            errorMessage = "Pet name is required"
            isLoading = false
            return@LaunchedEffect
        }
        
        isLoading = true
        errorMessage = null
        
        coroutineScope.launch {
            // First, get pet_id from pet name
            val petsResult = petRepository.getMyPets(userId)
            petsResult.onSuccess { pets ->
                val pet = pets.find { it.pet_name == petName }
                if (pet != null) {
                    petId = pet.pet_id
                    // Now fetch medications
                    val medicationsResult = medicationRepository.getMedications(
                        userId = userId,
                        petId = pet.pet_id,
                        petName = petName
                    )
                    isLoading = false
                    medicationsResult.onSuccess { medicationList ->
                        medications = medicationList
                    }.onFailure { exception ->
                        errorMessage = exception.message
                    }
                } else {
                    isLoading = false
                    errorMessage = "Pet not found"
                }
            }.onFailure { exception ->
                isLoading = false
                errorMessage = exception.message
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
                text = "Medication Reminders",
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
                    Text(
                        text = errorMessage ?: "Error loading medications",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                medications.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No medications recorded yet. Add your first medication!",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    // Medication Cards
                    medications.forEach { medication ->
                        MedicationCard(
                            medication = medication,
                            onClick = { onMedicationClick(medication.medication_name) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Add Medication Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = onAddMedication,
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
                    text = "Add Medication",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}


@Composable
fun MedicationCard(
    medication: Medication,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), // Light gray background
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pill Icon (Light Purple)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFE1BEE7), RoundedCornerShape(8.dp)), // Light purple
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info, // Pill capsule icon placeholder
                    contentDescription = null,
                    tint = Color(0xFF9C27B0), // Darker purple
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Medication Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = medication.medication_name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = medication.dosage_time,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = medication.frequency,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Bell Icon (Orange)
            IconButton(
                onClick = { /* Handle bell click */ },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info, // Bell icon placeholder
                    contentDescription = "Reminder",
                    tint = Color(0xFFFF8A50), // Orange
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

