@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

package com.example.achievementtracker

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.achievementtracker.persistence.GameFileManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manejador del archivo de los juegos
        val gameFileManager = GameFileManager(this)

        setContent {
            // Crear el controlador de navegación
            val navController = rememberNavController()

            // Manejador de estado para la lista de juegos
            var games by remember { mutableStateOf(gameFileManager.loadGamesFromFile()) }

            // Verificar si hay un token de usuario guardado
            val sharedPreferences = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
            val savedToken = sharedPreferences.getString("firebase_token", null)

            // Usar LaunchedEffect para evitar navegar múltiples veces
            LaunchedEffect(Unit) {
                if (savedToken != null) {
                    Log.d("MainActivity", "Token encontrado: $savedToken")
                    navController.navigate("drawer_screen") {
                        popUpTo(0) // Limpia el historial de navegación
                    }
                } else {
                    Log.d("MainActivity", "No se encontró token. Redirigiendo a welcome_screen")
                    navController.navigate("welcome_screen") {
                        popUpTo(0) // Limpia el historial de navegación
                    }
                }
            }

            // Configurar Navigator con las rutas y funciones
            Navigator(
                navController = navController,
                games = games,
                onGameListChange = { updatedGames ->
                    games = updatedGames // Actualizar el estado de los juegos
                    try {
                        gameFileManager.saveGamesToFile(updatedGames) // Guardar los cambios en el archivo
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error al guardar los datos: ${e.message}")
                    }
                }
            )
        }


    }
}

@Composable
fun DrawerScreen(
    navController: NavHostController,
    games: List<GameInfo>, // Donde están los juegos
    onGameListChange: (List<GameInfo>) -> Unit // Para actualizar la lista
) {
    val drawerState =
        rememberDrawerState(initialValue = DrawerValue.Closed) //Si el desplegable está abierto
    val coroutineScope = rememberCoroutineScope() // Para abrir o cerrar el desplegable
    var showAddGameDialog by remember { mutableStateOf(false) } // Cuadro de diálogo para añadir juegos
    var gameName by remember { mutableStateOf("") } // Nombre juego
    var appId by remember { mutableStateOf("") }    // ID juego
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var gameToDelete by remember { mutableStateOf<GameInfo?>(null) } // El juego que se intenta borrar

    // Diálogo para añadir juegos
    if (showAddGameDialog) {
        AddGameDialog(
            gameName = gameName,
            onGameNameChange = { gameName = it },
            appId = appId,
            onAppIdChange = { appId = it },
            onDismiss = { showAddGameDialog = false },
            onAccept = {
                if (gameName.isNotBlank() && appId.isNotBlank()) { // Si los datos están bien, añade el juego
                    val updatedGames =
                        games + GameInfo(appId = appId, name = gameName, imageUrl = "")
                    onGameListChange(updatedGames) // Actualiza la lista
                    gameName = "" // Resetea nombre e ID para la próxima adiciónn
                    appId = ""
                    showAddGameDialog = false // Cierra
                } else {
                    errorMessage = "Please enter valid data for both fields."
                }
            }
        )
    }

    // Diálogo para confirmar eliminación de juego
    if (showDeleteDialog && gameToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Juego") },
            text = { Text("¿Estás seguro de que quieres eliminar \"${gameToDelete?.name}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    onGameListChange(games - gameToDelete!!)
                    showDeleteDialog = false
                    gameToDelete = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    gameToDelete = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
// La barra de navegación TODO: hacer que se pueda usar para buscar.
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón para navegar a la pantalla de bienvenida
                Button(
                    onClick = {
                        // Eliminar el token de SharedPreferences
                        val sharedPreferences = navController.context.getSharedPreferences(
                            "AuthPrefs",
                            Context.MODE_PRIVATE
                        )
                        sharedPreferences.edit().remove("firebase_token").apply()

                        // Navegar a la pantalla de bienvenida
                        navController.navigate("welcome_screen") {
                            popUpTo(0) // Limpia el historial de navegación
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Log out", color = Color.White)
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Botón para abrir el diálogo de añadir juego
                Button(
                    onClick = { showAddGameDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text("Add Game", color = Color.White)
                }
            }
        }
    ) {
        // Contenido principal de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF6200EA))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Barra superior con búsqueda
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEDE7F6))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu Icon")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Search") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon"
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de juegos
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(games) { game ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Gray)
                                .combinedClickable(
                                    onClick = {
                                        if (game.appId.isNotBlank()) {
                                            navController.navigate("achievement_screen/${game.appId}")
                                        } else {
                                            Log.d(
                                                "DrawerScreen",
                                                "Invalid App ID for: ${game.name}"
                                            )
                                        }
                                    },
                                    onLongClick = {
                                        gameToDelete = game
                                        showDeleteDialog = true
                                    }
                                )
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = game.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "ID: ${game.appId}",
                                    fontSize = 14.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddGameDialog(
    gameName: String,
    onGameNameChange: (String) -> Unit,
    appId: String,
    onAppIdChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Game") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = gameName,
                    onValueChange = onGameNameChange,
                    placeholder = { Text("Game Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = appId,
                    onValueChange = onAppIdChange,
                    placeholder = { Text("App ID") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onAccept) {
                Text("Accept")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

data class GameInfo(val appId: String, val name: String, val imageUrl: String)