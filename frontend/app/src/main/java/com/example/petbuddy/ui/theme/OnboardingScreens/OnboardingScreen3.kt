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
fun OnboardingScreen3(
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

            // PURPLE OUTER CARD
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .shadow(14.dp, RoundedCornerShape(26.dp))
                    .background(
                        color = Color(0xFFB77CFF),
                        shape = RoundedCornerShape(26.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                // INNER WHITE CARD
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .background(Color.White, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("📷", fontSize = 32.sp, color = Color(0xFF9B5CFF))
                        Text("✨ ✨ ✨", fontSize = 18.sp, color = Color(0xFFFFB703))
                        Text("✔", fontSize = 24.sp, color = Color(0xFF00C896))
                    }
                }

                // FLOATING GREEN ICON (INSIDE BOX → align() WORKS)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFF00D1A3), CircleShape)
                        .align(Alignment.TopEnd)
                        .offset(x = 14.dp, y = (-14).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✨", fontSize = 18.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "AI Photo Matching",
                fontSize = 20.sp,
                color = Color(0xFF2B2B2B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Advanced AI recognizes and matches pet\nphotos to help reunite lost pets with their\nfamilies faster",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // PAGE INDICATOR (3rd ACTIVE)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {

                repeat(2) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFFDADADA), CircleShape)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(26.dp)
                        .height(6.dp)
                        .background(Color(0xFFFF8A50), RoundedCornerShape(50))
                )

                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFFDADADA), CircleShape)
                )
            }
        }

        /* ---------- BOTTOM NEXT BUTTON ---------- */
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(60.dp)
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
