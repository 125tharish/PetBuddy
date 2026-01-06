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
import com.example.petbuddy.data.model.QuickSearchPet
import com.example.petbuddy.data.repository.QuickSearchRepository
import kotlinx.coroutines.launch

@Composable
fun QuickSearchScreen(onBack: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var searchResults by remember { mutableStateOf<List<QuickSearchPet>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val repository = remember { QuickSearchRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    // Perform search when query or filter changes
    LaunchedEffect(searchQuery, selectedStatus) {
        if (searchQuery.isNotEmpty() || selectedStatus != null) {
            isLoading = true
            errorMessage = null
            
            coroutineScope.launch {
                val result = repository.searchPets(
                    searchQuery = if (searchQuery.isNotEmpty()) searchQuery else null,
                    status = selectedStatus
                )
                
                isLoading = false
                result.onSuccess { response ->
                    searchResults = response.pets ?: emptyList()
                }.onFailure { exception ->
                    errorMessage = exception.message
                    searchResults = emptyList()
                }
            }
        } else {
            searchResults = emptyList()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F3))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF8A50))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Quick Search",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.width(48.dp))
        }

        // Search Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search pets by name, breed, location...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8A50),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Search Filters",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    text = "Lost Pets",
                    isSelected = selectedStatus == "lost",
                    onClick = { 
                        selectedStatus = if (selectedStatus == "lost") null else "lost"
                    }
                )
                FilterChip(
                    text = "Found Pets",
                    isSelected = selectedStatus == "found",
                    onClick = { 
                        selectedStatus = if (selectedStatus == "found") null else "found"
                    }
                )
                FilterChip(
                    text = "All",
                    isSelected = selectedStatus == null,
                    onClick = { selectedStatus = null }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFFF8A50)
                    )
                }
            }

            // Error message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFD32F2F),
                        fontSize = 14.sp
                    )
                }
            }

            // Search Results
            if (!isLoading && searchResults.isNotEmpty()) {
                Text(
                    text = "Search Results (${searchResults.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                searchResults.forEach { pet ->
                    SearchResultItem(
                        pet = pet,
                        onClick = {
                            // TODO: Navigate to pet details
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else if (!isLoading && searchQuery.isEmpty() && selectedStatus == null) {
                // Show recent searches when no search is active
                Text(
                    text = "Recent Searches",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Enter a search query or select a filter to find pets",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else if (!isLoading && searchQuery.isEmpty() && selectedStatus != null) {
                // Show results for filter only
                if (searchResults.isNotEmpty()) {
                    Text(
                        text = "Search Results (${searchResults.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    searchResults.forEach { pet ->
                        SearchResultItem(
                            pet = pet,
                            onClick = {
                                // TODO: Navigate to pet details
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    Text(
                        text = "No pets found",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .wrapContentWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFFFF8A50) else Color.White,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.Gray) else null
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (isSelected) Color.White else Color.Black,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SearchResultItem(
    pet: QuickSearchPet,
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pet icon based on status
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (pet.status == "lost") Color(0xFFFFE0B2) else Color(0xFFC8E6C9),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (pet.status == "lost") "🐕" else "🏠",
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pet.pet_name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = pet.title, // breed
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = if (pet.status == "lost") Color(0xFFFFE0B2) else Color(0xFFC8E6C9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = pet.status.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pet.status == "lost") Color(0xFFE65100) else Color(0xFF2E7D32)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pet.distance,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = pet.time_ago,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}