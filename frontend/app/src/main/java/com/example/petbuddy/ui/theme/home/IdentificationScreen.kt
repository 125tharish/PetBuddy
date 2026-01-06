package com.example.petbuddy.ui.theme.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petbuddy.data.repository.LostPetRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun IdentificationScreen(
    lostId: Int = -1,
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    val repository = remember { LostPetRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var microchipEnabled by remember { mutableStateOf(false) }
    var microchipNumber by remember { mutableStateOf("") }
    var collarEnabled by remember { mutableStateOf(false) }
    var collarDescription by remember { mutableStateOf("") }
    var idTagEnabled by remember { mutableStateOf(false) }
    var idTagInfo by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
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
                text = "Identification",
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
            
            // Identification Details Section
            Text(
                text = "Identification Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "This information helps verify your pet when found.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Microchip Card
            IdentificationCard(
                icon = Icons.Default.Info,
                iconColor = Color(0xFF64B5F6),
                title = "Microchip",
                subtitle = "Permanent ID",
                isEnabled = microchipEnabled,
                onToggle = { microchipEnabled = it },
                placeholder = "Enter microchip number",
                value = microchipNumber,
                onValueChange = { microchipNumber = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Collar Card
            IdentificationCard(
                icon = Icons.Default.Info,
                iconColor = Color(0xFFFF8A50),
                title = "Collar",
                subtitle = "Color & description",
                isEnabled = collarEnabled,
                onToggle = { collarEnabled = it },
                placeholder = "e.g., Blue with stars",
                value = collarDescription,
                onValueChange = { collarDescription = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ID Tag Card
            IdentificationCard(
                icon = Icons.Default.Info,
                iconColor = Color(0xFFFFC107),
                title = "ID Tag",
                subtitle = "Tag information",
                isEnabled = idTagEnabled,
                onToggle = { idTagEnabled = it },
                placeholder = "Tag text or phone number",
                value = idTagInfo,
                onValueChange = { idTagInfo = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Error Message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFD32F2F),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Success Message
            successMessage?.let { success ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = success,
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF2E7D32),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Continue Button and Skip Link
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (lostId == -1) {
                        errorMessage = "Lost pet ID not found. Please go back and try again."
                        return@Button
                    }
                    
                    errorMessage = null
                    isLoading = true
                    
                    coroutineScope.launch {
                        val result = repository.updateIdentification(
                            lostId = lostId,
                            hasMicrochip = microchipEnabled,
                            microchipNumber = microchipNumber.trim().takeIf { it.isNotEmpty() },
                            hasCollar = collarEnabled,
                            collarDescription = collarDescription.trim().takeIf { it.isNotEmpty() },
                            hasIdTag = idTagEnabled,
                            idTagText = idTagInfo.trim().takeIf { it.isNotEmpty() }
                        )
                        
                        isLoading = false
                        result.onSuccess { response ->
                            successMessage = response.message ?: "Identification details saved successfully"
                            // Navigate to next screen after a short delay
                            delay(500)
                            onContinue()
                        }.onFailure { exception ->
                            errorMessage = exception.message ?: "Failed to save identification details"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8A50)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 6.dp
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Continue",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Skip this step link
            TextButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(
                    text = "Skip this step",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun IdentificationCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
) {
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
            // Header with icon, title, and toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                
                // Title and Subtitle
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                // Toggle Switch
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF4CAF50),
                        checkedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
                    )
                )
            }
            
            // Input Field (shown when enabled)
            if (isEnabled) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = { Text(placeholder) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = isEnabled,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8A50),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        disabledBorderColor = Color(0xFFE0E0E0)
                    )
                )
            }
        }
    }
}

