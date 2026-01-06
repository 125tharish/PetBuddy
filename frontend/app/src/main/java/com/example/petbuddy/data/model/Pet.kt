package com.example.petbuddy.data.model

import com.google.gson.annotations.SerializedName

data class Pet(
    val pet_id: Int,
    val pet_name: String,
    val pet_type: String,
    val breed: String?,
    val age: String?,
    val color: String? = null,
    val microchip_id: String? = null,
    @SerializedName("pet_image")
    val photo_url: String? = null
)

