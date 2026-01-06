package com.example.petbuddy.ui.theme.OnboardingScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petbuddy.data.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun VerificationScreen(
    email: String,
    onBack: () -> Unit,
    onVerify: (String) -> Unit,
    onResend: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val codes = remember { List(6) { mutableStateOf("") } }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isResending by remember { mutableStateOf(false) }
    
    // Auto-focus first field on composition
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // Back + Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "←",
                fontSize = 20.sp,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(end = 12.dp)
            )
            Text(
                text = "Verification",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "🛡️",
            fontSize = 36.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Enter Verification Code",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We sent a 6-digit code to your email",
            fontSize = 14.sp,
            color = Color(0xFF9E9E9E),
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

        // OTP Boxes
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            codes.forEachIndexed { index, state ->
                OutlinedTextField(
                    value = state.value,
                    onValueChange = { newValue ->
                        // Handle paste (if user pastes 6 digits)
                        if (newValue.length == 6 && newValue.all { it.isDigit() }) {
                            newValue.forEachIndexed { i, char ->
                                if (i < codes.size) {
                                    codes[i].value = char.toString()
                                }
                            }
                            // Focus last field and hide keyboard
                            focusRequesters[5].requestFocus()
                            keyboardController?.hide()
                            errorMessage = null
                        } else if (newValue.length <= 1) {
                            // Only allow single digit
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                state.value = newValue
                                errorMessage = null
                                
                                // Move to next field if digit entered
                                if (newValue.isNotEmpty() && index < focusRequesters.size - 1) {
                                    focusRequesters[index + 1].requestFocus()
                                } else if (newValue.isEmpty() && index > 0) {
                                    // Move to previous field if deleted
                                    focusRequesters[index - 1].requestFocus()
                                } else if (newValue.isNotEmpty() && index == focusRequesters.size - 1) {
                                    // Hide keyboard when last field is filled
                                    keyboardController?.hide()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .width(48.dp)
                        .height(56.dp)
                        .focusRequester(focusRequesters[index]),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val verificationCode = codes.joinToString("") { it.value }
                
                if (verificationCode.length != 6) {
                    errorMessage = "Please enter the complete 6-digit code"
                    return@Button
                }
                
                errorMessage = null
                isLoading = true
                
                coroutineScope.launch {
                    authRepository.verifyCode(email, verificationCode)
                        .onSuccess { response ->
                            isLoading = false
                            onVerify(email)
                        }
                        .onFailure { exception ->
                            isLoading = false
                            errorMessage = exception.message ?: "Invalid verification code"
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
                    text = "Verify Code",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Didn't receive code?",
                fontSize = 13.sp,
                color = Color(0xFF9E9E9E)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isResending) "Resending..." else "Resend",
                fontSize = 13.sp,
                color = Color(0xFFFF8A50),
                modifier = Modifier.clickable { 
                    if (!isResending) {
                        isResending = true
                        coroutineScope.launch {
                            authRepository.sendVerificationCode(email)
                                .onSuccess {
                                    isResending = false
                                }
                                .onFailure {
                                    isResending = false
                                    errorMessage = "Failed to resend code. Please try again."
                                }
                        }
                    }
                }
            )
        }
    }
}
