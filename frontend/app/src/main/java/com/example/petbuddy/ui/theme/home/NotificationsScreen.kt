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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petbuddy.data.model.Notification
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.NotificationRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    onBack: () -> Unit = {},
    onMarkAllRead: () -> Unit = {},
    onNotificationClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val notificationRepository = remember { NotificationRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Function to fetch notifications
    fun fetchNotifications() {
        val userId = userSession.getUserId()
        if (userId == -1) {
            errorMessage = "Please login first"
            isLoading = false
            return
        }
        
        isLoading = true
        errorMessage = null
        
        coroutineScope.launch {
            val result = notificationRepository.getLostPetNotifications(userId)
            isLoading = false
            result.onSuccess { notificationList ->
                notifications = notificationList
            }.onFailure { exception ->
                errorMessage = exception.message
            }
        }
    }
    
    // Fetch notifications on screen load
    LaunchedEffect(Unit) {
        fetchNotifications()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Notifications",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            // Refresh and Mark All Read
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { fetchNotifications() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color(0xFFFF8A50),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Mark All",
                        fontSize = 12.sp,
                        color = Color(0xFFFF8A50),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onMarkAllRead() }
                    )
                    Text(
                        text = "Read",
                        fontSize = 12.sp,
                        color = Color(0xFFFF8A50),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onMarkAllRead() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notification List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFF8A50))
                    }
                }
                errorMessage != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = errorMessage ?: "Error loading notifications",
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { fetchNotifications() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF8A50)
                                )
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No notifications yet",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "You'll see notifications here when there are updates",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                else -> {
                    // Display all notifications
                    notifications.forEachIndexed { index, notification ->
                        NotificationCardFromData(
                            notification = notification,
                            onClick = { onNotificationClick(notification.notification_id.toString()) }
                        )
                        if (index < notifications.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// Helper function to format timestamp
private fun formatTimestamp(timestamp: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(timestamp)
        if (date != null) {
            val now = Date()
            val diff = now.time - date.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            
            when {
                seconds < 60 -> {
                    // Show actual time for "just now" notifications
                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    timeFormat.format(date)
                }
                minutes < 60 -> {
                    // Show actual time for recent notifications (within an hour)
                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    timeFormat.format(date)
                }
                hours < 24 -> {
                    // Show time for today's notifications
                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    "Today at ${timeFormat.format(date)}"
                }
                days < 7 -> {
                    // Show day and time for this week
                    val dayFormat = SimpleDateFormat("EEE hh:mm a", Locale.getDefault())
                    dayFormat.format(date)
                }
                else -> {
                    // Show full date and time for older notifications
                    val outputFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
                    outputFormat.format(date)
                }
            }
        } else {
            timestamp
        }
    } catch (e: Exception) {
        timestamp
    }
}

// Helper function to get icon and colors based on notification type
private fun getNotificationIconAndColor(type: String): Triple<androidx.compose.ui.graphics.vector.ImageVector, Color, Color> {
    return when {
        type.contains("lost_pet_alert", ignoreCase = true) -> Triple(
            Icons.Default.Info,
            Color(0xFFE57373),
            Color(0xFFE57373)
        )
        type.contains("pet_profile_created", ignoreCase = true) -> Triple(
            Icons.Default.Check,
            Color(0xFF4CAF50),
            Color(0xFF4CAF50)
        )
        type.contains("community_post", ignoreCase = true) -> Triple(
            Icons.Default.Create,
            Color(0xFF2196F3),
            Color(0xFF2196F3)
        )
        type.contains("vaccination", ignoreCase = true) -> Triple(
            Icons.Default.CheckCircle,
            Color(0xFF4CAF50),
            Color(0xFF4CAF50)
        )
        type.contains("medication", ignoreCase = true) -> Triple(
            Icons.Default.CheckCircle,
            Color(0xFF9C27B0),
            Color(0xFF9C27B0)
        )
        type.contains("nutrition", ignoreCase = true) -> Triple(
            Icons.Default.CheckCircle,
            Color(0xFFFF9800),
            Color(0xFFFF9800)
        )
        type.contains("match", ignoreCase = true) -> Triple(
            Icons.Default.Check,
            Color(0xFF4CAF50),
            Color(0xFF4CAF50)
        )
        type.contains("sighting", ignoreCase = true) -> Triple(
            Icons.Default.LocationOn,
            Color(0xFFFF8A50),
            Color(0xFFFF8A50)
        )
        else -> Triple(
            Icons.Default.Info,
            Color(0xFF64B5F6),
            Color(0xFF64B5F6)
        )
    }
}

@Composable
fun NotificationCardFromData(
    notification: Notification,
    onClick: () -> Unit
) {
    val (icon, iconColor, iconBackground) = getNotificationIconAndColor(notification.type)
    val formattedTimestamp = formatTimestamp(notification.timestamp)
    
    NotificationCard(
        icon = icon,
        iconColor = iconColor,
        iconBackground = iconBackground,
        title = notification.title,
        description = notification.description,
        timestamp = formattedTimestamp,
        isUnread = notification.is_unread,
        onClick = onClick
    )
}

@Composable
fun NotificationCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    iconBackground: Color,
    title: String,
    description: String,
    timestamp: String,
    isUnread: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timestamp,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Unread Dot
            if (isUnread) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFFF8A50), CircleShape)
                        .align(Alignment.Top)
                )
            }
        }
    }
}