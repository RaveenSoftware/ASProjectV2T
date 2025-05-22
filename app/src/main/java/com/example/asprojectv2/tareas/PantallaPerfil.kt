package com.example.asprojectv2.tareas

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.asprojectv2.core.TareaDTO
import com.example.asprojectv2.datastore.PerfilDataStore
import com.example.asprojectv2.notifications.AlarmHelper
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(viewModel: TareasViewModel = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { PerfilDataStore(context) }

    // Estados del formulario
    var uriFoto by remember { mutableStateOf<Uri?>(null) }
    var nombre by remember { mutableStateOf("") }
    var cumpleaños by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var nacionalidad by remember { mutableStateOf("") }

    // Opciones de género
    val opcionesGenero = listOf("Masculino", "Femenino", "No binario", "Otro")
    var expanded by remember { mutableStateOf(false) }

    // DatePicker
    var showDatePicker by remember { mutableStateOf(false) }
    val calendar = remember { java.util.Calendar.getInstance() }

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                cumpleaños = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                showDatePicker = false
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Cargar perfil desde DataStore
    LaunchedEffect(Unit) {
        dataStore.obtenerNombre.collect { nombre = it }
        dataStore.obtenerCumpleaños.collect { cumpleaños = it }
        dataStore.obtenerGenero.collect { genero = it }
        dataStore.obtenerNacionalidad.collect { nacionalidad = it }
    }

    // Imagen
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uriFoto = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mi Perfil", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(20.dp))

        // Imagen de perfil (temporal)
        Image(
            painter = rememberAsyncImagePainter(uriFoto),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Subir foto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cumpleaños,
            onValueChange = { cumpleaños = it },
            label = { Text("Cumpleaños (yyyy-MM-dd)") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors()
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                readOnly = true,
                value = genero,
                onValueChange = {},
                label = { Text("Género") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opcionesGenero.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            genero = opcion
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nacionalidad,
            onValueChange = { nacionalidad = it },
            label = { Text("Nacionalidad") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (nombre.isNotBlank() && cumpleaños.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                scope.launch {
                    dataStore.guardarPerfil(nombre, cumpleaños, genero, nacionalidad)
                }

                val tareaCumpleaños = TareaDTO(
                    id = (1000..9999).random(),
                    titulo = "🎉 Cumpleaños de $nombre",
                    descripcion = "Recordatorio de cumpleaños",
                    fecha = cumpleaños
                )
                viewModel.agregarTarea(tareaCumpleaños)

                // Programar notificación para el cumpleaños a las 8:00 AM
                val fecha = LocalDate.parse(cumpleaños, DateTimeFormatter.ISO_DATE)
                val fechaHoraNotificacion = LocalDateTime.of(fecha.year, fecha.month, fecha.dayOfMonth, 8, 0)
                AlarmHelper(context).programarNotificacion(
                    fechaHora = fechaHoraNotificacion,
                    titulo = "🎂 Cumpleaños de $nombre",
                    mensaje = "¡Hoy es el cumpleaños de $nombre!",
                    id = 100 + nombre.hashCode().absoluteValue % 10000
                )

                Toast.makeText(context, "Perfil guardado y notificación programada", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Revisa que los campos sean válidos", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Guardar información")
        }
    }
}
