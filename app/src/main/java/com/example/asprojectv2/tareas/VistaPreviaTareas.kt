package com.example.asprojectv2.tareas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.asprojectv2.core.TareaDTO

// Componente que muestra una lista simple de tareas en pantalla
@Composable
fun VistaPreviaTareas(viewModel: TareasViewModel = viewModel()) {
    val tareas by viewModel.tareas.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Listado de Tareas", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        tareas.forEach { tarea ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (tarea.estaCompleta) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = tarea.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(text = tarea.descripcion, style = MaterialTheme.typography.bodySmall)
                    Text(text = "Fecha: ${tarea.fecha}", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Botón de prueba para insertar una tarea
        Button(onClick = {
            val nuevaTarea = TareaDTO(
                id = tareas.size + 1,
                titulo = "Tarea ${tareas.size + 1}",
                descripcion = "Descripción genérica",
                fecha = "2025-05-22"
            )
            viewModel.agregarTarea(nuevaTarea)
        }) {
            Text("Añadir tarea de prueba")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewVistaPreviaTareas() {
    MaterialTheme {
        VistaPreviaTareas()
    }
}
