package com.example.achievementtracker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.achievementtracker.network.Achievement
import com.example.achievementtracker.network.ApiClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementScreen(appId: String, navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope() // Para correr cosas en segundo plano
    var achievements by remember { mutableStateOf<List<Achievement>?>(null) } //Lista de logros vacía
    var errorMessage by remember { mutableStateOf<String?>(null) } // Guardar errores

    // Llamada a la API cuando se cargue la pantalla
    LaunchedEffect(appId) {
        coroutineScope.launch {
            try {
                // Llamar a la API para conseguir los logros de un juego
                val response = ApiClient.steamApi.getGameAchievements(
                    apiKey = "<YOUR_API_KEY",
                    appId = appId // La ID que se ha introducido con el juego
                )
                // Guardar los logros en una lista
                achievements = response.game.availableGameStats?.achievements
            } catch (e: Exception) {
                errorMessage = "Error al obtener logros: ${e.message}"
            }
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { navController.navigate("drawer_screen") }) {
                    Text("Volver al inicio", color = Color.White)
                }
                Button(onClick = {}) {
                    Text("Steam", color = Color.White)
                }
                Button(onClick = {}) {
                    Text("XBox", color = Color.White)
                }
                Button(onClick = {}) {
                    Text("PSN", color = Color.White)
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF6200EA))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Barra de búsqueda
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEDE7F6))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu Icon")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Buscar logros...") },
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

                // Mostrar errores, progreso o logros
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (achievements == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(achievements!!) { achievement ->
                            AchievementItem(
                                title = achievement.displayName,
                                description = achievement.description ?: "Sin descripción",
                                iconUrl = achievement.icon,
                                onClick = {
                                    // Navegar al detalle del logro
                                    navController.navigate("achievement_detail/${achievement.displayName}")
                                },
                                isClickable = false // Desactivamos el clic para esta pantalla
                            )
                        }
                    }
                }
            }
        }
    }
}
// Para mostrar los logros
@Composable
fun AchievementItem(
    title: String,
    description: String,
    iconUrl: String,
    onClick: () -> Unit,
    isClickable: Boolean // Esto en false para que no se ponga nerviosa
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(if (isClickable) Modifier.clickable { onClick() } else Modifier), // Hacer clic solo si isClickable es true
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = description, fontSize = 14.sp)
            }
            Image(
                painter = rememberAsyncImagePainter(iconUrl),
                contentDescription = "Achievement Icon",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

