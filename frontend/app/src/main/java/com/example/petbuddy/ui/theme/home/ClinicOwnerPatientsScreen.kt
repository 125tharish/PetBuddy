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
import com.example.petbuddy.data.model.ClinicPatient
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.ClinicPatientRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class Patient(
    val id: String,
    val petName: String,
    val petBreed: String,
    val petAge: String,
    val petEmoji: String,
    val ownerName: String,
    val ownerPhone: String,
    val lastVisit: String,
    val nextAppointment: String?,
    val totalVisits: Int
)

@Composable
fun ClinicOwnerPatientsScreen(
    onBack: () -> Unit = {},
    onPatientClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val patientRepository = remember { ClinicPatientRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var searchQuery by remember { mutableStateOf("") }
    var patients by remember { mutableStateOf<List<ClinicPatient>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var totalPatients by remember { mutableStateOf(0) }
    var upcomingAppointments by remember { mutableStateOf(0) }
    
    // Load patients on initial composition
    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        
        coroutineScope.launch {
            patientRepository.getClinicPatients("")
                .onSuccess { response ->
                    patients = response.patients
                    totalPatients = response.total_patients
                    upcomingAppointments = response.patients.count { it.next_visit != null }
                    isLoading = false
                }
                .onFailure { exception ->
                    errorMessage = exception.message ?: "Failed to load patients"
                    isLoading = false
                }
        }
    }
    
    // Search with debounce
    LaunchedEffect(searchQuery) {
        kotlinx.coroutines.delay(500) // Debounce search
        
        isLoading = true
        errorMessage = null
        
        coroutineScope.launch {
            patientRepository.getClinicPatients(searchQuery)
                .onSuccess { response ->
                    patients = response.patients
                    totalPatients = response.total_patients
                    upcomingAppointments = response.patients.count { it.next_visit != null }
                    isLoading = false
                }
                .onFailure { exception ->
                    errorMessage = exception.message ?: "Failed to search patients"
                    isLoading = false
                }
        }
    }
    
    // Helper function to get pet emoji
    fun getPetEmoji(petType: String?): String {
        return when (petType?.lowercase()) {
            "dog" -> "🐕"
            "cat" -> "🐱"
            else -> "🐾"
        }
    }
    
    // Helper function to format date
    fun formatDate(dateString: String?): String {
        if (dateString == null || dateString.isEmpty()) return "N/A"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F3))
    ) {
        // Header
        PatientsHeaderSection(
            onBack = onBack
        )
        
        // Search Bar
        PatientsSearchBarSection(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it }
        )
        
        // Stats Summary
        PatientsStatsSummarySection(
            totalPatients = totalPatients,
            upcomingAppointments = upcomingAppointments
        )
        
        // Patients List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Loading State
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4A90E2))
                }
            }
            
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
            
            // Empty State
            if (!isLoading && errorMessage == null && patients.isEmpty()) {
                EmptyStateSection()
            }
            
            // Patients List
            if (!isLoading && errorMessage == null && patients.isNotEmpty()) {
                patients.forEach { patient ->
                    PatientCardFromBackend(
                        patient = patient,
                        onClick = { onPatientClick(patient.pet_id.toString()) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun PatientsHeaderSection(
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
                    text = "Patients",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PatientsSearchBarSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        placeholder = { Text("Search patients, owners, or breeds...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4A90E2),
            unfocusedBorderColor = Color(0xFFE0E0E0)
        )
    )
}

@Composable
fun PatientsStatsSummarySection(
    totalPatients: Int,
    upcomingAppointments: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatSummaryCard(
            title = "Total Patients",
            value = totalPatients.toString(),
            color = Color(0xFF4A90E2),
            modifier = Modifier.weight(1f)
        )
        
        StatSummaryCard(
            title = "Upcoming",
            value = upcomingAppointments.toString(),
            color = Color(0xFF50C878),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatSummaryCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun PatientCard(
    patient: Patient,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pet Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFF5F5F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = patient.petEmoji,
                    fontSize = 32.sp
                )
            }
            
            // Patient Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = patient.petName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2B2B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${patient.petBreed} • ${patient.petAge}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = patient.ownerName,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Last visit: ${patient.lastVisit}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // Right Side Info
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (patient.nextAppointment != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF4A90E2).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Next: ${patient.nextAppointment}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4A90E2),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${patient.totalVisits} visits",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun EmptyStateSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔍",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No patients found",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2B2B2B)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try adjusting your search",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun PatientCardFromBackend(
    patient: ClinicPatient,
    onClick: () -> Unit
) {
    // Helper function to get pet emoji
    val petEmoji = when (patient.pet_type?.lowercase()) {
        "dog" -> "🐕"
        "cat" -> "🐱"
        else -> "🐾"
    }
    
    // Helper function to format date
    fun formatDate(dateString: String?): String {
        if (dateString == null || dateString.isEmpty()) return "N/A"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pet Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFF5F5F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = petEmoji,
                    fontSize = 32.sp
                )
            }
            
            // Patient Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = patient.pet_name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2B2B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${patient.breed ?: "Unknown Breed"} • ${patient.age ?: "Unknown Age"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = patient.owner_name,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Last visit: ${formatDate(patient.last_visit)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // Right Side Info
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (patient.next_visit != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF4A90E2).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Next: ${formatDate(patient.next_visit)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4A90E2),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${patient.total_visits} visits",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

