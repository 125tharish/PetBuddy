package com.example.petbuddy.ui.theme.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FAQsScreen(
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF8A50))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Frequently Asked Questions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            FAQItem(
                question = "How to add a pet?",
                answer = "Go to My Pets section from your profile screen and tap the 'Add New Pet' button. Fill in your pet's details including name, breed, age, and upload a photo."
            )
            
            FAQItem(
                question = "How to report a lost pet?",
                answer = "Navigate to the Home screen and tap on 'Report Lost Pet'. Fill in the details about your pet including last seen location, description, and upload photos. The community will be notified to help find your pet."
            )
            
            FAQItem(
                question = "How to book an appointment?",
                answer = "Use the Quick Search feature to find nearby veterinary clinics. Select a clinic, view available time slots, and book your preferred appointment time. You'll receive a confirmation notification."
            )
            
            FAQItem(
                question = "How to update my profile?",
                answer = "Go to your Profile screen and tap on 'Edit Profile'. You can update your name, email, phone number, and profile picture. Don't forget to save your changes."
            )
            
            FAQItem(
                question = "How does AI Photo Matching work?",
                answer = "The AI Photo Matching feature uses advanced image recognition to compare photos of found pets with reported lost pets. Upload a photo of a found pet, and the system will search for potential matches in the database."
            )
            
            FAQItem(
                question = "How to contact a pet owner?",
                answer = "When you find a match or see a lost pet report, you can tap on 'Contact Owner' to send a message or call the owner directly using the provided contact information."
            )
            
            FAQItem(
                question = "How to share a lost pet alert?",
                answer = "After reporting a lost pet, you can share the alert on social media platforms or with your contacts by tapping the 'Share Alert' button. This helps spread the word quickly."
            )
            
            FAQItem(
                question = "How to manage vaccination schedules?",
                answer = "Go to your pet's profile, select 'Medical Records', and then 'Vaccination Schedule'. You can view upcoming vaccinations and add new vaccination records. The app will send you reminders before due dates."
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FAQItem(
    question: String,
    answer: String
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2B2B),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = Color(0xFFFF8A50),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = answer,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

