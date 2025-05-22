package com.example.asprojectv2.tareas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * Pantalla principal con navegación y barra inferior.
 */
@Composable
fun AppNavegacion() {
    // Controlador de navegación
    val navController = rememberNavController()

    // Obtenemos la ruta actual
    val backStackEntry = navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry.value?.destination?.route ?: "home"

    // Scaffold = estructura base con barra inferior
    Scaffold(
        bottomBar = {
            BottomNavBar(rutaActual) { ruta ->
                navController.navigate(ruta) {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            }
        }
    ) { paddingValues ->
        // Espacio visual interno
        Box(modifier = Modifier.padding(paddingValues)) {
            NavegacionHost(navController)
        }
    }
}

/**
 * Define las rutas disponibles en la app y qué pantalla mostrar en cada una.
 */
@Composable
fun NavegacionHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("home") { PantallaInicio() }
        composable("add") { PantallaAgregarTarea(navController) }
        composable("profile") { PantallaPerfil() }
    }
}
