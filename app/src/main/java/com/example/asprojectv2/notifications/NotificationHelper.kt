package com.example.asprojectv2.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.asprojectv2.R

/**
 * Clase para crear y mostrar notificaciones.
 */
class NotificationHelper(private val context: Context) {

    private val CHANNEL_ID = "tareas_channel"

    init {
        crearCanalNotificaciones()
    }

    // Crea el canal (requerido desde Android 8 en adelante)
    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Recordatorios"
            val descripcion = "Notificaciones de tareas y cumpleaños"
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(CHANNEL_ID, nombre, importancia).apply {
                description = descripcion
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }

    // Muestra la notificación
    fun mostrarNotificacion(titulo: String, mensaje: String, id: Int = 1) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // puedes reemplazarlo
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }
    }
}
