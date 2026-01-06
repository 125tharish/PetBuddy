package com.example.petbuddy.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // XAMPP Configuration:
    // - For Android Emulator: http://10.0.2.2/pet_buddy/ (10.0.2.2 is the emulator's alias for localhost)
    // - For Physical Device: http://[YOUR_LOCAL_IP]/pet_buddy/ (e.g., http://192.168.1.100/pet_buddy/)
    //   Find your local IP: ipconfig (Windows) or ifconfig (Mac/Linux)
    //   Ensure your device and computer are on the same network
    // - Make sure XAMPP Apache is running and pet_buddy folder is in htdocs
    private const val BASE_URL = "http://172.23.49.204/pet_buddy/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Configure Gson to be lenient to handle malformed JSON
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .create()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
