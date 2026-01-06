package com.example.petbuddy.ui.theme.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UploadPhotosScreen(
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    onRemovePhoto: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    var photos by remember { mutableStateOf<List<Uri?>>(listOf(null, null, null)) }
    var currentPhotoIndex by remember { mutableStateOf(0) }
    var photoFile by remember { mutableStateOf<File?>(null) }
    
    // Camera result launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoFile != null) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile!!
            )
            if (currentPhotoIndex < photos.size) {
                val updatedPhotos = photos.toMutableList()
                updatedPhotos[currentPhotoIndex] = uri
                photos = updatedPhotos
            }
        }
    }
    
    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Create a temporary file for the photo
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_${timeStamp}_"
            val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
            photoFile = File.createTempFile(imageFileName, ".jpg", storageDir)
            
            val photoURI = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile!!
            )
            cameraLauncher.launch(photoURI)
        }
    }
    
    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { selectedUri ->
            // Use the current photo index that was set when gallery was clicked
            if (currentPhotoIndex < photos.size) {
                val updatedPhotos = photos.toMutableList()
                updatedPhotos[currentPhotoIndex] = selectedUri
                photos = updatedPhotos
            }
        }
    }
    
    // Gallery permission launcher (for Android 12 and below)
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val pickMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            galleryLauncher.launch(pickMediaRequest)
        }
    }
    
    // Function to handle camera click
    fun handleCameraClick(index: Int) {
        currentPhotoIndex = index
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        
        if (hasPermission) {
            // Create a temporary file for the photo
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_${timeStamp}_"
            val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
            photoFile = File.createTempFile(imageFileName, ".jpg", storageDir)
            
            val photoURI = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile!!
            )
            cameraLauncher.launch(photoURI)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    
    // Function to handle gallery click
    fun handleGalleryClick() {
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
                text = "Upload Photos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(40.dp)) // Balance the back button
        }
        
        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Add Pet Photos Section
            Text(
                text = "Add Pet Photos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Upload clear photos to help identify your pet. Multiple angles work best.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Photo Upload Grid
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Row - Two Photos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // First Photo Slot (Main Photo)
                    PhotoSlot(
                        photoUri = photos[0],
                        hasPhoto = photos[0] != null,
                        isMain = true,
                        photoIndex = 0,
                        onRemove = { index ->
                            val updatedPhotos = photos.toMutableList()
                            updatedPhotos[index] = null
                            photos = updatedPhotos
                            onRemovePhoto(index)
                        },
                        onCameraClick = { handleCameraClick(0) },
                        onGalleryClick = {
                            currentPhotoIndex = 0
                            handleGalleryClick()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Second Photo Slot
                    PhotoSlot(
                        photoUri = photos[1],
                        hasPhoto = photos[1] != null,
                        isMain = false,
                        photoIndex = 1,
                        onRemove = { index ->
                            val updatedPhotos = photos.toMutableList()
                            updatedPhotos[index] = null
                            photos = updatedPhotos
                            onRemovePhoto(index)
                        },
                        onCameraClick = { handleCameraClick(1) },
                        onGalleryClick = {
                            currentPhotoIndex = 1
                            handleGalleryClick()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Bottom Row - One Empty Photo Slot (Centered)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    PhotoSlot(
                        photoUri = photos[2],
                        hasPhoto = photos[2] != null,
                        isMain = false,
                        photoIndex = 2,
                        onRemove = { index ->
                            val updatedPhotos = photos.toMutableList()
                            updatedPhotos[index] = null
                            photos = updatedPhotos
                            onRemovePhoto(index)
                        },
                        onCameraClick = { handleCameraClick(2) },
                        onGalleryClick = {
                            currentPhotoIndex = 2
                            handleGalleryClick()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Photo Source Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Camera Button
                OutlinedButton(
                    onClick = {
                        // Find first empty slot or use first slot
                        val emptyIndex = photos.indexOfFirst { it == null }
                        val indexToUse = if (emptyIndex != -1) emptyIndex else 0
                        handleCameraClick(indexToUse)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = Color(0xFFFF8A50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Camera",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
                
                // Gallery Button
                OutlinedButton(
                    onClick = { handleGalleryClick() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = Color(0xFFFF8A50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gallery",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Photo Tips Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Photo Tips",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    
                    PhotoTipItem("Clear, well-lit photos work best")
                    Spacer(modifier = Modifier.height(8.dp))
                    PhotoTipItem("Include face, body, and unique markings")
                    Spacer(modifier = Modifier.height(8.dp))
                    PhotoTipItem("Avoid blurry or dark images")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Continue Button - Neat and Prominent
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8A50)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 6.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoSlot(
    photoUri: Uri?,
    hasPhoto: Boolean,
    isMain: Boolean,
    photoIndex: Int,
    onRemove: (Int) -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .then(
                if (hasPhoto) {
                    Modifier
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                } else {
                    Modifier
                        .border(
                            BorderStroke(2.dp, Color(0xFFE0E0E0)),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onGalleryClick() }
                }
            )
    ) {
        if (hasPhoto && photoUri != null) {
            // Display captured photo
            Image(
                painter = rememberAsyncImagePainter(photoUri),
                contentDescription = "Pet Photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Main tag (bottom left)
            if (isMain) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(Color(0xFFFF8A50), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Main",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            // Remove button (top right)
            IconButton(
                onClick = { onRemove(photoIndex) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE57373), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        } else {
            // Empty slot with plus icon
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onGalleryClick() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Photo",
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add Photo",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
fun PhotoTipItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
    }
}

