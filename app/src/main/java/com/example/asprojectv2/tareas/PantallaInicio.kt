package com.example.asprojectv2.tareas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.asprojectv2.core.TareaDTO
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun PantallaInicio(viewModel: TareasViewModel = viewModel()) {
    val hoy = LocalDate.now()
    var fechaSeleccionada by remember { mutableStateOf(hoy) }
    val tareas by viewModel.tareas.collectAsState()

    //  Escucha del ciclo de vida osea log
    EscucharEventosDeCicloDeVida { evento ->
        when (evento) {
            "ON_CREATE" -> println(" onCreate ejecutado")
            "ON_START" -> println(" onStart ejecutado")
            "ON_RESUME" -> println(" onResume ejecutado")
            "ON_PAUSE" -> println("⏸ onPause ejecutado")
            "ON_STOP" -> println(" onStop ejecutado")
            "ON_DESTROY" -> println(" onDestroy ejecutado")
        }
    }

    //  Listas actualizadas en tiempo real (sin remember)
    val tareasDelDia = tareas.filter {
        it.fecha == fechaSeleccionada.toString() && !it.estaCompleta
    }

    val tareasFuturas = tareas.filter {
        LocalDate.parse(it.fecha).isAfter(fechaSeleccionada) && !it.estaCompleta
    }

    val diasDelMes = YearMonth.now().lengthOfMonth()

    Column(modifier = Modifier.padding(16.dp)) {
        //  Encabezado del calendario
        Text(
            "Calendario - ${fechaSeleccionada.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        //  Calendario horizontal simple
        Row(
            modifier = Modifier
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
                    Text(
                        text = fecha.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //  Lista de tareas del día seleccionado
        Text("Tareas del día $fechaSeleccionada", style = MaterialTheme.typography.titleMedium)

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

                        //  Botón que actualiza el estado y fuerza redibujo
                        Button(
                            onClick = {
                                viewModel.marcarComoCompletada(tarea.id)
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Marcar como completada")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //  Tareas próximas a vencer
        Text("Tareas futuras", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(tareasFuturas) { tarea ->
                ListItem(
                    headlineContent = { Text(tarea.titulo) },
                    supportingContent = { Text("Para: ${tarea.fecha}") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        //  Gráfico circular actualizado en tiempo real
        GraficoTareasPie(tareas = tareas)
    }
}

@Composable
fun GraficoTareasPie(tareas: List<TareaDTO>) {
    val completadas = tareas.count { it.estaCompleta }
    val pendientes = tareas.size - completadas
    val total = tareas.size.coerceAtLeast(1)

    val porcentajeCompletadas = completadas.toFloat() / total
    val porcentajePendientes = pendientes.toFloat() / total

    val chartData = listOf(
        PieData("Completadas", porcentajeCompletadas, MaterialTheme.colorScheme.primary),
        PieData("Pendientes", porcentajePendientes, MaterialTheme.colorScheme.error)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Resumen visual", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Canvas(modifier = Modifier.size(200.dp)) {
            var startAngle = -90f
            chartData.forEach {
                drawArc(
                    color = it.color,
                    startAngle = startAngle,
                    sweepAngle = 360 * it.porcentaje,
                    useCenter = true,
                    style = Fill
                )
                startAngle += 360 * it.porcentaje
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        chartData.forEach {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(it.color)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("${it.label}: ${(it.porcentaje * 100).toInt()}%")
            }
        }
    }
}

// 📦 Datos de cada segmento del pastel
data class PieData(val label: String, val porcentaje: Float, val color: Color)

/**
 * Observa y responde a los eventos del ciclo de vida de la pantalla.
 */
@Composable
fun EscucharEventosDeCicloDeVida(onEvento: (String) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, evento ->
            onEvento(evento.name)
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
