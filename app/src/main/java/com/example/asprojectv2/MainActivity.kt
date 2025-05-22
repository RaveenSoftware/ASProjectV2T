package com.example.asprojectv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.asprojectv2.notifications.PedirPermisoNotificacion
import com.example.asprojectv2.tareas.AppNavegacion

/**
 * Actividad principal de la aplicación.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val activity = this
            PedirPermisoNotificacion(activity)

            MaterialTheme {
                AppNavegacion()
            }
        }
    }
}
