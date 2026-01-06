package com.example.petbuddy.ui.theme.OnboardingScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.ClinicProfileRepository
import kotlinx.coroutines.launch

@Composable
fun ClinicOwnerCompleteProfileScreen(
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val profileRepository = remember { ClinicProfileRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    val clinicUserId = userSession.getUserId()
    
    var name by remember { mutableStateOf("") }
    var clinicName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Complete Profile",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Help us personalize your experience",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .background(Color(0xFF22C55E), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("👤", fontSize = 36.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = clinicName,
            onValueChange = { clinicName = it },
            label = { Text("Clinic name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone number") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("City, State") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error Message
        errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (clinicUserId == -1) {
                    errorMessage = "User session not found. Please login again."
                    return@Button
                }
                
                // Validation
                if (name.trim().isEmpty()) {
                    errorMessage = "Full name is required"
                    return@Button
                }
                if (clinicName.trim().isEmpty()) {
                    errorMessage = "Clinic name is required"
                    return@Button
                }
                if (email.trim().isEmpty()) {
                    errorMessage = "Email address is required"
                    return@Button
                }
                
                isLoading = true
                errorMessage = null
                
                coroutineScope.launch {
                    profileRepository.updateClinicProfile(
                        clinicUserId = clinicUserId,
                        fullName = name.trim(),
                        clinicName = clinicName.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        address = location.trim()
                    )
                        .onSuccess {
                            isLoading = false
                            // Update UserSession with new data
                            userSession.saveUser(
                                userId = clinicUserId,
                                name = name.trim(),
                                email = email.trim(),
                                role = "clinic_owner"
                            )
                            onComplete()
                        }
                        .onFailure { exception ->
                            isLoading = false
                            errorMessage = exception.message ?: "Failed to save profile"
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF22C55E)
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text("Complete Setup", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Skip for now",
            color = Color.Gray,
            modifier = Modifier.clickable { onSkip() }
        )
    }
}

