package com.example.petbuddy.ui.theme.home

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.petbuddy.data.model.ImageComparisonResponse
import com.example.petbuddy.data.repository.AIImageComparisonRepository
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AIPhotoMatchingScreen(
    onBack: () -> Unit = {},
    onCapture: () -> Unit = {},
    onUpload: () -> Unit = {},
    onEnhance: () -> Unit = {},
    onMatchFound: (Uri, ImageComparisonResponse) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val repository = remember { AIImageComparisonRepository() }
    val coroutineScope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Create a function to generate a new photo file each time
    fun createPhotoFile(): File? {
        return try {
            // HHmmss format: Hours, Minutes, Seconds (24-hour format)
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "pet_photo_$timeStamp.jpg"
            // Use external files directory (configured in file_provider_paths.xml)
            // This is more reliable than cache directory for FileProvider
            val storageDir = context.getExternalFilesDir(null)
            if (storageDir != null) {
                if (!storageDir.exists()) {
                    storageDir.mkdirs()
                }
                File(storageDir, imageFileName)
            } else {
                // Fallback to cache directory if external storage is not available
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
    
    // Function to process image and compare
    val processImage: (Uri) -> Unit = { uri ->
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            repository.comparePetImage(context, uri)
                .onSuccess { result ->
                    isLoading = false
                    if (result.matches?.isNotEmpty() == true) {
                        onMatchFound(uri, result)
                    } else {
                        errorMessage = "No matches found. Try another photo."
                    }
                }
                .onFailure { exception ->
                    errorMessage = exception.message ?: "Failed to compare image"
                    isLoading = false
                }
        }
    }
    
    // Camera launcher - must be defined before launchCamera function
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            // Photo was taken successfully, process with AI
            processImage(imageUri!!)
        } else {
            // User cancelled or photo capture failed
            errorMessage = "Photo capture cancelled or failed"
        }
    }
    
    // Function to launch camera with proper file handling
    val launchCamera: () -> Unit = {
        try {
            val photoFile = createPhotoFile()
            if (photoFile != null) {
                // fileprovider matches the authority in AndroidManifest.xml
                val photoUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    photoFile
                )
                
                imageUri = photoUri
                errorMessage = null
                cameraLauncher.launch(photoUri)
            } else {
                errorMessage = "Failed to create photo file"
            }
        } catch (e: Exception) {
            errorMessage = "Error launching camera: ${e.message}"
            e.printStackTrace()
        }
    }
    
    // Permission launcher for camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, create file and launch camera
            launchCamera()
        } else {
            errorMessage = "Camera permission is required to take photos"
        }
    }
    
    // Gallery launcher - must be defined first
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            errorMessage = null
            // Process image with AI
            processImage(uri)
        }
    }
    
    // Function to launch gallery picker directly
    val launchGalleryPicker: () -> Unit = {
        val pickMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        galleryLauncher.launch(pickMediaRequest)
    }
    
    // Permission launcher for gallery (for Android 12 and below)
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, launch gallery picker
            launchGalleryPicker()
        } else {
            errorMessage = "Storage permission is required to access gallery"
        }
    }
    
    // Function to open gallery
    val openGallery: () -> Unit = {
        // For Android 13+ (API 33+), no permission needed for picking images
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launchGalleryPicker()
        } else {
            // For Android 12 and below, check READ_EXTERNAL_STORAGE permission
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            
            if (hasPermission) {
                launchGalleryPicker()
            } else {
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
    
    // Function to open camera
    val openCamera: () -> Unit = {
        // Check if camera permission is already granted
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        
        if (hasPermission) {
            // Permission already granted, launch camera directly
            launchCamera()
        } else {
            // Request camera permission
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with back button and AI Photo Matching title
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
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Photo Matching",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Top Section - Camera Viewfinder Area (2/3 of screen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .background(Color(0xFF2A2A2A))
            ) {
                // Camera Frame with Orange Corners
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(horizontal = 40.dp, vertical = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Rounded Rectangle Frame with light gray border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.75f)
                            .border(
                                width = 2.dp,
                                color = Color(0xFFCCCCCC),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        // Orange corner indicators - positioned at corners
                        // Top-left corner
                        Box(
                            modifier = Modifier
                                .size(24.dp, 24.dp)
                                .align(Alignment.TopStart)
                        ) {
                            // Horizontal line
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(3.dp)
                                    .background(Color(0xFFFF8A50))
                                    .align(Alignment.TopStart)
                            )
                            // Vertical line
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(20.dp)
                                    .background(Color(0xFFFF8A50))
                                    .align(Alignment.TopStart)
                            )
                        }
                        
                        // Top-right corner
                        Box(
                            modifier = Modifier
                                .size(24.dp, 24.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            // Horizontal line
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(3.dp)
                                    .background(Color(0xFFFF8A50))
                                    .align(Alignment.TopEnd)
                            )
                            // Vertical line
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(20.dp)
                                    .background(Color(0xFFFF8A50))
                                    .align(Alignment.TopEnd)
                            )
                        }
                        
                        // Bottom-left corner
                        Box(
                            modifier = Modifier
                                .size(24.dp, 24.dp)
                                .align(Alignment.BottomStart)
                        ) {
                            // Horizontal line
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(3.dp)
                                    .background(Color(0xFFFF8A50))
                                    .align(Alignment.BottomStart)
                            )
                            // Vertical line
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(20.dp)
                                    .background(Color(0xFFFF8A50))
                                    .align(Alignment.BottomStart)
                            )
                        }
                        
                        // Bottom-right corner
                        Box(
                            modifier = Modifier
                                .size(24.dp, 24.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            // Horizontal line
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(3.dp)
                                    .background(Color(0xFFFF8A50))
                                    .align(Alignment.BottomEnd)
                            )
                            // Vertical line
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(20.dp)
                                    .background(Color(0xFFFF8A50))
                                    .align(Alignment.BottomEnd)
                            )
                        }
                        
                        // Camera Icon in Center - rectangle with circle
                        Box(
                            modifier = Modifier.align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            // Rectangle outline (camera body)
                            Box(
                                modifier = Modifier
                                    .size(80.dp, 60.dp)
                                    .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                            )
                            // Circle in center (lens)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        }
                    }
                }
                
                // Instruction Overlay at Bottom of Viewfinder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 32.dp, vertical = 40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xCC000000),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Position pet in frame",
                                fontSize = 14.sp,
                                color = Color(0xFFCCCCCC),
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Take a clear photo of the pet for instant AI matching",
                                fontSize = 12.sp,
                                color = Color(0xFFCCCCCC),
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
            
            // Loading Overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFF8A50),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Analyzing image with AI...",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
            
            // Bottom Section - Control Bar (1/3 of screen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Control Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gallery Button (Left) - Dark gray circle
                        IconButton(
                            onClick = openGallery,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF2A2A2A), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                // Gallery icon
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Gallery",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(60.dp))
                        
                        // Capture Button (Center) - Orange circle with camera icon
                        IconButton(
                            onClick = openCamera,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFFF8A50), CircleShape)
                                    .border(3.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                // Custom Camera Icon
                                Box(
                                    modifier = Modifier.size(36.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Camera body (rounded rectangle)
                                    Box(
                                        modifier = Modifier
                                            .width(26.dp)
                                            .height(18.dp)
                                            .background(Color.White, RoundedCornerShape(3.dp))
                                    )
                                    // Camera lens (circle in center)
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(Color.White, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Instruction Text
                    Text(
                        text = "Tap camera to capture • Tap gallery to select photo",
                        fontSize = 12.sp,
                        color = Color(0xFFCCCCCC),
                        fontWeight = FontWeight.Normal
                    )
                    
                    // Error message if any
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            fontSize = 11.sp,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
