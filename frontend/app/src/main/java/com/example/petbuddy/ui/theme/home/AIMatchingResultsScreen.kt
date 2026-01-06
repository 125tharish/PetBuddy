package com.example.petbuddy.ui.theme.home

import android.net.Uri
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.petbuddy.data.model.ImageComparisonResponse
import com.example.petbuddy.data.model.PetMatch

@Composable
fun AIMatchingResultsScreen(
    uploadedImageUri: Uri?,
    comparisonResult: ImageComparisonResponse,
    onBack: () -> Unit = {},
    onMatchClick: (PetMatch) -> Unit = {},
    onRetakePhoto: () -> Unit = {}
) {
    val matches = comparisonResult.matches ?: emptyList()
    val topMatch = matches.firstOrNull()
    val similarityScore = (comparisonResult.similarity_score ?: 0f) * 100
    val confidence = (comparisonResult.confidence ?: 0f) * 100
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Header Section with Match Score
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (similarityScore >= 80) {
                            listOf(Color(0xFF4CAF50), Color(0xFF66BB6A))
                        } else if (similarityScore >= 60) {
                            listOf(Color(0xFFFFC107), Color(0xFFFFD54F))
                        } else {
                            listOf(Color(0xFF9C27B0), Color(0xFFAB47BC))
                        }
                    )
                )
                .padding(vertical = 40.dp, horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // AI Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (similarityScore >= 80) Color(0xFF4CAF50) else Color(0xFF9C27B0),
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "AI Analysis Complete",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = if (matches.isNotEmpty()) {
                        "Found ${matches.size} potential match${if (matches.size > 1) "es" else ""}"
                    } else {
                        "No matches found"
                    },
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                
                if (topMatch != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Confidence Score Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Top Match Confidence",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${similarityScore.toInt()}%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Back Button (Top Left)
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .offset(x = 0.dp, y = (-280).dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Main Content Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // Uploaded Image Section
            if (uploadedImageUri != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Your Photo",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        AsyncImage(
                            model = uploadedImageUri,
                            contentDescription = "Uploaded pet photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Matches Section
            if (matches.isNotEmpty()) {
                Text(
                    text = "Potential Matches",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                matches.forEachIndexed { index, match ->
                    PetMatchCard(
                        match = match,
                        rank = index + 1,
                        onClick = { onMatchClick(match) }
                    )
                    
                    if (index < matches.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            } else {
                // No Matches Found
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFFFF8A50),
                            modifier = Modifier.size(64.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "No Matches Found",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "We couldn't find any matching pets in our database. Try taking another photo or check back later.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = onRetakePhoto,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF8A50)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Retake Photo",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PetMatchCard(
    match: PetMatch,
    rank: Int,
    onClick: () -> Unit
) {
    val similarityPercent = (match.similarity * 100).toInt()
    val progressColor = when {
        similarityPercent >= 80 -> Color(0xFF4CAF50)
        similarityPercent >= 60 -> Color(0xFFFFC107)
        else -> Color(0xFFFF9800)
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
            // Rank Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(progressColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Pet Image or Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (match.image_url != null && match.image_url.isNotEmpty()) {
                    AsyncImage(
                        model = match.image_url,
                        contentDescription = match.pet_name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = if (match.pet_type == "Cat") "🐱" else "🐕",
                        fontSize = 32.sp
                    )
                }
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
                        text = match.pet_name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LOST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE57373),
                        modifier = Modifier
                            .background(Color(0xFFFFEBEE), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${match.pet_type}${match.breed?.let { " • $it" } ?: ""}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                
                if (match.location != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = match.location,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                if (match.owner_name != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Owner: ${match.owner_name}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Similarity Score
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
                            .width((60.dp * similarityPercent / 100))
                            .background(progressColor, RoundedCornerShape(3.dp))
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "$similarityPercent%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

