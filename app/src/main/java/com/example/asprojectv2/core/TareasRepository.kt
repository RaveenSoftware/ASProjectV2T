package com.example.asprojectv2.core

// Repositorio singleton que simula una base de datos en memoria
object TareasRepository {
    private val tareasInternas = mutableListOf<TareaDTO>()

    fun obtenerTareas(): List<TareaDTO> {
        return tareasInternas
    }

    fun agregarTarea(tarea: TareaDTO) {
        tareasInternas.add(tarea)
    }

    fun actualizarTarea(tarea: TareaDTO) {
        val index = tareasInternas.indexOfFirst { it.id == tarea.id }
        if (index != -1) {
            tareasInternas[index] = tarea
        }
    }

    fun eliminarTarea(id: Int) {
        tareasInternas.removeAll { it.id == id }
    }
}