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
fun OnboardingScreen2(
    onNextClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {

        /* -------- CENTER CONTENT -------- */
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TEAL MAP CARD
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .shadow(14.dp, RoundedCornerShape(26.dp))
                    .background(
                        color = Color(0xFF6FE1D3),
                        shape = RoundedCornerShape(26.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                // GRID BACKGROUND
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(3) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(
                                            Color(0xFFBDF1EC),
                                            RoundedCornerShape(10.dp)
                                        )
                                )
                            }
                        }
                    }
                }

                // CENTER LOCATION PIN
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFFF4C4C), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📍", fontSize = 18.sp, color = Color.White)
                }

                // YELLOW ALERT ICON
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFFFB703), CircleShape)
                        .align(Alignment.BottomStart)
                        .offset(x = (-14).dp, y = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📡", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Real-Time Alerts",
                fontSize = 20.sp,
                color = Color(0xFF2B2B2B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Get instant notifications and see live\nupdates on an interactive map when pets\nare spotted",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // PAGE INDICATOR (SECOND ACTIVE)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {

                repeat(1) {
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

                repeat(2) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFFDADADA), CircleShape)
                    )
                }
            }
        }

        /* -------- BOTTOM NEXT BUTTON -------- */
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
