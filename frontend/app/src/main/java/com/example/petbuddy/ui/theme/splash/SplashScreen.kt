package com.example.petbuddy.ui.theme.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2500) // splash duration
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFA44D),
                        Color(0xFFFF7A00)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            /* Pet Buddy Logo - Orange Rounded Square with Two White Paw Prints */
            Card(
                modifier = Modifier.size(160.dp),
                shape = RoundedCornerShape(32.dp),
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
                            .size(55.dp, 40.dp)
                            .offset(x = (-30).dp, y = (-20).dp)
                            .rotate(15f),
                        contentAlignment = Alignment.Center
                    ) {
                        // Central pad (larger, heart-like shape, rounded at bottom)
                        Box(
                            modifier = Modifier
                                .size(22.dp, 18.dp)
                                .offset(y = 8.dp)
                                .shadow(3.dp, RoundedCornerShape(11.dp))
                                .background(Color.White, RoundedCornerShape(11.dp))
                        )
                        // Top left toe pad
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .offset(x = (-14).dp, y = (-6).dp)
                                .shadow(2.dp, CircleShape)
                                .background(Color.White, CircleShape)
                        )
                        // Top left-middle toe pad (slightly higher)
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .offset(x = (-4).dp, y = (-9).dp)
                                .shadow(2.dp, CircleShape)
                                .background(Color.White, CircleShape)
                        )
                        // Top right-middle toe pad (slightly higher)
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .offset(x = 4.dp, y = (-9).dp)
                                .shadow(2.dp, CircleShape)
                                .background(Color.White, CircleShape)
                        )
                        // Top right toe pad
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .offset(x = 14.dp, y = (-6).dp)
                                .shadow(2.dp, CircleShape)
                                .background(Color.White, CircleShape)
                        )
                    }
                    
                    // Second Paw Print (Bottom-Right, slightly smaller, angled up-left, overlapping)
                    Box(
                        modifier = Modifier
                            .size(50.dp, 36.dp)
                            .offset(x = 30.dp, y = 20.dp)
                            .rotate(-12f),
                        contentAlignment = Alignment.Center
                    ) {
                        // Central pad (larger, heart-like shape, rounded at bottom)
                        Box(
                            modifier = Modifier
                                .size(20.dp, 16.dp)
                                .offset(y = 7.dp)
                                .shadow(3.dp, RoundedCornerShape(10.dp))
                                .background(Color.White, RoundedCornerShape(10.dp))
                        )
                        // Top left toe pad
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .offset(x = (-13).dp, y = (-5).dp)
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
                                .offset(x = 13.dp, y = (-5).dp)
                                .shadow(2.dp, CircleShape)
                                .background(Color.White, CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "PET BUDDY",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "♡  Your Pet's Best Friend",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
    }
}
