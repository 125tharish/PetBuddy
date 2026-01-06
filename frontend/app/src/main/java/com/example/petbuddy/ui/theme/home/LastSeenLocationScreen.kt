package com.example.petbuddy.ui.theme.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.runtime.key
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import android.location.Location
import java.util.Locale
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LastSeenLocationScreen(
    onBack: () -> Unit = {},
    onSubmitReport: () -> Unit = {},
    onUseCurrentLocation: () -> Unit = {}
) {
    val context = LocalContext.current
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var additionalNotes by remember { mutableStateOf("") }
    var isLoadingLocation by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }
    
    // Permission state
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    // Map state
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val defaultLocation = remember { LatLng(12.9716, 77.5946) } // Default location
    val cameraPositionState = rememberCameraPositionState()
    
    // Initialize camera with default location
    LaunchedEffect(Unit) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }
    
    // Check permission status and request location on screen load
    LaunchedEffect(Unit) {
        // Check if location permission is already granted
        val hasFinePermission = ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val hasCoarsePermission = ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        hasLocationPermission = hasFinePermission || hasCoarsePermission
        
        if (hasFinePermission || hasCoarsePermission) {
            // Permission already granted, get location automatically
            isLoadingLocation = true
            getCurrentLocation(context) { address, coordinates, error ->
                if (address != null && coordinates != null) {
                    location = address
                    currentLocation = coordinates // This will trigger the LaunchedEffect below
                    locationError = null
                } else {
                    locationError = error ?: "Failed to get location. Please ensure location services are enabled."
                }
                isLoadingLocation = false
            }
        }
    }
    
    // Update camera when location changes - this ensures smooth animation to the location
    LaunchedEffect(currentLocation) {
        currentLocation?.let { latLng ->
            // Update camera position directly - this will smoothly animate to the new location
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
        }
    }

    // Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        hasLocationPermission = fineLocationGranted || coarseLocationGranted
        
        if (fineLocationGranted || coarseLocationGranted) {
            // Permission granted, get current location
            getCurrentLocation(context) { address, coordinates, error ->
                if (address != null && coordinates != null) {
                    location = address
                    currentLocation = coordinates // This will trigger the LaunchedEffect to update camera
                    locationError = null
                } else {
                    locationError = error ?: "Failed to get location. Please ensure location services are enabled."
                }
                isLoadingLocation = false
            }
        } else {
            // Permission denied
            locationError = "Location permission is required to use this feature. Please enable it in app settings."
            isLoadingLocation = false
        }
    }

    // Function to request location permission
    fun requestLocationPermission() {
        isLoadingLocation = true
        locationError = null
        
        val hasFinePermission = ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val hasCoarsePermission = ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (hasFinePermission || hasCoarsePermission) {
            // Permission already granted, get location directly
            getCurrentLocation(context) { address, coordinates, error ->
                if (address != null && coordinates != null) {
                    location = address
                    currentLocation = coordinates // This will trigger the LaunchedEffect to update camera
                    locationError = null
                } else {
                    locationError = error ?: "Failed to get location. Please ensure location services are enabled."
                }
                isLoadingLocation = false
            }
        } else {
            // Request permission
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
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
                text = "Last Seen Location",
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

            // Where & When? Section
            Text(
                text = "Where & When?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Help us narrow down the search area.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Map/Location Selection Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            ) {
                // Show map
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapType = MapType.NORMAL,
                        isMyLocationEnabled = hasLocationPermission
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        compassEnabled = true,
                        myLocationButtonEnabled = hasLocationPermission
                    ),
                    onMapClick = { latLng ->
                        currentLocation = latLng // This will trigger the LaunchedEffect to update camera
                        // Reverse geocode to get address
                        reverseGeocode(context, latLng) { address ->
                            location = address ?: "${latLng.latitude}, ${latLng.longitude}"
                        }
                    }
                ) {
                    // Add marker at selected/current location - ensure it updates when location changes
                    currentLocation?.let { latLng ->
                        key("location_marker_${latLng.latitude}_${latLng.longitude}") {
                            Marker(
                                state = MarkerState(position = latLng),
                                title = "Last Seen Location",
                                snippet = if (location.isNotEmpty()) location else "Location: ${String.format("%.6f", latLng.latitude)}, ${String.format("%.6f", latLng.longitude)}"
                            )
                        }
                    }
                }
                
                // Floating action button to get current location
                FloatingActionButton(
                    onClick = {
                        requestLocationPermission()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(56.dp),
                    containerColor = Color(0xFFFF8A50),
                    contentColor = Color.White
                ) {
                    if (isLoadingLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Get Current Location",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                
                // Show error message if any
                locationError?.let { error ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = error,
                                fontSize = 12.sp,
                                color = Color.Red,
                                maxLines = 2
                            )
                        }
                    }
                }
                
                // Show success message when location is found
                if (currentLocation != null && locationError == null && !isLoadingLocation) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Location found and marked on map",
                                fontSize = 12.sp,
                                color = Color(0xFF2E7D32),
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Location / Address (Required)
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location / Address *") },
                placeholder = { Text("Enter street address or area.") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8A50),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date and Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date (Required)
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date *") },
                    placeholder = { Text("mm/dd/yyyy") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { /* Open date picker */ }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Calendar",
                                tint = Color.Gray
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8A50),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )

                // Time (Required)
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time *") },
                    placeholder = { Text("HH:MM") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8A50),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Notes
            OutlinedTextField(
                value = additionalNotes,
                onValueChange = { additionalNotes = it },
                label = { Text("Additional Notes") },
                placeholder = { Text("Any additional details about the circumstances...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8A50),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Almost done banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Almost done!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "We'll activate your alert and start AI matching immediately.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Submit Report Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = onSubmitReport,
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
                Text(
                    text = "Submit Report",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Helper function to check if location services are enabled
 */
private fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
           locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

/**
 * Helper function to get current location and convert it to an address
 */
private fun getCurrentLocation(
    context: Context,
    onResult: (String?, LatLng?, String?) -> Unit
) {
    // Check if location permission is granted before making the call
    val hasFinePermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    
    val hasCoarsePermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    
    if (!hasFinePermission && !hasCoarsePermission) {
        onResult(null, null, "Location permission is required")
        return
    }
    
    // Check if location services are enabled
    if (!isLocationEnabled(context)) {
        onResult(null, null, "Location services are disabled. Please enable location services in settings.")
        return
    }
    
    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        // First, try to get last known location (faster, more reliable)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // We have a location, process it
                processLocation(context, location, onResult)
            } else {
                // If last location is null, try to get current location
                val cancellationTokenSource = CancellationTokenSource()
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { currentLocation: Location? ->
                    if (currentLocation != null) {
                        processLocation(context, currentLocation, onResult)
                    } else {
                        onResult(null, null, "Unable to get location. Please ensure GPS is enabled and try again.")
                    }
                }.addOnFailureListener { exception: Exception ->
                    val errorMsg = when {
                        exception.message?.contains("network", ignoreCase = true) == true -> 
                            "Network error. Please check your internet connection."
                        exception.message?.contains("timeout", ignoreCase = true) == true -> 
                            "Location request timed out. Please try again."
                        else -> 
                            "Error getting location: ${exception.message ?: "Unknown error"}"
                    }
                    onResult(null, null, errorMsg)
                }
            }
        }.addOnFailureListener { exception: Exception ->
            // If last location fails, try getCurrentLocation
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { currentLocation: Location? ->
                if (currentLocation != null) {
                    processLocation(context, currentLocation, onResult)
                } else {
                    onResult(null, null, "Unable to get location. Please ensure GPS is enabled and try again.")
                }
            }.addOnFailureListener { e: Exception ->
                val errorMsg = when {
                    e.message?.contains("network", ignoreCase = true) == true -> 
                        "Network error. Please check your internet connection."
                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "Location request timed out. Please try again."
                    else -> 
                        "Error getting location: ${e.message ?: "Unknown error"}"
                }
                onResult(null, null, errorMsg)
            }
        }
    } catch (e: SecurityException) {
        onResult(null, null, "Location permission denied. Please grant location permission.")
    } catch (e: Exception) {
        onResult(null, null, "Error: ${e.message ?: "Unknown error"}")
    }
}

/**
 * Helper function to process a Location object and convert it to address
 */
private fun processLocation(
    context: Context,
    location: Location,
    onResult: (String?, LatLng?, String?) -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    val latLng = LatLng(location.latitude, location.longitude)
    
    // Check if Geocoder is available
    if (!Geocoder.isPresent()) {
        onResult("${location.latitude}, ${location.longitude}", latLng, null)
        return
    }
    
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            ) { addresses ->
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val addressString = buildString {
                        val addressLine = address.getAddressLine(0)
                        if (!addressLine.isNullOrEmpty()) {
                            append(addressLine)
                        } else {
                            // Fallback: build address from components
                            val parts = listOfNotNull(
                                address.featureName,
                                address.locality,
                                address.adminArea,
                                address.countryName
                            ).filter { it.isNotEmpty() }
                            if (parts.isNotEmpty()) {
                                append(parts.joinToString(", "))
                            } else {
                                append("${location.latitude}, ${location.longitude}")
                            }
                        }
                    }
                    onResult(addressString, latLng, null)
                } else {
                    onResult("${location.latitude}, ${location.longitude}", latLng, null)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )

            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressString = buildString {
                    val addressLine = address.getAddressLine(0)
                    if (!addressLine.isNullOrEmpty()) {
                        append(addressLine)
                    } else {
                        // Fallback: build address from components
                        val parts = listOfNotNull(
                            address.featureName,
                            address.locality,
                            address.adminArea,
                            address.countryName
                        ).filter { it.isNotEmpty() }
                        if (parts.isNotEmpty()) {
                            append(parts.joinToString(", "))
                        } else {
                            append("${location.latitude}, ${location.longitude}")
                        }
                    }
                }
                onResult(addressString, latLng, null)
            } else {
                onResult("${location.latitude}, ${location.longitude}", latLng, null)
            }
        }
    } catch (_: Exception) {
        // If geocoding fails, still return coordinates
        onResult("${location.latitude}, ${location.longitude}", latLng, null)
    }
}


/**
 * Helper function to reverse geocode coordinates to address
 */
private fun reverseGeocode(
    context: Context,
    latLng: LatLng,
    onResult: (String?) -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    
    // Check if Geocoder is available
    if (!Geocoder.isPresent()) {
        onResult("${latLng.latitude}, ${latLng.longitude}")
        return
    }
    
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            ) { addresses ->
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val addressString = buildString {
                        val addressLine = address.getAddressLine(0)
                        if (!addressLine.isNullOrEmpty()) {
                            append(addressLine)
                        } else {
                            // Fallback: build address from components
                            val parts = listOfNotNull(
                                address.featureName,
                                address.locality,
                                address.adminArea,
                                address.countryName
                            ).filter { it.isNotEmpty() }
                            if (parts.isNotEmpty()) {
                                append(parts.joinToString(", "))
                            } else {
                                append("${latLng.latitude}, ${latLng.longitude}")
                            }
                        }
                    }
                    onResult(addressString)
                } else {
                    onResult("${latLng.latitude}, ${latLng.longitude}")
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )

            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressString = buildString {
                    val addressLine = address.getAddressLine(0)
                    if (!addressLine.isNullOrEmpty()) {
                        append(addressLine)
                    } else {
                        // Fallback: build address from components
                        val parts = listOfNotNull(
                            address.featureName,
                            address.locality,
                            address.adminArea,
                            address.countryName
                        ).filter { it.isNotEmpty() }
                        if (parts.isNotEmpty()) {
                            append(parts.joinToString(", "))
                        } else {
                            append("${latLng.latitude}, ${latLng.longitude}")
                        }
                    }
                }
                onResult(addressString)
            } else {
                onResult("${latLng.latitude}, ${latLng.longitude}")
            }
        }
    } catch (_: Exception) {
        // If geocoding fails, return coordinates
        onResult("${latLng.latitude}, ${latLng.longitude}")
    }
}