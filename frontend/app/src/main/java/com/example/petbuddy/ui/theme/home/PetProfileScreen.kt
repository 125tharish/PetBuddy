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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.petbuddy.data.model.Pet
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.PetRepository
import kotlinx.coroutines.launch

@Composable
fun PetProfileScreen(
    petName: String = "Max",
    breed: String = "Golden Retriever",
    age: String = "3 years",
    onBack: () -> Unit = {},
    onVaccinationsClick: () -> Unit = {},
    onMedicationsClick: () -> Unit = {},
    onNutritionClick: () -> Unit = {},
    onGroomingClick: () -> Unit = {},
    onViewMedicalRecords: () -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val petRepository = remember { PetRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var petData by remember { mutableStateOf<Pet?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Fetch pet data from backend
    LaunchedEffect(petName) {
        val userId = userSession.getUserId()
        if (userId != -1 && petName.isNotEmpty()) {
            coroutineScope.launch {
                val result = petRepository.getMyPets(userId)
                result.onSuccess { pets ->
                    petData = pets.find { it.pet_name == petName }
                    isLoading = false
                }.onFailure {
                    isLoading = false
                }
            }
        } else {
            isLoading = false
        }
    }
    
    // Use fetched data or fallback to parameters
    val displayBreed = petData?.breed ?: breed
    val displayAge = petData?.age ?: age
    val microchipId = petData?.microchip_id ?: "Not registered"
    
    // Store photo URL and pet type in local variables to avoid smart cast issues
    val photoUrlRaw = petData?.photo_url
    val petType = petData?.pet_type
    
    // Convert relative URL to full URL if needed
    val photoUrl = photoUrlRaw?.let { url ->
        when {
            url.startsWith("http://") || url.startsWith("https://") -> url // Already a full URL
            url.startsWith("/") -> "http://10.129.3.54/pet_buddy$url" // Absolute path
            else -> "http://10.129.3.54/pet_buddy/$url" // Relative path
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
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
                text = "Pet Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // Scrollable Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                // Pet Information Header (Orange Section)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFF8A50))
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Pet Image
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color.White, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (photoUrl != null && photoUrl.isNotBlank()) {
                                // Display pet photo
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = petName,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White, RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // Display emoji placeholder
                                Text(
                                    text = when (petType?.lowercase()) {
                                        "dog" -> "\uD83D\uDC36"
                                        "cat" -> "\uD83D\uDC31"
                                        else -> "\uD83D\uDC3E"
                                    },
                                    fontSize = 56.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(20.dp))
                        
                        // Pet Name and Details
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = petName,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$displayBreed • $displayAge",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
                
                // Main Content - Centered
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Medical ID Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Header
                            Text(
                                text = "Medical ID",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            // Microchip
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Microchip",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (microchipId.isNullOrBlank()) "Not registered" else microchipId,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(28.dp))
                    
                    // Feature Tiles Grid (2x2) - Centered
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            FeatureTile(
                                icon = Icons.Default.FavoriteBorder, // Heart outline icon
                                iconColor = Color(0xFF4CAF50), // Green
                                label = "Vaccinations",
                                onClick = onVaccinationsClick,
                                modifier = Modifier.weight(1f)
                            )
                            FeatureTile(
                                icon = PillIcon, // Custom pill/capsule icon
                                iconColor = Color(0xFF9C27B0), // Purple
                                label = "Medications",
                                onClick = onMedicationsClick,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            FeatureTile(
                                icon = CalendarIcon, // Custom calendar icon
                                iconColor = Color(0xFFFF8A50), // Orange
                                label = "Nutrition",
                                onClick = onNutritionClick,
                                modifier = Modifier.weight(1f)
                            )
                            FeatureTile(
                                icon = ScissorsIcon, // Custom scissors icon
                                iconColor = Color(0xFF009688), // Teal
                                label = "Grooming",
                                onClick = onGroomingClick,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
        
        // View Medical Records Button - Centered
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            OutlinedButton(
                onClick = onViewMedicalRecords,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFF8A50)
                ),
                border = BorderStroke(2.dp, Color(0xFFFF8A50)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "View Medical Records",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF8A50)
                )
            }
        }
    }
}

// Custom Calendar Icon - Simple outline calendar
val CalendarIcon: ImageVector
    get() {
        return androidx.compose.ui.graphics.vector.ImageVector.Builder(
            name = "calendar",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
                strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
            ) {
                // Calendar body rectangle
                moveTo(5f, 5f)
                lineTo(19f, 5f)
                lineTo(19f, 19f)
                lineTo(5f, 19f)
                close()
                // Top horizontal line (separating header)
                moveTo(5f, 9f)
                lineTo(19f, 9f)
                // Left ring
                moveTo(7f, 3f)
                lineTo(7f, 6f)
                // Right ring
                moveTo(17f, 3f)
                lineTo(17f, 6f)
            }
        }.build()
    }

// Custom Pill/Capsule Icon - Horizontal capsule with two rounded ends and middle line
val PillIcon: ImageVector
    get() {
        return androidx.compose.ui.graphics.vector.ImageVector.Builder(
            name = "pill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Capsule outline - using rounded rectangle approach with lines
            path(
                fill = null,
                stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
                strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
            ) {
                // Draw capsule as a rounded rectangle outline
                // Top line (left to right)
                moveTo(6f, 9f)
                lineTo(18f, 9f)
                // Middle vertical line (separating two halves)
                moveTo(12f, 9f)
                lineTo(12f, 15f)
                // Bottom line (right to left)
                moveTo(18f, 15f)
                lineTo(6f, 15f)
                // Left rounded end (vertical line - rounded caps will make it look rounded)
                moveTo(6f, 9f)
                lineTo(6f, 15f)
                // Right rounded end (vertical line - rounded caps will make it look rounded)
                moveTo(18f, 9f)
                lineTo(18f, 15f)
            }
        }.build()
    }

// Custom Scissors Icon - Pair of scissors with two finger holes and crossed blades
val ScissorsIcon: ImageVector
    get() {
        return androidx.compose.ui.graphics.vector.ImageVector.Builder(
            name = "scissors",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Scissors outline
            path(
                fill = null,
                stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
                strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
            ) {
                // Left finger hole (circular outline)
                moveTo(8f, 4f)
                lineTo(10f, 4f)
                lineTo(10f, 8f)
                lineTo(8f, 8f)
                close()
                
                // Right finger hole (circular outline)
                moveTo(14f, 4f)
                lineTo(16f, 4f)
                lineTo(16f, 8f)
                lineTo(14f, 8f)
                close()
                
                // Left blade (from left hole downward and to the right)
                moveTo(9f, 8f)
                lineTo(5f, 19f)
                
                // Right blade (from right hole downward and to the left)
                moveTo(15f, 8f)
                lineTo(19f, 19f)
                
                // Pivot point (where blades cross - small X)
                moveTo(11.5f, 9f)
                lineTo(12.5f, 10f)
                moveTo(12.5f, 9f)
                lineTo(11.5f, 10f)
            }
        }.build()
    }

@Composable
fun FeatureTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconBackground: Color? = null
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon with optional background
            if (iconBackground != null) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(iconBackground, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

