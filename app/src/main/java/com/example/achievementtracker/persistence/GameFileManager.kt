package com.example.achievementtracker.persistence

import android.content.Context
import com.example.achievementtracker.GameInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class GameFileManager(private val context: Context) {
    private val fileName = "games.json"
    private val gson = Gson()

    // Guardar la lista de juegos en un archivo JSON
    fun saveGamesToFile(games: List<GameInfo>) {
        val json = gson.toJson(games)
        val file = File(context.filesDir, fileName)
        file.writeText(json)
    }

    // Cargar la lista de juegos desde un archivo JSON
    fun loadGamesFromFile(): List<GameInfo> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return emptyList()

        val json = file.readText()
        val type = object : TypeToken<List<GameInfo>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}
