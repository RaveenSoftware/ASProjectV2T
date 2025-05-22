package com.example.asprojectv2.tareas

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.asprojectv2.datastore.PerfilDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

/**
 * Pantalla Splash con animación de entrada vertical y desvanecimiento.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    val dataStore = remember { PerfilDataStore(context) }

    var nombre by remember { mutableStateOf("") }
    var mostrarTexto by remember { mutableStateOf(false) }

    // Iniciamos animación y redirección
    LaunchedEffect(Unit) {
        nombre = dataStore.obtenerNombre.first()
        delay(500) // pequeño retardo antes de mostrar el texto
        mostrarTexto = true
        delay(5500) // tiempo restante hasta completar los 10 segundos
        if (nombre.isNotBlank()) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("profile") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // Pantalla de bienvenida simple
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(24.dp))

            // Animación de aparición vertical + desvanecimiento
            AnimatedVisibility(
                visible = mostrarTexto,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
            ) {
                Text(
                    text = if (nombre.isNotBlank()) "¡Hola, $nombre!" else "Cargando configuración...",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
