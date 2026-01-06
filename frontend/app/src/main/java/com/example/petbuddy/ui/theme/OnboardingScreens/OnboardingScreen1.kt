package com.example.petbuddy.ui.theme.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen1(
    onNextClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {

        /* ---------- CENTER CONTENT ---------- */
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ORANGE CARD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
                    .shadow(12.dp, RoundedCornerShape(22.dp))
                    .background(
                        color = Color(0xFFFF8A50),
                        shape = RoundedCornerShape(22.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("📍", fontSize = 22.sp)
                    Text("🔔", fontSize = 22.sp)
                    Text("🔍", fontSize = 22.sp)
                }

                // FLOATING CIRCLE
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF3FD1C2), CircleShape)
                        .align(Alignment.TopEnd)
                        .offset(x = 14.dp, y = (-14).dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "All-in-One Platform",
                fontSize = 20.sp,
                color = Color(0xFF2B2B2B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Track lost pets, manage care, and access\nemergency support—all in one place",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // PAGE INDICATOR
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {

                Box(
                    modifier = Modifier
                        .width(26.dp)
                        .height(6.dp)
                        .background(
                            Color(0xFFFF8A50),
                            RoundedCornerShape(50)
                        )
                )

                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFFDADADA), CircleShape)
                    )
                }
            }
        }

        /* ---------- BOTTOM NEXT BUTTON ---------- */
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(60.dp) // increased size
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF8A50)
            )
        ) {
            Text(
                text = "Next",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}
