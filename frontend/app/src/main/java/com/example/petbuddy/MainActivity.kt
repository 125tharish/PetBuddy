package com.example.petbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.petbuddy.ui.theme.navgraph.AppNavHost
import com.example.petbuddy.ui.theme.PetBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetBuddyTheme {
                AppNavHost()
            }
        }
    }
}
