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
import com.example.petbuddy.data.model.Pet
import com.example.petbuddy.data.model.Vaccination
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.PetRepository
import com.example.petbuddy.data.repository.VaccinationRepository
import kotlinx.coroutines.launch

@Composable
fun VaccinationScheduleScreen(
    petName: String = "",
    onBack: () -> Unit = {},
    onVaccinationClick: (String) -> Unit = {},
    onAddVaccination: () -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val petRepository = remember { PetRepository() }
    val vaccinationRepository = remember { VaccinationRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var vaccinations by remember { mutableStateOf<List<Vaccination>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var petId by remember { mutableStateOf<Int?>(null) }
    
    fun loadVaccinations() {
        val userId = userSession.getUserId()
        if (userId == -1) {
            errorMessage = "Please login first"
            isLoading = false
            return
        }
        
        if (petName.isEmpty()) {
            errorMessage = "Pet name is required"
            isLoading = false
            return
        }
        
        isLoading = true
        errorMessage = null
        
        coroutineScope.launch {
            val petsResult = petRepository.getMyPets(userId)
            petsResult.onSuccess { pets ->
                val pet = pets.find { it.pet_name == petName }
                if (pet != null) {
                    petId = pet.pet_id
                    // Now fetch vaccinations
                    val vaccinationsResult = vaccinationRepository.getVaccinations(
                        userId = userId,
                        petId = pet.pet_id,
                        petName = petName
                    )
                    isLoading = false
                    vaccinationsResult.onSuccess { vaccinationList ->
                        vaccinations = vaccinationList
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
    
    LaunchedEffect(petName) {
        loadVaccinations()
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
                text = "Vaccination Schedule",
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
                        text = errorMessage ?: "Error loading vaccinations",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                vaccinations.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No vaccinations recorded yet. Add your first vaccination!",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    // Calculate status summary
                    val currentCount = vaccinations.count { it.status == "CURRENT" }
                    val totalCount = vaccinations.size
                    
                    // Update Status Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFF4CAF50), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column {
                                Text(
                                    text = if (currentCount == totalCount) "Up to Date" else "Some Due Soon",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$currentCount of $totalCount vaccinations current",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Vaccination Cards
                    vaccinations.forEach { vaccination ->
                        VaccinationCard(
                            vaccination = vaccination,
                            onClick = { onVaccinationClick(vaccination.vaccine_name) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Add Vaccination Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = onAddVaccination,
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
                    text = "Add Vaccination",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun VaccinationCard(
    vaccination: Vaccination,
    onClick: () -> Unit
) {
    val statusColor = when (vaccination.status) {
        "CURRENT" -> Color(0xFF4CAF50)
        "DUE_SOON" -> Color(0xFFFF8A50)
        "OVERDUE" -> Color(0xFFF44336)
        else -> Color.Gray
    }
    
    val icon = when (vaccination.status) {
        "CURRENT" -> Icons.Default.CheckCircle
        "DUE_SOON" -> Icons.Default.Info
        "OVERDUE" -> Icons.Default.Warning
        else -> Icons.Default.Info
    }
    
    val statusText = when (vaccination.status) {
        "CURRENT" -> "CURRENT"
        "DUE_SOON" -> "DUE SOON"
        "OVERDUE" -> "OVERDUE"
        else -> vaccination.status
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(statusColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Vaccination Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = vaccination.vaccine_name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Last: ${vaccination.last_date}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Next: ${vaccination.next_date}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

