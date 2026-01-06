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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petbuddy.data.model.ClinicAppointment
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.ClinicAppointmentRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class Appointment(
    val id: String,
    val time: String,
    val date: String,
    val petName: String,
    val petBreed: String,
    val ownerName: String,
    val serviceType: String,
    val status: String,
    val statusColor: Color
)

@Composable
fun ClinicOwnerAppointmentsScreen(
    onBack: () -> Unit = {},
    onAppointmentClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val appointmentRepository = remember { ClinicAppointmentRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var selectedFilter by remember { mutableStateOf("Today") }
    var appointments by remember { mutableStateOf<List<ClinicAppointment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Load appointments when filter changes
    LaunchedEffect(selectedFilter) {
        val clinicUserId = userSession.getUserId()
        if (clinicUserId == -1) {
            errorMessage = "Please login first"
            isLoading = false
            return@LaunchedEffect
        }
        
        isLoading = true
        errorMessage = null
        
        val filterType = when (selectedFilter) {
            "Today" -> "today"
            "Upcoming" -> "upcoming"
            "Past" -> "past"
            else -> "all"
        }
        
        coroutineScope.launch {
            appointmentRepository.getClinicAppointments(clinicUserId, filterType)
                .onSuccess { response ->
                    appointments = response.appointments
                    isLoading = false
                }
                .onFailure { exception ->
                    errorMessage = exception.message ?: "Failed to load appointments"
                    isLoading = false
                }
        }
    }
    
    // Group appointments by date
    val groupedAppointments = remember(appointments) {
        appointments.groupBy { it.appointment_date }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F3))
    ) {
        // Header
        HeaderSection(
            onBack = onBack
        )
        
        // Filter Tabs
        FilterTabs(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it }
        )
        
        // Appointments List
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
            if (!isLoading && errorMessage == null && appointments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No appointments found",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "There are no appointments for the selected filter",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Appointments grouped by date
            if (!isLoading && errorMessage == null && appointments.isNotEmpty()) {
                groupedAppointments.forEach { (date, dateAppointments) ->
                    // Date Group Header
                    Text(
                        text = date,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2B2B2B),
                        modifier = Modifier.padding(bottom = 12.dp, top = if (date == groupedAppointments.keys.first()) 0.dp else 16.dp)
                    )
                    
                    // Appointments for this date
                    dateAppointments.forEach { appointment ->
                        AppointmentCardFromBackend(
                            appointment = appointment,
                            onClick = { onAppointmentClick(appointment.appointment_id.toString()) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun HeaderSection(
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
                    text = "Appointments",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun FilterTabs(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("Today", "Upcoming", "Past", "All")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter,
                        fontSize = 14.sp,
                        fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4A90E2),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFF5F5F5),
                    labelColor = Color(0xFF2B2B2B)
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AppointmentCardFromBackend(
    appointment: ClinicAppointment,
    onClick: () -> Unit
) {
    val statusColor = Color(android.graphics.Color.parseColor(appointment.status_color))
    
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
            // Time Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {
                Text(
                    text = appointment.appointment_time,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A90E2)
                )
                Text(
                    text = appointment.appointment_date.split(", ")[0],
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFE0E0E0))
            )
            
            // Appointment Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appointment.pet_name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2B2B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${appointment.pet_breed} • ${appointment.service_name}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
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
                        text = appointment.owner_name,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // Status Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusColor.copy(alpha = 0.2f)
            ) {
                Text(
                    text = appointment.status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
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
            // Time Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {
                Text(
                    text = appointment.time,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A90E2)
                )
                Text(
                    text = appointment.date.split(", ")[0],
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFE0E0E0))
            )
            
            // Appointment Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appointment.petName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2B2B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${appointment.petBreed} • ${appointment.serviceType}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
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
                        text = appointment.ownerName,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // Status Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = appointment.statusColor.copy(alpha = 0.2f)
            ) {
                Text(
                    text = appointment.status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = appointment.statusColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

