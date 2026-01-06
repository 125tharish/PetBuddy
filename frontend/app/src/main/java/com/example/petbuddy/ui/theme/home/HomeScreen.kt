package com.example.petbuddy.ui.theme.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
fun HomeScreen(
    onSearchClick: () -> Unit = {},
    onAIScanClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPetCareClick: () -> Unit = {},
    onCommunityClick: () -> Unit = {},
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
            HeaderSection(
                onSearchClick = onSearchClick,
                onAIScanClick = onAIScanClick,
                onReportClick = onReportClick,
                onNotificationsClick = onNotificationsClick
            )
            
            // Scrollable Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Recent Alerts Section
                RecentAlertsSection()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Pet Care Hub Card
                PetCareHubCard(
                    onClick = onPetCareClick
                )
                
                Spacer(modifier = Modifier.height(80.dp)) // Space for bottom nav
            }
        }
        
        // Bottom Navigation Bar
        BottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onSearchClick = onSearchClick,
            onCareClick = onPetCareClick,
            onCommunityClick = onCommunityClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun HeaderSection(
    onSearchClick: () -> Unit = {},
    onAIScanClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFF8A50))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Welcome Back!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Let's help reunite pets today",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
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
                            .background(Color.Red, RoundedCornerShape(4.dp))
                            .align(Alignment.TopEnd)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(
                    icon = Icons.Default.Search,
                    text = "Search",
                    color = Color(0xFF2196F3), // Blue outline color
                    backgroundColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = onSearchClick
                )
                Spacer(modifier = Modifier.width(10.dp))
                // AI Scan Button with Scan Icon
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(12.dp)) // Green outline
                        .padding(16.dp)
                        .clickable { onAIScanClick() },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Custom Scan Icon - Scanner with scanning lines
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Scanner body (rounded rectangle)
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(16.dp)
                                .background(Color(0xFF4CAF50), RoundedCornerShape(2.dp))
                        )
                        // Scanning lines (horizontal lines representing scan)
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height(2.dp)
                                    .background(Color.White)
                            )
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(2.dp)
                                    .background(Color.White)
                            )
                            Box(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(2.dp)
                                    .background(Color.White)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Scan",
                        color = Color(0xFF4CAF50), // Green text
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                ActionButton(
                    icon = Icons.Default.LocationOn,
                    text = "Report",
                    color = Color(0xFFFF5722), // Orange/Red outline color
                    backgroundColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = onReportClick
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    color: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(2.dp, color, RoundedCornerShape(12.dp)) // Add outline border
            .padding(16.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun RecentAlertsSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Alerts",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "View All",
                fontSize = 14.sp,
                color = Color(0xFFFF8A50),
                modifier = Modifier.clickable { }
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Alert Cards
        AlertCard(
            petName = "Max",
            status = "LOST",
            statusColor = Color.Red,
            breed = "Golden Retriever",
            time = "5 min ago",
            distance = "0.3 mi away"
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        AlertCard(
            petName = "Luna",
            status = "FOUND",
            statusColor = Color(0xFF4CAF50),
            breed = "Siamese Cat",
            time = "12 min ago",
            distance = "0.8 mi away"
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        AlertCard(
            petName = "Charlie",
            status = "LOST",
            statusColor = Color.Red,
            breed = "Beagle",
            time = "1 hour ago",
            distance = "1.2 mi away"
        )
    }
}

@Composable
fun AlertCard(
    petName: String,
    status: String,
    statusColor: Color,
    breed: String,
    time: String,
    distance: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = petName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                Text(
                    text = breed,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = distance,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun PetCareHubCard(onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Heart Icon - centered at top
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = Color(0xFF4DB6AC), // Light teal/turquoise color
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title
            Text(
                text = "Pet Care Hub",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            Text(
                text = "Manage health records, schedules & more",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    onCareClick: () -> Unit = {},
    onCommunityClick: () -> Unit = {},
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
        BottomNavItem(
            icon = Icons.Default.Home,
            label = "Home",
            isSelected = true,
            selectedColor = Color(0xFFFF8A50),
            onClick = {}
        )
        BottomNavItem(
            icon = Icons.Default.Search,
            label = "Search",
            isSelected = false,
            selectedColor = Color(0xFFFF8A50),
            onClick = onSearchClick
        )
        BottomNavItem(
            icon = Icons.Default.Favorite,
            label = "Care",
            isSelected = false,
            selectedColor = Color(0xFFFF8A50),
            onClick = onCareClick
        )
        BottomNavItem(
            icon = Icons.Default.Star,
            label = "Community",
            isSelected = false,
            selectedColor = Color(0xFFFF8A50),
            onClick = onCommunityClick
        )
        BottomNavItem(
            icon = Icons.Default.Person,
            label = "Profile",
            isSelected = false,
            selectedColor = Color(0xFFFF8A50),
            onClick = onProfileClick
        )
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
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
            fontSize = 12.sp,
            color = if (isSelected) selectedColor else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}
