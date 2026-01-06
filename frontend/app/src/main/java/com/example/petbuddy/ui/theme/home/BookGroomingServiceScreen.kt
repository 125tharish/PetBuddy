package com.example.petbuddy.ui.theme.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petbuddy.data.model.GroomingService
import com.example.petbuddy.data.repository.GroomingRepository
import kotlinx.coroutines.launch

@Composable
fun BookGroomingServiceScreen(
    onBack: () -> Unit = {},
    onServiceClick: (String) -> Unit = {}
) {
    val groomingRepository = remember { GroomingRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var services by remember { mutableStateOf<List<GroomingService>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Fetch grooming services from backend
    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        
        coroutineScope.launch {
            val result = groomingRepository.getGroomingServices()
            isLoading = false
            result.onSuccess { serviceList ->
                services = serviceList
            }.onFailure { exception ->
                errorMessage = exception.message
                services = emptyList()
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
                text = "Book Grooming Service",
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
                            text = errorMessage ?: "Error loading grooming services",
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                    }
                }
                services.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No grooming services available",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    // Service Cards
                    services.forEach { service ->
                        GroomingServiceCard(
                            service = service,
                            onClick = { onServiceClick(service.service_name) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GroomingServiceCard(
    service: GroomingService,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)) // Light grey border
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Light Blue Scissors Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp)), // Light blue
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info, // Scissors icon placeholder
                    contentDescription = null,
                    tint = Color(0xFF2196F3), // Blue
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Service Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = service.service_name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107), // Yellow
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${service.rating}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    if (service.total_reviews > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(${service.total_reviews})",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "•",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = service.priceRange,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                // Location
                service.location?.takeIf { it.isNotBlank() }?.let { location ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF757575),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                // Hours
                service.hours?.takeIf { it.isNotBlank() }?.let { hours ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF757575),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = hours,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

