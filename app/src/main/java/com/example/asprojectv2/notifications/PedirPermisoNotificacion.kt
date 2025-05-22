package com.example.asprojectv2.notifications

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat

/**
 * Solicita el permiso POST_NOTIFICATIONS si el sistema es Android 13+.
 */
@Composable
fun PedirPermisoNotificacion(activity: Activity) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) {
                android.util.Log.w("PERMISO", " Permiso de notificación DENEGADO.")
            } else {
                android.util.Log.i("PERMISO", " Permiso de notificación CONCEDIDO.")
            }
        }
    )

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            val permiso = Manifest.permission.POST_NOTIFICATIONS
            val estado = ContextCompat.checkSelfPermission(activity, permiso)
            if (estado != PackageManager.PERMISSION_GRANTED) {
                launcher.launch(permiso)
            }
        }
    }
}
