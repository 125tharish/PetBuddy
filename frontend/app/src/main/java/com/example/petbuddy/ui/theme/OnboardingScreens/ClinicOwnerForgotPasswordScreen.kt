package com.example.petbuddy.ui.theme.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.petbuddy.data.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun ClinicOwnerForgotPasswordScreen(
    onBack: () -> Unit,
    onSendResetCode: (String) -> Unit
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // Top Back Arrow + Title
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                fontSize = 20.sp,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(end = 12.dp)
            )
            Text(
                text = "Reset Password",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Mail Icon
        Text(
            text = "✉️",
            fontSize = 40.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Forgot Password?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Enter your email address and we'll send you a\ncode to reset your password",
            fontSize = 14.sp,
            color = Color(0xFF9E9E9E),
            lineHeight = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

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

        // Success Message
        successMessage?.let { success ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = success,
                    color = Color(0xFF2E7D32),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                errorMessage = null
                successMessage = null
            },
            placeholder = { Text("Email address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Send Reset Code Button
        Button(
            onClick = {
                val trimmedEmail = email.trim()
                
                if (trimmedEmail.isEmpty()) {
                    errorMessage = "Email is required"
                    return@Button
                }
                
                if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
                    errorMessage = "Please enter a valid email address"
                    return@Button
                }
                
                errorMessage = null
                successMessage = null
                isLoading = true
                
                coroutineScope.launch {
                    authRepository.clinicOwnerSendVerificationCode(trimmedEmail)
                        .onSuccess { response ->
                            isLoading = false
                            successMessage = response.message
                            // Navigate to verification screen after a short delay
                            kotlinx.coroutines.delay(1000)
                            onSendResetCode(trimmedEmail)
                        }
                        .onFailure { exception ->
                            isLoading = false
                            errorMessage = exception.message ?: "Failed to send verification code"
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
                Text(
                    text = "Send Reset Code",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Back to Sign In
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                color = Color(0xFF22C55E),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Back to Sign In",
                color = Color(0xFF22C55E),
                fontSize = 14.sp,
                modifier = Modifier.clickable { onBack() }
            )
        }
    }
}

