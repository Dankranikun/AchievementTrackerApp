package com.example.achievementtracker

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

class WelcomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            WelcomeScreenContent(navController = navController)
        }
    }
}

@Composable
fun WelcomeScreenContent(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF6200EA)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            WELCOME()
            FormLogIn(onLoginSuccess = {
                // Navegar a la pantalla de logros después de iniciar sesión
                navController.navigate("drawer_screen") {
                    popUpTo(0)
                }
            })
        }
    }
}

@Composable
fun WELCOME(modifier: Modifier = Modifier) {
    Text(
        text = "WELCOME",
        color = Color.White,
        style = TextStyle(
            fontSize = 54.sp,
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.25f),
                offset = Offset(0f, 4f),
                blurRadius = 12f
            ),
            fontWeight = FontWeight.Bold
        ),
        textAlign = TextAlign.Center,
        modifier = modifier
            .requiredWidth(324.dp)
            .requiredHeight(64.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoDeTexto(
    modifier: Modifier = Modifier,
    etiqueta: String,
    valor: String,
    onValueChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = etiqueta,
            color = Color(0xff1e1e1e),
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(BorderStroke(1.dp, Color(0xffd9d9d9)), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            TextField(
                value = valor,
                onValueChange = onValueChange,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun FormLogIn(modifier: Modifier = Modifier, onLoginSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, Color(0xffd9d9d9)), RoundedCornerShape(8.dp))
            .padding(24.dp)
    ) {
        CampoDeTexto(etiqueta = "Email", valor = email, onValueChange = { email = it })
        CampoDeTexto(etiqueta = "Password", valor = password, onValueChange = { password = it })

        OutlinedButton(
            onClick = {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                                if (tokenTask.isSuccessful) {
                                    val idToken = tokenTask.result?.token
                                    if (idToken != null) {
                                        saveToken(context, idToken)
                                        onLoginSuccess()
                                    }
                                } else {
                                    errorMessage = tokenTask.exception?.localizedMessage
                                }
                            }
                        } else {
                            errorMessage = task.exception?.localizedMessage ?: "Unknown error"
                        }
                    }
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color(0xff2c2c2c),
                contentColor = Color(0xfff5f5f5)
            ),
            border = BorderStroke(1.dp, Color(0xff2c2c2c)),
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Sign In",
                color = Color(0xfff5f5f5),
                style = TextStyle(fontSize = 16.sp, lineHeight = 20.sp)
            )
        }

        errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

fun saveToken(context: Context, token: String) {
    val sharedPreferences = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("firebase_token", token).apply()
}
