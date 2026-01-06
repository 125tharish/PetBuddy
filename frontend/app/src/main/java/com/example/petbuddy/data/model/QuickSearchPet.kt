package com.example.petbuddy.data.model

data class QuickSearchPet(
    val title: String,        // breed name
    val pet_name: String,
    val status: String,      // "lost" or "found"
    val time_ago: String,
    val distance: String
)

