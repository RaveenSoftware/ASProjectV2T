package com.example.asprojectv2.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Este BroadcastReceiver recibe la alarma y muestra la notificación.
 */
class NotificacionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        android.util.Log.d("DEBUG_NOTIF", "BroadcastReceiver recibido")
        // Obtener datos enviados por la alarma
        val titulo = intent.getStringExtra("titulo") ?: "Recordatorio"
        val mensaje = intent.getStringExtra("mensaje") ?: "Tienes una tarea pendiente"

        // Usar NotificationHelper para mostrar la notificación
        val helper = NotificationHelper(context)
        helper.mostrarNotificacion(titulo, mensaje)
    }
}
