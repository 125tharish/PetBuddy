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
fun OnboardingScreen4(
    onGetStartedClick: () -> Unit
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

            // GREEN CARD
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .shadow(14.dp, RoundedCornerShape(26.dp))
                    .background(
                        color = Color(0xFF22C55E),
                        shape = RoundedCornerShape(26.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                // GRID OF WHITE TILES
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        FeatureTile("❤️", "Health")
                        FeatureTile("📅", "Schedule")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        FeatureTile("💊", "Meds")
                        FeatureTile("📈", "Track")
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Complete Pet Care",
                fontSize = 20.sp,
                color = Color(0xFF2B2B2B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Manage health records, vaccinations,\nmedications, and get personalized care tips\nfor your pets",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // PAGE INDICATOR (4th ACTIVE)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {

                repeat(3) {
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
            }
        }

        /* -------- BOTTOM GET STARTED BUTTON -------- */
        Button(
            onClick = onGetStartedClick,
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
                text = "Get Started",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun FeatureTile(
    icon: String,
    label: String
) {
    Box(
        modifier = Modifier
            .size(84.dp)
            .background(Color.White, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(label, fontSize = 12.sp, color = Color(0xFF2B2B2B))
        }
    }
}
