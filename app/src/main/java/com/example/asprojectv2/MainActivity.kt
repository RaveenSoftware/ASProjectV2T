package com.example.asprojectv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.asprojectv2.notifications.PedirPermisoNotificacion
import com.example.asprojectv2.tareas.AppNavegacion
import com.example.asprojectv2.ui.theme.ASProjectV2Theme // ✅ Importa tu tema personalizado

/**
 * Actividad principal de la aplicación.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //  Pide permisos para notificaciones  necesario
            PedirPermisoNotificacion(this)


            ASProjectV2Theme {
                AppNavegacion()
            }
        }
    }
}
