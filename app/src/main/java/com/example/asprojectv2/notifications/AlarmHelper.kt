package com.example.asprojectv2.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Clase auxiliar para programar notificaciones usando AlarmManager.
 */
class AlarmHelper(private val context: Context) {

    fun programarNotificacion(
        fechaHora: LocalDateTime,
        titulo: String,
        mensaje: String,
        id: Int
    ) {
        val intent = Intent(context, NotificacionReceiver::class.java).apply {
            putExtra("titulo", titulo)
            putExtra("mensaje", mensaje)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val tiempoEnMillis = fechaHora.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        //  Comprobamos si tiene permiso para programar alarmas exactas (Android 12+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w("ALARM_HELPER", "No se puede programar alarma exacta.")
                Toast.makeText(
                    context,
                    "Activa el permiso de alarmas exactas en Ajustes",
                    Toast.LENGTH_LONG
                ).show()

                // Abre la pantalla de ajustes donde el usuario puede habilitarlo manualmente
                val intentAjustes = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intentAjustes.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intentAjustes)
                return
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            tiempoEnMillis,
            pendingIntent
        )

        Log.d("DEBUG_NOTIF", " Alarma programada para $fechaHora")
    }
}
