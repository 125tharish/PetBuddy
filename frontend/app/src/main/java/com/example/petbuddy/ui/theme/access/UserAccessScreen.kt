package com.example.petbuddy.ui.theme.access

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserAccessScreen(
    onPetOwnerLogin: () -> Unit,
    onClinicOwnerLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Section - Orange Rounded Square with Two White Paw Prints
        Card(
            modifier = Modifier.size(140.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFF8A50)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFF8A50)),
                contentAlignment = Alignment.Center
            ) {
                // First Paw Print (Top-Left, larger, angled down-right)
                Box(
                    modifier = Modifier
                        .size(48.dp, 35.dp)
                        .offset(x = (-26).dp, y = (-18).dp)
                        .rotate(15f),
                    contentAlignment = Alignment.Center
                ) {
                    // Central pad (larger, heart-like shape, rounded at bottom)
                    Box(
                        modifier = Modifier
                            .size(19.dp, 16.dp)
                            .offset(y = 7.dp)
                            .shadow(3.dp, RoundedCornerShape(10.dp))
                            .background(Color.White, RoundedCornerShape(10.dp))
                    )
                    // Top left toe pad
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .offset(x = (-12).dp, y = (-5).dp)
                            .shadow(2.dp, CircleShape)
                            .background(Color.White, CircleShape)
                    )
                    // Top left-middle toe pad (slightly higher)
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .offset(x = (-3).dp, y = (-8).dp)
                            .shadow(2.dp, CircleShape)
                            .background(Color.White, CircleShape)
                    )
                    // Top right-middle toe pad (slightly higher)
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .offset(x = 3.dp, y = (-8).dp)
                            .shadow(2.dp, CircleShape)
                            .background(Color.White, CircleShape)
                    )
                    // Top right toe pad
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .offset(x = 12.dp, y = (-5).dp)
                            .shadow(2.dp, CircleShape)
                            .background(Color.White, CircleShape)
                    )
                }
                
                // Second Paw Print (Bottom-Right, slightly smaller, angled up-left, overlapping)
                Box(
                    modifier = Modifier
                        .size(44.dp, 32.dp)
                        .offset(x = 26.dp, y = 18.dp)
                        .rotate(-12f),
                    contentAlignment = Alignment.Center
                ) {
                    // Central pad (larger, heart-like shape, rounded at bottom)
                    Box(
                        modifier = Modifier
                            .size(18.dp, 14.dp)
                            .offset(y = 6.dp)
                            .shadow(3.dp, RoundedCornerShape(9.dp))
                            .background(Color.White, RoundedCornerShape(9.dp))
                    )
                    // Top left toe pad
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .offset(x = (-11).dp, y = (-4).dp)
                            .shadow(2.dp, CircleShape)
                            .background(Color.White, CircleShape)
                    )
                    // Top left-middle toe pad (slightly higher)
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .offset(x = (-3).dp, y = (-7).dp)
                            .shadow(2.dp, CircleShape)
                            .background(Color.White, CircleShape)
                    )
                    // Top right-middle toe pad (slightly higher)
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .offset(x = 3.dp, y = (-7).dp)
                            .shadow(2.dp, CircleShape)
                            .background(Color.White, CircleShape)
                    )
                    // Top right toe pad
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .offset(x = 11.dp, y = (-4).dp)
                            .shadow(2.dp, CircleShape)
                            .background(Color.White, CircleShape)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to Pet Buddy",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2B2B2B)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Choose how you want to continue",
            fontSize = 14.sp,
            color = Color(0xFF9E9E9E)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 🐾 PET OWNER LOGIN
        Button(
            onClick = {
                onPetOwnerLogin()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = true,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF8A50)
            )
        ) {
            Text(
                text = "Pet Owner Login",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🏥 CLINIC OWNER LOGIN
        Button(
            onClick = {
                onClinicOwnerLogin()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = true,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF22C55E)
            )
        ) {
            Text(
                text = "Clinic Owner Login",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

