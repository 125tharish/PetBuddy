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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onLostPetAlertsToggle: (Boolean) -> Unit = {},
    onLocationServicesToggle: (Boolean) -> Unit = {},
    onMessagesToggle: (Boolean) -> Unit = {}
) {
    var lostPetAlertsEnabled by remember { mutableStateOf(true) }
    var locationServicesEnabled by remember { mutableStateOf(true) }
    var messagesEnabled by remember { mutableStateOf(true) }
    
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
                text = "Settings",
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
            
            // Section Title
            Text(
                text = "Notifications & Location",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Lost Pet Alerts Card
            SettingCard(
                icon = Icons.Default.Notifications,
                iconColor = Color(0xFFFF8A50),
                title = "Lost Pet Alerts",
                description = "Nearby sightings",
                isEnabled = lostPetAlertsEnabled,
                onToggle = {
                    lostPetAlertsEnabled = it
                    onLostPetAlertsToggle(it)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Location Services Card
            SettingCard(
                icon = Icons.Default.LocationOn,
                iconColor = Color(0xFFFF8A50),
                title = "Location Services",
                description = "For nearby alerts",
                isEnabled = locationServicesEnabled,
                onToggle = {
                    locationServicesEnabled = it
                    onLocationServicesToggle(it)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Messages Card
            SettingCard(
                icon = Icons.Default.Info, // Chat icon placeholder
                iconColor = Color(0xFFFF8A50),
                title = "Messages",
                description = "From other users",
                isEnabled = messagesEnabled,
                onToggle = {
                    messagesEnabled = it
                    onMessagesToggle(it)
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Title and Description
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Toggle Switch
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4CAF50) // Green when enabled
                )
            )
        }
    }
}

