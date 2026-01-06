package com.example.petbuddy.ui.theme.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ViewSideBySideScreen(
    onBack: () -> Unit = {},
    onConfirmMatch: () -> Unit = {},
    onZoomOut: () -> Unit = {},
    onZoomIn: () -> Unit = {}
) {
    var zoomLevel by remember { mutableStateOf(100) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Section - Photo Comparison Area (Dark Blue-Gray Background)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF2C3E50))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Your Photo Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // "Your Photo" Label
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFE0E0E0),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Your Photo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Dog Emoji
                    Text(
                        text = "🐕",
                        fontSize = 120.sp
                    )
                }
                
                // Match Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // "Match • 95%" Label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Match",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Color(0xFF9E9E9E), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "95%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Dog Emoji
                    Text(
                        text = "🐕",
                        fontSize = 120.sp
                    )
                }
            }
        }
        
        // Bottom Section - Controls Area (Black Background)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Zoom Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Zoom Out Button
                    IconButton(
                        onClick = {
                            if (zoomLevel > 50) {
                                zoomLevel -= 10
                                onZoomOut()
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF2A2A2A), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Zoom Out",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Percentage Display
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(56.dp)
                            .background(Color(0xFF2A2A2A), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$zoomLevel %",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Zoom In Button
                    IconButton(
                        onClick = {
                            if (zoomLevel < 200) {
                                zoomLevel += 10
                                onZoomIn()
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF2A2A2A), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Zoom In",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Confirm Match Button
                Button(
                    onClick = onConfirmMatch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF8A50)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Confirm Match",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
    
    // Back Button (Top Left)
    IconButton(
        onClick = onBack,
        modifier = Modifier
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

