package com.example.petbuddy.ui.theme.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun AIAnalysisScreen(
    onBack: () -> Unit = {},
    onMatchClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Header Section (Purple Background)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF9C27B0)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // AI Analysis Complete Title
                Text(
                    text = "AI Analysis Complete",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Found 3 potential matches",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Confidence Score Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFAB47BC), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Confidence Score",
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "95%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
        
        // Main Content Area (White Background)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // Best Matches Heading
            Text(
                text = "Best Matches",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Match Cards
            MatchCard(
                petName = "Max",
                status = "LOST",
                statusColor = Color(0xFFE57373),
                breed = "Golden Retriever",
                distance = "0.3 mi away",
                matchPercentage = 95,
                progressColor = Color(0xFF4CAF50),
                onCardClick = { onMatchClick("Max") }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            MatchCard(
                petName = "Buddy",
                status = "LOST",
                statusColor = Color(0xFFE57373),
                breed = "Golden Retriever",
                distance = "1.2 mi away",
                matchPercentage = 87,
                progressColor = Color(0xFFFFC107),
                onCardClick = { onMatchClick("Buddy") }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            MatchCard(
                petName = "Charlie",
                status = "FOUND",
                statusColor = Color(0xFF81C784),
                breed = "Labrador Mix",
                distance = "2.1 mi away",
                matchPercentage = 72,
                progressColor = Color(0xFFFFC107),
                onCardClick = { onMatchClick("Charlie") }
            )
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

@Composable
fun MatchCard(
    petName: String,
    status: String,
    statusColor: Color,
    breed: String,
    distance: String,
    matchPercentage: Int,
    progressColor: Color,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
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
            // Pet Icon (Gray square with dog emoji placeholder)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🐕",
                    fontSize = 32.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Pet Information
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = petName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = breed,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = distance,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Progress Bar and Percentage
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                // Progress Bar
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(6.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(3.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width((60.dp * matchPercentage / 100))
                            .background(progressColor, RoundedCornerShape(3.dp))
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$matchPercentage%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}

