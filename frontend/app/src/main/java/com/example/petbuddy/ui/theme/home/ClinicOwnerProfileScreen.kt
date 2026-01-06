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
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.ClinicProfileRepository

@Composable
fun ClinicOwnerProfileScreen(
    totalPatients: Int = 324,
    todayAppointments: Int = 12,
    monthlyRevenue: String = "$12,450",
    onBack: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onServicesManagement: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val profileRepository = remember { ClinicProfileRepository() }
    
    val clinicUserId = userSession.getUserId()
    
    var clinicName by remember { mutableStateOf("") }
    var doctorName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    
    // Fetch profile data on screen load
    LaunchedEffect(clinicUserId) {
        if (clinicUserId != -1) {
            profileRepository.getClinicProfile(clinicUserId)
                .onSuccess { profile ->
                    doctorName = profile.full_name.ifEmpty { "Not set" }
                    clinicName = profile.clinic_name.ifEmpty { "Not set" }
                    email = profile.email.ifEmpty { "Not set" }
                    phone = profile.phone.ifEmpty { "Not set" }
                    address = profile.address.ifEmpty { "Not set" }
                    isLoading = false
                }
                .onFailure {
                    // If profile fetch fails, use session data or defaults
                    doctorName = userSession.getUserName().ifEmpty { "Not set" }
                    email = userSession.getUserEmail().ifEmpty { "Not set" }
                    clinicName = "Not set"
                    phone = "Not set"
                    address = "Not set"
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F3))
    ) {
        // Header
        ClinicProfileHeaderSection(
            onBack = onBack
        )
        
        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
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
            } else {
                // Clinic Information Banner (Blue)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF4A90E2))
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Clinic Icon
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF4A90E2),
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Doctor Name
                    Text(
                        text = doctorName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Clinic Name
                    Text(
                        text = clinicName,
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Contact Info - Centered
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = email,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = phone,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = address,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                } // Close Box
                
                // Statistics Card (Partially overlaid on banner)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-24).dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ClinicProfileStatisticItem(
                            number = "$totalPatients",
                            label = "Patients"
                        )
                        ClinicProfileStatisticItem(
                            number = "$todayAppointments",
                            label = "Today"
                        )
                        ClinicProfileStatisticItem(
                            number = monthlyRevenue,
                            label = "Revenue"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Action List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    ClinicProfileActionItem(
                        icon = null,
                        iconColor = Color(0xFF4A90E2),
                        text = "Edit Profile",
                        onClick = onEditProfile
                    )
                    ClinicProfileActionItem(
                        icon = null,
                        iconColor = Color.Gray,
                        text = "Services Management",
                        onClick = onServicesManagement
                    )
                    ClinicProfileActionItem(
                        icon = null,
                        iconColor = Color.Red,
                        text = "Logout",
                        onClick = onLogout
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ClinicProfileHeaderSection(
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
                    text = "Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ClinicProfileStatisticItem(
    number: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2B2B2B)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ClinicProfileActionItem(
    icon: ImageVector?,
    iconColor: Color,
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (text == "Logout") Color.Red else Color(0xFF2B2B2B)
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

