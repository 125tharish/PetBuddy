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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ClinicOwnerHomeScreen(
    onAppointmentsClick: () -> Unit = {},
    onPatientsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F3))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Section
            ClinicHeaderSection(
                onNotificationsClick = onNotificationsClick,
                onProfileClick = onProfileClick
            )
            
            // Scrollable Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // Quick Actions
                QuickActionsSection(
                    onAppointmentsClick = onAppointmentsClick,
                    onPatientsClick = onPatientsClick
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Today's Appointments
                TodaysAppointmentsSection(
                    onViewAllClick = onAppointmentsClick
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Recent Activity
                RecentActivitySection()
                
                Spacer(modifier = Modifier.height(80.dp)) // Space for bottom nav
            }
        }
        
        // Bottom Navigation Bar
        ClinicBottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onHomeClick = {},
            onAppointmentsClick = onAppointmentsClick,
            onPatientsClick = onPatientsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun ClinicHeaderSection(
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4A90E2))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Welcome Back, Doctor!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Manage your clinic efficiently",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        IconButton(
                            onClick = onNotificationsClick
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        // Notification dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.Red, CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onProfileClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardStatsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Today",
            value = "12",
            subtitle = "Appointments",
            icon = null,
            color = Color(0xFF4A90E2),
            modifier = Modifier.weight(1f)
        )
        
        StatCard(
            title = "This Week",
            value = "48",
            subtitle = "Total",
            icon = null,
            color = Color(0xFF50C878),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector?,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2B2B2B)
            )
            
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun QuickActionsSection(
    onAppointmentsClick: () -> Unit = {},
    onPatientsClick: () -> Unit = {}
) {
    Column {
        Text(
            text = "Quick Actions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2B2B2B),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "View Appointments",
                icon = Icons.Default.Search,
                color = Color(0xFF4A90E2),
                onClick = onAppointmentsClick,
                modifier = Modifier.weight(1f)
            )
            
            QuickActionCard(
                title = "Manage Patients",
                icon = Icons.Default.Favorite,
                color = Color(0xFF50C878),
                onClick = onPatientsClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2B2B2B),
                maxLines = 2
            )
        }
    }
}

@Composable
fun TodaysAppointmentsSection(
    onViewAllClick: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Appointments",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2B2B2B)
            )
            
            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View All",
                    color = Color(0xFF4A90E2),
                    fontSize = 14.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        AppointmentItem(
            time = "09:00 AM",
            petName = "Max - Golden Retriever",
            ownerName = "John Smith",
            status = "Confirmed"
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        AppointmentItem(
            time = "10:30 AM",
            petName = "Luna - Persian Cat",
            ownerName = "Emily Johnson",
            status = "Pending"
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        AppointmentItem(
            time = "02:00 PM",
            petName = "Buddy - Beagle",
            ownerName = "Michael Brown",
            status = "Confirmed"
        )
    }
}

@Composable
fun AppointmentItem(
    time: String,
    petName: String,
    ownerName: String,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = time,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A90E2)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = petName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2B2B2B)
                )
                Text(
                    text = "Owner: $ownerName",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (status == "Confirmed") Color(0xFF50C878).copy(alpha = 0.2f) else Color(0xFFFF8A50).copy(alpha = 0.2f)
            ) {
                Text(
                    text = status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (status == "Confirmed") Color(0xFF50C878) else Color(0xFFFF8A50),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun RecentActivitySection() {
    Column {
        Text(
            text = "Recent Activity",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2B2B2B),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        ActivityItem(
            icon = Icons.Default.Done,
            title = "Appointment Completed",
            description = "Max - Vaccination",
            time = "2 hours ago",
            iconColor = Color(0xFF50C878)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        ActivityItem(
            icon = Icons.Default.Person,
            title = "New Patient Registered",
            description = "Luna - Persian Cat",
            time = "5 hours ago",
            iconColor = Color(0xFF4A90E2)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        ActivityItem(
            icon = Icons.Default.Info,
            title = "Appointment Scheduled",
            description = "Buddy - Checkup",
            time = "Yesterday",
            iconColor = Color(0xFFFF8A50)
        )
    }
}

@Composable
fun ActivityItem(
    icon: ImageVector,
    title: String,
    description: String,
    time: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconColor.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2B2B2B)
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = time,
                fontSize = 11.sp,
                color = Color.Gray.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ClinicBottomNavigationBar(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit = {},
    onAppointmentsClick: () -> Unit = {},
    onPatientsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ClinicBottomNavItem(
            icon = Icons.Default.Home,
            label = "Home",
            isSelected = true,
            selectedColor = Color(0xFF4A90E2),
            onClick = onHomeClick
        )
        ClinicBottomNavItem(
            icon = Icons.Default.Search,
            label = "Appointments",
            isSelected = false,
            selectedColor = Color(0xFF4A90E2),
            onClick = onAppointmentsClick
        )
        ClinicBottomNavItem(
            icon = Icons.Default.Favorite,
            label = "Patients",
            isSelected = false,
            selectedColor = Color(0xFF4A90E2),
            onClick = onPatientsClick
        )
        ClinicBottomNavItem(
            icon = Icons.Default.Person,
            label = "Profile",
            isSelected = false,
            selectedColor = Color(0xFF4A90E2),
            onClick = onProfileClick
        )
    }
}

@Composable
fun ClinicBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) selectedColor else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) selectedColor else Color.Gray
        )
    }
}

