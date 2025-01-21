package com.example.achievementtracker.network

import retrofit2.http.GET
import retrofit2.http.Query

interface SteamApi {
    @GET("ISteamUserStats/GetSchemaForGame/v2/")
    suspend fun getGameAchievements(
        @Query("key") apiKey: String,
        @Query("appid") appId: String
    ): AchievementResponse
}

// Modelo para manejar la respuesta
data class AchievementResponse(
    val game: GameData
)

data class GameData(
    val gameName: String,
    val availableGameStats: StatsData?
)

data class StatsData(
    val achievements: List<Achievement>
)

data class Achievement(
    val displayName: String,
    val description: String?,
    val icon: String
)
