package com.example.asprojectv2.tareas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.asprojectv2.core.TareaDTO
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

/**
 * Pantalla principal con un calendario horizontal simple y listado de tareas del día.
 */
@Composable
fun PantallaInicio(viewModel: TareasViewModel = viewModel()) {
    // Obtenemos el día de hoy como fecha inicial
    val hoy = LocalDate.now()

    // Estado: fecha seleccionada por el usuario
    var fechaSeleccionada by remember { mutableStateOf(hoy) }

    // Obtenemos tareas desde el ViewModel
    val tareas by viewModel.tareas.collectAsState()

    // Filtramos tareas para el día seleccionado
    val tareasDelDia = tareas.filter { it.fecha == fechaSeleccionada.toString() && !it.estaCompleta }

    // Tareas futuras sin completar
    val tareasFuturas = tareas.filter {
        LocalDate.parse(it.fecha).isAfter(fechaSeleccionada) && !it.estaCompleta
    }

    val diasDelMes = YearMonth.now().lengthOfMonth()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Calendario - ${fechaSeleccionada.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
            style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        // Calendario horizontal básico
        Row(modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (dia in 1..diasDelMes) {
                val fecha = hoy.withDayOfMonth(dia)

                Column(
                    modifier = Modifier
                        .background(
                            if (fecha == fechaSeleccionada) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                        .clickable { fechaSeleccionada = fecha }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = dia.toString())
                    Text(text = fecha.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Tareas del día ${fechaSeleccionada}", style = MaterialTheme.typography.titleMedium)

        // Lista de tareas del día seleccionado
        LazyColumn {
            items(tareasDelDia) { tarea ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = tarea.titulo, style = MaterialTheme.typography.titleSmall)
                        Text(text = tarea.descripcion, style = MaterialTheme.typography.bodySmall)
                        Text(text = "Fecha: ${tarea.fecha}", style = MaterialTheme.typography.labelSmall)

                        Button(
                            onClick = { viewModel.marcarComoCompletada(tarea.id) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Marcar como completada")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Tareas futuras", style = MaterialTheme.typography.titleMedium)

        // Lista de tareas futuras pendientes
        LazyColumn {
            items(tareasFuturas) { tarea ->
                ListItem(
                    headlineContent = { Text(tarea.titulo) },
                    supportingContent = { Text("Para: ${tarea.fecha}") }
                )
            }
        }
    }


}
