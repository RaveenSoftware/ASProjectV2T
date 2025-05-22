package com.example.asprojectv2.tareas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.asprojectv2.core.TareaDTO
import com.example.asprojectv2.notifications.AlarmHelper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.absoluteValue

/**
 * Pantalla para agregar tareas, con selección de fecha, hora y notificación automática.
 */
@Composable
fun PantallaAgregarTarea(
    navController: NavController? = null,
    tareasViewModel: TareasViewModel = viewModel()
) {
    val contexto = LocalContext.current

    // Estados para los campos del formulario
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var fechaTexto by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    var horaSeleccionada by remember { mutableStateOf("") }
    var horaInt by remember { mutableStateOf(8) }
    var minutoInt by remember { mutableStateOf(0) }

    val tareas = tareasViewModel.tareas.collectAsState().value

    // Calendario para los diálogos
    val calendario = Calendar.getInstance()

    // TimePickerDialog
    val timePicker = TimePickerDialog(
        contexto,
        { _, hour, minute ->
            horaInt = hour
            minutoInt = minute
            horaSeleccionada = String.format("%02d:%02d", hour, minute)
        },
        calendario.get(Calendar.HOUR_OF_DAY),
        calendario.get(Calendar.MINUTE),
        true
    )

    // DatePickerDialog para seleccionar la fecha
    if (showDatePicker) {
        DatePickerDialog(
            contexto,
            { _, year, month, day ->
                fechaTexto = "%04d-%02d-%02d".format(year, month + 1, day)
                showDatePicker = false
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Agregar nueva tarea", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(20.dp))

        // Campo: Título
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Descripción
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Fecha con ícono de calendario
        OutlinedTextField(
            value = fechaTexto,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha (yyyy-MM-dd)") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            colors = TextFieldDefaults.colors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Hora con diálogo al tocar
        OutlinedTextField(
            value = horaSeleccionada,
            onValueChange = {},
            readOnly = true,
            label = { Text("Hora (HH:mm)") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { timePicker.show() },
            placeholder = { Text("Ej: 08:00") },
            colors = TextFieldDefaults.colors()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón: Guardar tarea
        Button(
            onClick = {
                if (
                    titulo.isNotBlank() &&
                    fechaTexto.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) &&
                    horaSeleccionada.isNotBlank()
                ) {
                    val tarea = TareaDTO(
                        id = (0..99999).random(),
                        titulo = titulo,
                        descripcion = descripcion,
                        fecha = fechaTexto
                    )
                    tareasViewModel.agregarTarea(tarea)

                    try {
                        val fecha = LocalDate.parse(fechaTexto, DateTimeFormatter.ISO_DATE)
                        val fechaHoraNotificacion = LocalDateTime.of(
                            fecha.year, fecha.month, fecha.dayOfMonth,
                            horaInt, minutoInt
                        )

                        AlarmHelper(contexto).programarNotificacion(
                            fechaHora = fechaHoraNotificacion,
                            titulo = "Tarea: $titulo",
                            mensaje = "Tienes pendiente: $descripcion",
                            id = 200 + titulo.hashCode().absoluteValue % 10000
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(contexto, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }

                    Toast.makeText(contexto, "Tarea guardada y notificación programada", Toast.LENGTH_SHORT).show()
                    navController?.navigate("home")
                } else {
                    Toast.makeText(contexto, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Guardar")
        }

        // Mostrar resumen debajo del botón guardar
        Spacer(modifier = Modifier.height(24.dp))
        ResumenEstadisticas(tareas = tareas)
    }
}

/**
 * Muestra un resumen visual de las tareas: total, completadas, pendientes y progreso.
 */
@Composable
fun ResumenEstadisticas(tareas: List<TareaDTO>) {
    val total = tareas.size
    val completadas = tareas.count { it.estaCompleta }
    val pendientes = total - completadas
    val progreso = if (total > 0) completadas.toFloat() / total else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Resumen de tareas",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Total: $total")
        Text("Completadas: $completadas")
        Text("Pendientes: $pendientes")

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = progreso,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
    }
}
