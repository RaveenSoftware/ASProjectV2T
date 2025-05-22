package com.example.asprojectv2.core

// Modelo de datos para una tarea
data class TareaDTO(
    val id: Int,
    var titulo: String,
    var descripcion: String,
    var fecha: String, // Formato: "yyyy-MM-dd"
    var estaCompleta: Boolean = false
)
