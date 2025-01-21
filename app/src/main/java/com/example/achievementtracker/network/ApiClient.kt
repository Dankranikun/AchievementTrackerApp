package com.example.achievementtracker.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.steampowered.com/"

    // Instancia de SteamApi creada con Retrofit
    val steamApi: SteamApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // URL base para la API de Steam
            .addConverterFactory(GsonConverterFactory.create()) // Conversor para manejar JSON
            .build()
            .create(SteamApi::class.java) // Crea la instancia de SteamApi
    }
}
