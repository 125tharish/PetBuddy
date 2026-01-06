package com.example.petbuddy.ui.theme.home

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.petbuddy.data.preferences.UserSession
import com.example.petbuddy.data.repository.PetRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory

@Composable
fun AddPetProfileScreen(
    onBack: () -> Unit = {},
    onImageClick: () -> Unit = {},
    onCreateProfile: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val petRepository = remember { PetRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var petName by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var microchipId by remember { mutableStateOf("") }
    var selectedPetType by remember { mutableStateOf("Dog") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var petPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var photoFile by remember { mutableStateOf<File?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    
    // Function to create photo file
    fun createPhotoFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "pet_profile_$timeStamp.jpg"
            val storageDir = context.getExternalFilesDir(null)
            if (storageDir != null) {
                if (!storageDir.exists()) {
                    storageDir.mkdirs()
                }
                File(storageDir, imageFileName)
            } else {
                val cacheDir = context.cacheDir
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs()
                }
                File(cacheDir, imageFileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoFile != null) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile!!
            )
            petPhotoUri = uri
        }
    }
    
    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = createPhotoFile()
            if (file != null) {
                photoFile = file
                val photoUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                cameraLauncher.launch(photoUri)
            } else {
                errorMessage = "Failed to create photo file"
            }
        } else {
            errorMessage = "Camera permission is required to take photos"
        }
    }
    
    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            petPhotoUri = uri
        }
    }
    
    // Gallery permission launcher (for Android 12 and below)
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val pickMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            galleryLauncher.launch(pickMediaRequest)
        } else {
            errorMessage = "Storage permission is required to access gallery"
        }
    }
    
    // Function to open camera
    fun openCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        
        if (hasPermission) {
            val file = createPhotoFile()
            if (file != null) {
                photoFile = file
                val photoUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                cameraLauncher.launch(photoUri)
            } else {
                errorMessage = "Failed to create photo file"
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    
    // Function to open gallery
    fun openGallery() {
        val pickMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        
        // For Android 13+ (API 33+), no permission needed for picking images
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            galleryLauncher.launch(pickMediaRequest)
        } else {
            // For Android 12 and below, check READ_EXTERNAL_STORAGE permission
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            
            if (hasPermission) {
                galleryLauncher.launch(pickMediaRequest)
            } else {
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
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
                text = "Add Pet Profile",
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
            
            // Pet Image/Avatar Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Pet Photo or Emoji
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { showImageSourceDialog = true }
                        .background(
                            if (petPhotoUri != null) Color.Transparent else Color(0xFFF5F5F5),
                            CircleShape
                        )
                        .border(
                            if (petPhotoUri != null) BorderStroke(0.dp, Color.Transparent) else BorderStroke(2.dp, Color(0xFFE0E0E0)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (petPhotoUri != null) {
                        // Display selected photo
                        AsyncImage(
                            model = petPhotoUri,
                            contentDescription = "Pet Photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Display emoji placeholder
                        Text(
                            text = if (selectedPetType == "Dog") "\uD83D\uDC36" else if (selectedPetType == "Cat") "\uD83D\uDC31" else "\uD83D\uDC3E",
                            fontSize = 80.sp
                        )
                    }
                }
                
                // Camera button overlay (positioned close to pet face, overlapping the circle)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = 10.dp)
                ) {
                    IconButton(
                        onClick = { showImageSourceDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFFF8A50), CircleShape)
                            .border(3.dp, Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Add Photo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Pet Type Selection
            Text(
                text = "Pet Type",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PetTypeSelectionButton(
                    emoji = "\uD83D\uDC36", // Dog emoji
                    text = "Dog",
                    isSelected = selectedPetType == "Dog",
                    onClick = { selectedPetType = "Dog" },
                    modifier = Modifier.weight(1f)
                )
                PetTypeSelectionButton(
                    emoji = "\uD83D\uDC31", // Cat emoji
                    text = "Cat",
                    isSelected = selectedPetType == "Cat",
                    onClick = { selectedPetType = "Cat" },
                    modifier = Modifier.weight(1f)
                )
                PetTypeSelectionButton(
                    emoji = "\uD83D\uDC3E", // Paw prints emoji (for Other)
                    text = "Other",
                    isSelected = selectedPetType == "Other",
                    onClick = { selectedPetType = "Other" },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Pet Details Form
            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                label = { Text("Pet Name *") },
                placeholder = { Text("e.g., Max") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8A50),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFFFF8A50),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Breed *") },
                placeholder = { Text("e.g., Golden Retriever") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8A50),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFFFF8A50),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8A50),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFFFF8A50),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8A50),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFFFF8A50),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Colour Field
            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Colour") },
                placeholder = { Text("e.g., Brown, Black, White") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8A50),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFFFF8A50),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Microchip ID Field
            OutlinedTextField(
                value = microchipId,
                onValueChange = { microchipId = it },
                label = { Text("Microchip ID") },
                placeholder = { Text("e.g., 123456789012345") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8A50),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFFFF8A50),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Error Message
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Image Source Selection Dialog
        if (showImageSourceDialog) {
            AlertDialog(
                onDismissRequest = { showImageSourceDialog = false },
                title = {
                    Text(
                        text = "Add Pet Photo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Choose how you want to add your pet's photo",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = {
                                showImageSourceDialog = false
                                openGallery()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gallery")
                        }
                        TextButton(
                            onClick = {
                                showImageSourceDialog = false
                                openCamera()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Camera")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImageSourceDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Create Profile Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    val userId = userSession.getUserId()
                    if (userId == -1) {
                        errorMessage = "Please login first"
                        return@Button
                    }
                    
                    if (petName.isBlank()) {
                        errorMessage = "Pet name is required"
                        return@Button
                    }
                    
                    errorMessage = null
                    isLoading = true
                    
                    coroutineScope.launch {
                        val result = petRepository.addPet(
                            userId = userId,
                            petName = petName.trim(),
                            petType = selectedPetType,
                            breed = breed.takeIf { it.isNotBlank() },
                            age = age.takeIf { it.isNotBlank() },
                            color = color.takeIf { it.isNotBlank() },
                            microchipId = microchipId.takeIf { it.isNotBlank() },
                            photoUri = petPhotoUri,
                            context = context
                        )
                        isLoading = false
                        
                        result.onSuccess { petResponse ->
                            // Create notification for the user
                            val petId = petResponse.pet?.pet_id
                            coroutineScope.launch {
                                try {
                                    val notificationResult = petRepository.createPetProfileNotification(
                                        userId = userId,
                                        petId = petId,
                                        petName = petName.trim(),
                                        petType = selectedPetType,
                                        breed = breed.takeIf { it.isNotBlank() }
                                    )
                                    notificationResult.onSuccess {
                                        android.util.Log.d("AddPetProfileScreen", "Notification created successfully")
                                    }.onFailure { e ->
                                        android.util.Log.e("AddPetProfileScreen", "Failed to create notification: ${e.message}")
                                    }
                                    // Small delay to ensure notification is created
                                    delay(300)
                                    onCreateProfile(petName)
                                } catch (e: Exception) {
                                    android.util.Log.e("AddPetProfileScreen", "Error creating notification: ${e.message}", e)
                                    onCreateProfile(petName) // Still navigate even if notification fails
                                }
                            }
                        }.onFailure { exception ->
                            errorMessage = exception.message ?: "Failed to create pet profile"
                        }
                    }
                },
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
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Create Profile",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Helper function to convert URI to base64
private fun convertUriToBase64(context: android.content.Context, uri: Uri): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        if (bitmap == null) return null
        
        // Resize if too large (max 1024px)
        val resizedBitmap = resizeBitmapForUpload(bitmap, 1024)
        
        // Compress and convert to base64
        val outputStream = java.io.ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val imageBytes = outputStream.toByteArray()
        android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_WRAP)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Helper function to resize bitmap
private fun resizeBitmapForUpload(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    
    if (width <= maxDimension && height <= maxDimension) {
        return bitmap
    }
    
    val scale = maxDimension.toFloat() / maxOf(width, height)
    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()
    
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}

@Composable
fun PetTypeSelectionButton(
    emoji: String,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White else Color.White
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, Color(0xFFFF8A50))
        } else {
            BorderStroke(1.dp, Color(0xFFE0E0E0))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFFFF8A50) else Color.Black
            )
        }
    }
}

