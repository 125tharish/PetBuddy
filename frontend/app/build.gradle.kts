plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.petbuddy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.petbuddy"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    // ✅ FIX JVM TARGET MISMATCH
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    // ✅ Compose BOM (ONLY ONCE)
    implementation(platform(libs.androidx.compose.bom))

    // ✅ Compose Material3
    implementation(libs.androidx.compose.material3)

    // ✅ Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // ✅ Activity Compose
    implementation(libs.androidx.activity.compose)

    // ✅ Network Libraries
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    
    // ✅ Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // ✅ Image Loading (Coil)
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // ✅ Material Icons Extended (for camera icons)
    implementation("androidx.compose.material:material-icons-extended")
    
    // ✅ Google Play Services Location
    implementation("com.google.android.gms:play-services-location:21.0.1")
    
    // ✅ Google Maps for Compose
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.maps.android:maps-compose-utils:4.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    
    // ✅ Image Processing for AI Comparison
    implementation("androidx.exifinterface:exifinterface:1.3.6")
}
