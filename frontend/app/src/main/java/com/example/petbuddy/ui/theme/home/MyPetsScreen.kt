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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.petbuddy.data.model.Pet
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.PetRepository
import kotlinx.coroutines.launch

@Composable
fun MyPetsScreen(
    onBack: () -> Unit = {},
    onPetClick: (String) -> Unit = {},
    onAddNewPet: () -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val petRepository = remember { PetRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var pets by remember { mutableStateOf<List<Pet>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        val userId = userSession.getUserId()
        if (userId == -1) {
            errorMessage = "Please login first"
            isLoading = false
            return@LaunchedEffect
        }
        
        coroutineScope.launch {
            val result = petRepository.getMyPets(userId)
            isLoading = false
            result.onSuccess { petList ->
                pets = petList
            }.onFailure { exception ->
                errorMessage = exception.message
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
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
                text = "My Pets",
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
                    Text(
                        text = errorMessage ?: "Error loading pets",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                pets.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No pets yet. Add your first pet!",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    pets.forEach { pet ->
                        // Convert relative URL to full URL if needed
                        // PHP returns pet_image field which is mapped to photo_url
                        val photoUrl = pet.photo_url?.let { url ->
                            when {
                                url.isBlank() -> null // Empty string means no image
                                url.startsWith("http://") || url.startsWith("https://") -> url // Already a full URL
                                url.startsWith("/") -> "http://10.129.3.54/pet_buddy$url" // Absolute path
                                else -> "http://10.129.3.54/pet_buddy/$url" // Relative path
                            }
                        }
                        
                        PetCard(
                            petName = pet.pet_name,
                            breed = pet.breed ?: "Unknown",
                            age = pet.age ?: "Unknown",
                            color = pet.color,
                            microchipId = pet.microchip_id,
                            petEmoji = when (pet.pet_type.lowercase()) {
                                "dog" -> "\uD83D\uDC36"
                                "cat" -> "\uD83D\uDC31"
                                else -> "\uD83D\uDC3E"
                            },
                            photoUrl = photoUrl,
                            onClick = { onPetClick(pet.pet_name) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Add New Pet Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = onAddNewPet,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8A50)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add New Pet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PetCard(
    petName: String,
    breed: String,
    age: String,
    color: String? = null,
    microchipId: String? = null,
    petEmoji: String,
    photoUrl: String? = null,
    onClick: () -> Unit
) {
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
            // Pet Photo or Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (photoUrl != null && photoUrl.isNotBlank()) {
                    // Display pet photo
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = petName,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(28.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Display emoji placeholder
                    Text(
                        text = petEmoji,
                        fontSize = 32.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Pet Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = petName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = breed,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                if (age.isNotBlank() && age != "Unknown") {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Age: $age",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                if (!color.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Color: $color",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

