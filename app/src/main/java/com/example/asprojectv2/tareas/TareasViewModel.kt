package com.example.asprojectv2.tareas

import androidx.lifecycle.ViewModel
import com.example.asprojectv2.core.TareaDTO
import com.example.asprojectv2.core.TareasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel que maneja la lógica y estado de las tareas
class TareasViewModel : ViewModel() {

    private val repositorio = TareasRepository

    // Flow que contiene las tareas observables
    private val _tareas = MutableStateFlow<List<TareaDTO>>(emptyList())
    val tareas: StateFlow<List<TareaDTO>> = _tareas

    init {
        cargarTareas()
    }

    private fun cargarTareas() {
        _tareas.value = repositorio.obtenerTareas()
    }

    fun agregarTarea(tarea: TareaDTO) {
        repositorio.agregarTarea(tarea)
        cargarTareas()
    }

    fun marcarComoCompletada(id: Int) {
        val tarea = _tareas.value.find { it.id == id }
        if (tarea != null) {
            tarea.estaCompleta = true
            repositorio.actualizarTarea(tarea)
            cargarTareas()
        }
    }

    fun eliminarTarea(id: Int) {
        repositorio.eliminarTarea(id)
        cargarTareas()
    }
}
