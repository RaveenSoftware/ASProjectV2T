package com.example.asprojectv2.core

import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Función para obtener la fecha actual en formato ISO "yyyy-MM-dd"
fun obtenerFechaActual(): String {
    return LocalDate.now().format(DateTimeFormatter.ISO_DATE)
}