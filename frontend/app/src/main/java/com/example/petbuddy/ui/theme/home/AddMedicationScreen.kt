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
import com.example.petbuddy.data.repository.MedicationRepository
import com.example.petbuddy.data.repository.PetRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(
    petName: String = "",
    onBack: () -> Unit = {},
    onMedicationAdded: () -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val petRepository = remember { PetRepository() }
    val medicationRepository = remember { MedicationRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var medicationName by remember { mutableStateOf("") }
    var dosageTime by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Daily") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var petId by remember { mutableStateOf<Int?>(null) }
    
    // Get pet_id from pet name
    LaunchedEffect(petName) {
        val userId = userSession.getUserId()
        if (userId != -1 && petName.isNotEmpty()) {
            coroutineScope.launch {
                val petsResult = petRepository.getMyPets(userId)
                petsResult.onSuccess { pets ->
                    val pet = pets.find { it.pet_name == petName }
                    if (pet != null) {
                        petId = pet.pet_id
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
                text = "Add Medication",
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
            
            // Medication Name
            OutlinedTextField(
                value = medicationName,
                onValueChange = {
                    medicationName = it
                    errorMessage = null
                },
                label = { Text("Medication Name *") },
                placeholder = { Text("e.g., Heartgard Plus, Apoquel") },
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
            
            // Dosage Time
            OutlinedTextField(
                value = dosageTime,
                onValueChange = {
                    dosageTime = it
                    errorMessage = null
                },
                label = { Text("Dosage Time *") },
                placeholder = { Text("e.g., 8:00 AM, 8:00 AM & 8:00 PM") },
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
            
            // Frequency Dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = frequency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Frequency *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8A50),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFFFF8A50),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("Daily", "Twice Daily", "Weekly", "Monthly", "As Needed").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                frequency = option
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Add Medication Button
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
                    
                    if (medicationName.isBlank()) {
                        errorMessage = "Medication name is required"
                        return@Button
                    }
                    
                    if (dosageTime.isBlank()) {
                        errorMessage = "Dosage time is required"
                        return@Button
                    }
                    
                    errorMessage = null
                    isLoading = true
                    
                    coroutineScope.launch {
                        val result = medicationRepository.addMedication(
                            userId = userId,
                            petId = petId!!,
                            petName = petName,
                            medicationName = medicationName.trim(),
                            dosageTime = dosageTime.trim(),
                            frequency = frequency
                        )
                        isLoading = false
                        
                        result.onSuccess {
                            // Create notification
                            coroutineScope.launch {
                                try {
                                    val notificationResult = medicationRepository.createMedicationNotification(
                                        userId = userId,
                                        petId = petId!!,
                                        petName = petName,
                                        medicationName = medicationName.trim(),
                                        dosageTime = dosageTime.trim(),
                                        frequency = frequency
                                    )
                                    notificationResult.onSuccess {
                                        android.util.Log.d("AddMedicationScreen", "Notification created successfully")
                                    }.onFailure { e ->
                                        android.util.Log.e("AddMedicationScreen", "Failed to create notification: ${e.message}")
                                    }
                                    // Small delay to ensure notification is created
                                    delay(300)
                                    onMedicationAdded()
                                } catch (e: Exception) {
                                    android.util.Log.e("AddMedicationScreen", "Error creating notification: ${e.message}", e)
                                    onMedicationAdded() // Still navigate even if notification fails
                                }
                            }
                        }.onFailure { exception ->
                            errorMessage = exception.message ?: "Failed to add medication"
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
}

