package com.example.asprojectv2.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Creamos una extensión para tener un DataStore asociado al contexto
val Context.dataStore by preferencesDataStore(name = "perfil_usuario")

/**
 * Clase que permite guardar y recuperar los datos del perfil del usuario usando DataStore.
 */
class PerfilDataStore(private val context: Context) {

    // Claves únicas para cada campo que vamos a guardar
    companion object {
        val NOMBRE = stringPreferencesKey("nombre")
        val CUMPLEAÑOS = stringPreferencesKey("cumpleaños")
        val GENERO = stringPreferencesKey("genero")
        val NACIONALIDAD = stringPreferencesKey("nacionalidad")
    }

    // Función para guardar los datos del perfil
    suspend fun guardarPerfil(nombre: String, cumpleaños: String, genero: String, nacionalidad: String) {
        context.dataStore.edit { preferencias ->
            preferencias[NOMBRE] = nombre
            preferencias[CUMPLEAÑOS] = cumpleaños
            preferencias[GENERO] = genero
            preferencias[NACIONALIDAD] = nacionalidad
        }
    }

    // Flujos que permiten observar los valores guardados (de forma reactiva)
    val obtenerNombre: Flow<String> = context.dataStore.data.map { it[NOMBRE] ?: "" }
    val obtenerCumpleaños: Flow<String> = context.dataStore.data.map { it[CUMPLEAÑOS] ?: "" }
    val obtenerGenero: Flow<String> = context.dataStore.data.map { it[GENERO] ?: "" }
    val obtenerNacionalidad: Flow<String> = context.dataStore.data.map { it[NACIONALIDAD] ?: "" }
}
