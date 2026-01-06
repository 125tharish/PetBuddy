package com.example.petbuddy.ui.theme.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PetDetailsViewScreen(
    onBack: () -> Unit = {},
    onCall: () -> Unit = {},
    onMessage: () -> Unit = {},
    onEmail: () -> Unit = {},
    onContactOwner: () -> Unit = {}
) {
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
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Pet Details View",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(40.dp)) // Balance the back button
        }
        
        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Pet Profile Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pet Image/Icon
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color(0xFFFF8A50).copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🐕",
                            fontSize = 80.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Max",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Golden Retriever",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Match Percentage
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(6.dp)
                                .background(Color(0xFFE0E0E0), RoundedCornerShape(3.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.95f)
                                    .background(Color(0xFF4CAF50), RoundedCornerShape(3.dp))
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "95% Match",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Owner Information Section
            Text(
                text = "Owner Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Owner Name
            InfoCard(
                icon = Icons.Default.Person,
                iconColor = Color(0xFFFF8A50),
                title = "Owner Name",
                value = "John Doe"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Phone Number
            InfoCard(
                icon = Icons.Default.Info,
                iconColor = Color(0xFF4CAF50),
                title = "Phone Number",
                value = "+1 (555) 123-4567",
                isClickable = true,
                onClick = onCall
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Email
            InfoCard(
                icon = Icons.Default.Info,
                iconColor = Color(0xFF2196F3),
                title = "Email",
                value = "john.doe@example.com",
                isClickable = true,
                onClick = onEmail
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Location
            InfoCard(
                icon = Icons.Default.LocationOn,
                iconColor = Color(0xFFE57373),
                title = "Last Known Location",
                value = "Central Park, 0.3 mi away"
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Pet Information Section
            Text(
                text = "Pet Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Breed
            InfoCard(
                icon = Icons.Default.Info,
                iconColor = Color(0xFFFF8A50),
                title = "Breed",
                value = "Golden Retriever"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Age
            InfoCard(
                icon = Icons.Default.Info,
                iconColor = Color(0xFFFF8A50),
                title = "Age",
                value = "3 years"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Color
            InfoCard(
                icon = Icons.Default.Info,
                iconColor = Color(0xFFFF8A50),
                title = "Primary Color",
                value = "Golden/Cream"
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Contact Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Contact Owner Button
            Button(
                onClick = onContactOwner,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Contact Owner",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // Message Button
            OutlinedButton(
                onClick = onMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFF8A50)
                ),
                border = BorderStroke(2.dp, Color(0xFFFF8A50)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFFF8A50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Send Message",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF8A50)
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    isClickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isClickable) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
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
                    .background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Title and Value
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
            
            // Arrow icon if clickable
            if (isClickable) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

