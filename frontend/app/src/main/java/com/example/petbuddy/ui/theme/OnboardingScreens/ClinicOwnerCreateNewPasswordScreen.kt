package com.example.petbuddy.ui.theme.OnboardingScreens

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petbuddy.data.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun ClinicOwnerCreateNewPasswordScreen(
    email: String,
    onBack: () -> Unit,
    onPasswordResetSuccess: () -> Unit
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "←",
                fontSize = 20.sp,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(end = 12.dp)
            )
            Text(
                text = "Create New Password",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "🔐",
            fontSize = 36.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Set a New Password",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
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

        OutlinedTextField(
            value = newPassword,
            onValueChange = { 
                newPassword = it
                errorMessage = null
            },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { 
                confirmPassword = it
                errorMessage = null
            },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val trimmedNewPassword = newPassword.trim()
                val trimmedConfirmPassword = confirmPassword.trim()
                
                if (trimmedNewPassword.isEmpty() || trimmedConfirmPassword.isEmpty()) {
                    errorMessage = "Both password fields are required"
                    return@Button
                }
                
                if (trimmedNewPassword.length < 6) {
                    errorMessage = "Password must be at least 6 characters"
                    return@Button
                }
                
                if (trimmedNewPassword != trimmedConfirmPassword) {
                    errorMessage = "Passwords do not match"
                    return@Button
                }
                
                errorMessage = null
                isLoading = true
                
                coroutineScope.launch {
                    authRepository.clinicOwnerResetPassword(email, trimmedNewPassword)
                        .onSuccess { response ->
                            isLoading = false
                            onPasswordResetSuccess()
                        }
                        .onFailure { exception ->
                            isLoading = false
                            errorMessage = exception.message ?: "Failed to reset password"
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
                Text("Reset Password", color = Color.White)
            }
        }
    }
}

