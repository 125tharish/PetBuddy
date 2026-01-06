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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun ClinicOwnerCreateAccountScreen(
    onBack: () -> Unit,
    onCreateAccount: () -> Unit,
    onSignIn: () -> Unit
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }
    val userSession = remember { UserSession(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
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
                text = "Create Account",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

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
            value = fullName,
            onValueChange = { 
                fullName = it
                errorMessage = null
            },
            label = { Text("Full name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                errorMessage = null
            },
            label = { Text("Email address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                errorMessage = null
            },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { 
                confirmPassword = it
                errorMessage = null
            },
            label = { Text("Confirm password") },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val trimmedFullName = fullName.trim()
                val trimmedEmail = email.trim()
                val trimmedPassword = password.trim()
                val trimmedConfirmPassword = confirmPassword.trim()

                // Validation
                if (trimmedFullName.isEmpty() || trimmedEmail.isEmpty() || 
                    trimmedPassword.isEmpty() || trimmedConfirmPassword.isEmpty()) {
                    errorMessage = "All fields are required"
                    return@Button
                }

                if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
                    errorMessage = "Please enter a valid email address"
                    return@Button
                }

                if (trimmedPassword.length < 6) {
                    errorMessage = "Password must be at least 6 characters"
                    return@Button
                }

                if (trimmedPassword != trimmedConfirmPassword) {
                    errorMessage = "Passwords do not match"
                    return@Button
                }

                errorMessage = null
                isLoading = true

                coroutineScope.launch {
                    authRepository.clinicOwnerSignup(
                        trimmedFullName,
                        trimmedEmail,
                        trimmedPassword,
                        trimmedConfirmPassword
                    )
                        .onSuccess { response ->
                            isLoading = false
                            // Save clinic owner session
                            response.clinic_user_id?.let { clinicUserId ->
                                userSession.saveUser(
                                    userId = clinicUserId,
                                    name = trimmedFullName,
                                    email = trimmedEmail,
                                    role = "clinic_owner"
                                )
                            }
                            onCreateAccount()
                        }
                        .onFailure { exception ->
                            isLoading = false
                            errorMessage = exception.message ?: "Failed to create account"
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
                Text("Create Account", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Already have an account? ")
            Text(
                text = "Sign In",
                color = Color(0xFF22C55E),
                modifier = Modifier.clickable { onSignIn() }
            )
        }
    }
}

