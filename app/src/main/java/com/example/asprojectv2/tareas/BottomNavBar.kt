package com.example.asprojectv2.tareas

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Datos de cada ítem en el menú inferior de navegación
 */
data class ItemNavegacion(
    val titulo: String,
    val icono: ImageVector,
    val ruta: String
)

/**
 * Barra inferior de navegación con tres secciones: Home, Agregar y Perfil.
 * Navega según la opción seleccionada.
 */
@Composable
fun BottomNavBar(
    rutaActual: String,
    navegarA: (String) -> Unit
) {
    val items = listOf(
        ItemNavegacion("Inicio", Icons.Default.Home, "home"),
        ItemNavegacion("Agregar", Icons.Default.Add, "add"),
        ItemNavegacion("Perfil", Icons.Default.Person, "profile")
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = rutaActual == item.ruta,
                onClick = { navegarA(item.ruta) },
                icon = { Icon(item.icono, contentDescription = item.titulo) },
                label = { Text(item.titulo) }
            )
        }
    }
}
