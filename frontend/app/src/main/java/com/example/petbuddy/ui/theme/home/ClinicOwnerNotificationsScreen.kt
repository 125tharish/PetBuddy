package com.example.petbuddy.ui.theme.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ClinicNotification(
    val id: String,
    val type: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val isUnread: Boolean,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBackground: Color
)

@Composable
fun ClinicOwnerNotificationsScreen(
    onBack: () -> Unit = {},
    onMarkAllRead: () -> Unit = {},
    onNotificationClick: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("All") }
    var allNotificationsRead by remember { mutableStateOf(false) }
    
    val allNotifications = listOf(
        ClinicNotification(
            id = "1",
            type = "Appointment",
            title = "New Appointment Scheduled",
            description = "Max - Golden Retriever scheduled for Jan 25, 2025 at 09:00 AM",
            timestamp = "5 min ago",
            isUnread = true,
            icon = Icons.Default.Info,
            iconColor = Color(0xFF4A90E2),
            iconBackground = Color(0xFF4A90E2)
        ),
        ClinicNotification(
            id = "2",
            type = "Appointment",
            title = "Appointment Cancelled",
            description = "Luna - Persian Cat cancelled appointment for Jan 24, 2025",
            timestamp = "15 min ago",
            isUnread = true,
            icon = Icons.Default.Info,
            iconColor = Color(0xFFE74C3C),
            iconBackground = Color(0xFFE74C3C)
        ),
        ClinicNotification(
            id = "3",
            type = "Patient",
            title = "New Patient Registered",
            description = "Bella - German Shepherd registered by Jessica Martinez",
            timestamp = "1 hour ago",
            isUnread = true,
            icon = Icons.Default.Person,
            iconColor = Color(0xFF50C878),
            iconBackground = Color(0xFF50C878)
        ),
        ClinicNotification(
            id = "4",
            type = "Payment",
            title = "Payment Received",
            description = "Payment of $75.00 received for Charlie's appointment",
            timestamp = "2 hours ago",
            isUnread = false,
            icon = Icons.Default.Info,
            iconColor = Color(0xFF50C878),
            iconBackground = Color(0xFF50C878)
        ),
        ClinicNotification(
            id = "5",
            type = "Appointment",
            title = "Appointment Reminder",
            description = "Reminder: Buddy - Beagle appointment tomorrow at 02:00 PM",
            timestamp = "3 hours ago",
            isUnread = false,
            icon = Icons.Default.Info,
            iconColor = Color(0xFFFF8A50),
            iconBackground = Color(0xFFFF8A50)
        ),
        ClinicNotification(
            id = "6",
            type = "Patient",
            title = "Medical Record Updated",
            description = "Medical records updated for Milo - Siamese Cat",
            timestamp = "5 hours ago",
            isUnread = false,
            icon = Icons.Default.Info,
            iconColor = Color(0xFF9B59B6),
            iconBackground = Color(0xFF9B59B6)
        ),
        ClinicNotification(
            id = "7",
            type = "Appointment",
            title = "Appointment Rescheduled",
            description = "Rocky - Bulldog rescheduled to Jan 27, 2025 at 10:00 AM",
            timestamp = "1 day ago",
            isUnread = false,
            icon = Icons.Default.Info,
            iconColor = Color(0xFF4A90E2),
            iconBackground = Color(0xFF4A90E2)
        ),
        ClinicNotification(
            id = "8",
            type = "Payment",
            title = "Payment Pending",
            description = "Payment pending for Whiskers - Maine Coon appointment",
            timestamp = "2 days ago",
            isUnread = false,
            icon = Icons.Default.Info,
            iconColor = Color(0xFFFF8A50),
            iconBackground = Color(0xFFFF8A50)
        )
    )
    
    val filteredNotifications = when (selectedTab) {
        "All" -> allNotifications
        "Appointments" -> allNotifications.filter { it.type == "Appointment" }
        "Patients" -> allNotifications.filter { it.type == "Patient" }
        "Payments" -> allNotifications.filter { it.type == "Payment" }
        else -> allNotifications
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F3))
    ) {
        // Header
        ClinicNotificationsHeaderSection(
            onBack = onBack,
            onMarkAllRead = {
                allNotificationsRead = true
                onMarkAllRead()
            }
        )
        
        // Filter Tabs
        ClinicNotificationsFilterTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        
        // Notification List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            if (filteredNotifications.isEmpty()) {
                EmptyNotificationsSection()
            } else {
                filteredNotifications.forEach { notification ->
                    ClinicNotificationCard(
                        notification = notification.copy(isUnread = notification.isUnread && !allNotificationsRead),
                        onClick = { onNotificationClick(notification.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ClinicNotificationsHeaderSection(
    onBack: () -> Unit = {},
    onMarkAllRead: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4A90E2))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Notifications",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            TextButton(onClick = onMarkAllRead) {
                Text(
                    text = "Mark All Read",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ClinicNotificationsFilterTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf("All", "Appointments", "Patients", "Payments")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { tab ->
            FilterChip(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                label = {
                    Text(
                        text = tab,
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4A90E2),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFF5F5F5),
                    labelColor = Color(0xFF2B2B2B)
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ClinicNotificationCard(
    notification: ClinicNotification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isUnread) Color.White else Color(0xFFFAFAFA)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isUnread) 2.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(notification.iconBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 16.sp,
                        fontWeight = if (notification.isUnread) FontWeight.Bold else FontWeight.Medium,
                        color = Color(0xFF2B2B2B)
                    )
                    if (notification.isUnread) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF4A90E2), CircleShape)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.timestamp,
                    fontSize = 12.sp,
                    color = Color.Gray.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun EmptyNotificationsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔔",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No notifications",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2B2B2B)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You're all caught up!",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

