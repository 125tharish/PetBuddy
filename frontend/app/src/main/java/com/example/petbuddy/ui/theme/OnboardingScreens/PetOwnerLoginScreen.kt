package com.example.petbuddy.ui.theme.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import android.content.Context
import androidx.compose.ui.platform.LocalContext

@Composable
fun PetOwnerLoginScreen(
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onForgotPassword: () -> Unit,
    onBack: () -> Unit = {}
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Initialize repositories - use remember to avoid recreating on recomposition
    val authRepository = remember { AuthRepository() }
    val userSession = remember { UserSession(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        // Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                fontSize = 20.sp,
                modifier = Modifier
                    .clickable { onBack() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "🐾", fontSize = 40.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome Back",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2B2B2B)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Sign in to continue helping pets",
            fontSize = 14.sp,
            color = Color(0xFF9E9E9E)
        )

        Spacer(modifier = Modifier.height(32.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            placeholder = { Text("Email address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            placeholder = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Color.Gray
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Forgot Password?",
            fontSize = 12.sp,
            color = Color(0xFFFF8A50),
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onForgotPassword() }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                val trimmedEmail = email.trim()
                val trimmedPassword = password.trim()

                if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty()) {
                    errorMessage = "Email and password required"
                    return@Button
                }

                if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
                    errorMessage = "Please enter a valid email address"
                    return@Button
                }

                errorMessage = null
                isLoading = true

                coroutineScope.launch {
                    try {
                        val result = authRepository.login(trimmedEmail, trimmedPassword)
                        isLoading = false
                        
                        result.onSuccess { loginResponse ->
                            // Save user session
                            loginResponse.user_id?.let { userId ->
                                userSession.saveUser(
                                    userId = userId,
                                    name = loginResponse.name ?: "",
                                    email = loginResponse.email ?: trimmedEmail,
                                    role = loginResponse.role ?: "owner"
                                )
                            }
                            onSignIn()
                        }.onFailure { exception ->
                            errorMessage = exception.message ?: "Login failed. Please try again."
                        }
                    } catch (e: Exception) {
                        isLoading = false
                        val errorMsg = e.message ?: "Unable to connect to server"
                        // Show user-friendly error messages
                        errorMessage = if (errorMsg.contains("JsonReader") || errorMsg.contains("malformed JSON")) {
                            "Server response error. Please check if the server is running correctly."
                        } else {
                            errorMsg
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF8A50)
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
                    text = "Sign In",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account?",
                fontSize = 13.sp,
                color = Color(0xFF9E9E9E)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Sign Up",
                fontSize = 13.sp,
                color = Color(0xFFFF8A50),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { onSignUp() }
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            )
        }
    }
}
