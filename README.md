# AchievementManager

## Descripción
**AchievementManager** es una aplicación que permite gestionar logros de videojuegos de diferentes plataformas como Steam, Xbox, PlayStation Network (PSN), GOG y Epic Games. Utiliza la API de Steam para obtener los logros de los juegos, permitiendo a los usuarios visualizarlos y organizarlos de manera eficiente.

## Características
- Consulta y visualización de logros de juegos de Steam.
- Integración con múltiples plataformas de juegos.
- Interfaz intuitiva basada en Jetpack Compose con Material3.
- Navegación fluida con Jetpack Navigation.
- Soporte para almacenamiento de datos persistente.

## Tecnologías utilizadas
- **Lenguaje:** Kotlin
- **Interfaz:** Jetpack Compose con Material3
- **Red:** Retrofit para consumir la API de Steam
- **Navegación:** Jetpack Navigation
- **Persistencia:** SharedPreferences y almacenamiento local de juegos

## Instalación y Configuración
1. **Clonar el repositorio:**
   ```sh
   git clone https://github.com/Dankranikun/AchievementManager.git
   ```
2. **Abrir el proyecto en Android Studio.**
3. **Configurar la API Key de Steam:**
   - En el archivo `AchievementScreen.kt`, reemplaza el valor de `apiKey` con tu clave de Steam.
4. **Ejecutar la aplicación en un emulador o dispositivo físico.**

## Uso
### Navegación y Pantallas
- **Pantalla Principal:** Lista de juegos guardados.
- **Pantalla de Logros:** Muestra los logros obtenidos de un juego específico de Steam.
- **Menú lateral:** Permite seleccionar diferentes plataformas de juegos.
- **Búsqueda de logros:** Campo de texto con filtrado de logros.

### Código de ejemplo
#### Obtener logros de Steam mediante la API
```kotlin
LaunchedEffect(appId) {
    coroutineScope.launch {
        try {
            val response = ApiClient.steamApi.getGameAchievements(
                apiKey = "TU_API_KEY",
                appId = appId
            )
            achievements = response.game.availableGameStats?.achievements
        } catch (e: Exception) {
            errorMessage = "Error obteniendo los logros: ${e.message}"
        }
    }
}
```

#### Mostrar logros en una lista
```kotlin
LazyColumn(modifier = Modifier.fillMaxSize()) {
    items(achievements!!) { achievement ->
        AchievementItem(
            title = achievement.displayName,
            description = achievement.description ?: "No description",
            iconUrl = achievement.icon,
            onClick = { navController.navigate("achievement_detail/${achievement.displayName}") },
            isClickable = false
        )
    }
}
```

## Contribución
Si deseas contribuir:
1. Haz un fork del repositorio.
2. Crea una nueva rama (`git checkout -b feature/nueva-funcionalidad`).
3. Realiza tus cambios y haz commit (`git commit -m "Agregada nueva funcionalidad"`).
4. Sube los cambios (`git push origin feature/nueva-funcionalidad`).
5. Abre un Pull Request.

## Contacto
Si tienes preguntas o sugerencias, puedes contactarme en [nicopla808@gmail.com] o abrir un issue en el repositorio.

## Licencia
Este proyecto está licenciado bajo la [MIT License](LICENSE).

