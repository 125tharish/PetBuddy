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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petbuddy.data.model.MedicalRecord
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.MedicalRecordRepository
import com.example.petbuddy.data.repository.PetRepository
import kotlinx.coroutines.launch

@Composable
fun MedicalRecordsScreen(
    petName: String = "",
    shouldRefresh: Boolean = false,
    onBack: () -> Unit = {},
    onDocumentClick: (String) -> Unit = {},
    onDownloadClick: (String) -> Unit = {},
    onUploadDocument: () -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val petRepository = remember { PetRepository() }
    val medicalRecordRepository = remember { MedicalRecordRepository() }
    val coroutineScope = rememberCoroutineScope()

    var medicalRecords by remember { mutableStateOf<List<MedicalRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var petId by remember { mutableStateOf<Int?>(null) }

    // Function to load medical records
    fun loadMedicalRecords() {
        val userId = userSession.getUserId()
        if (userId != -1 && petId != null) {
            coroutineScope.launch {
                isLoading = true
                val recordsResult = medicalRecordRepository.getMedicalRecords(userId, petId!!)
                recordsResult.onSuccess { records ->
                    medicalRecords = records
                    errorMessage = null
                    isLoading = false
                }.onFailure { exception ->
                    medicalRecords = emptyList()
                    errorMessage = exception.message
                    isLoading = false
                }
            }
        }
    }

    // Get pet_id from pet name and load medical records
    LaunchedEffect(petName) {
        val userId = userSession.getUserId()
        if (userId != -1 && petName.isNotEmpty()) {
            coroutineScope.launch {
                val petsResult = petRepository.getMyPets(userId)
                petsResult.onSuccess { pets ->
                    val pet = pets.find { it.pet_name == petName }
                    if (pet != null) {
                        petId = pet.pet_id
                        // Load medical records
                        loadMedicalRecords()
                    } else {
                        errorMessage = "Pet not found"
                        isLoading = false
                    }
                }.onFailure {
                    errorMessage = "Failed to load pet information"
                    isLoading = false
                }
            }
        } else {
            isLoading = false
        }
    }
    
    // Refresh when returning from AddMedicalRecordScreen
    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh && petId != null) {
            loadMedicalRecords()
        }
    }
    
    // Also refresh when petId changes
    LaunchedEffect(petId) {
        if (petId != null) {
            loadMedicalRecords()
        }
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
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Medical Records",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Error Message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Loading Indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF8A50))
                }
            } else if (medicalRecords.isEmpty() && errorMessage == null) {
                // No Records Message
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
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No medical records yet",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // Document Cards
                medicalRecords.forEach { record ->
                    DocumentCard(
                        document = DocumentRecord(
                            title = record.title,
                            fileType = "PDF", // Default, can be extracted from URL if needed
                            fileSize = "", // Not provided by backend
                            downloadUrl = record.download_url
                        ),
                        onClick = { onDocumentClick(record.title) },
                        onDownloadClick = { onDownloadClick(record.download_url) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Upload Document Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = onUploadDocument,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8A50)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Upload Document",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

data class DocumentRecord(
    val title: String,
    val fileType: String,
    val fileSize: String,
    val downloadUrl: String = ""
)

@Composable
fun DocumentCard(
    document: DocumentRecord,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit
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
            // Document Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFF8A50).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFFF8A50),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Document Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = document.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${document.fileType} • ${document.fileSize}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Download Icon
            IconButton(
                onClick = { onDownloadClick() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Download",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

