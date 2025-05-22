package com.example.asprojectv2.core

import androidx.compose.runtime.mutableStateListOf

// Repositorio que simula una base de datos en memoria
class TareasRepository {

    // Lista simulada de tareas (se mantiene viva mientras la app está abierta)
    private val listaTareas = mutableStateListOf<TareaDTO>()

    fun obtenerTareas(): List<TareaDTO> = listaTareas

    fun agregarTarea(tarea: TareaDTO) {
        listaTareas.add(tarea)
    }

    fun actualizarTarea(tareaActualizada: TareaDTO) {
        val indice = listaTareas.indexOfFirst { it.id == tareaActualizada.id }
        if (indice != -1) {
            listaTareas[indice] = tareaActualizada
        }
    }

    fun eliminarTarea(id: Int) {
        listaTareas.removeAll { it.id == id }
    }
}
