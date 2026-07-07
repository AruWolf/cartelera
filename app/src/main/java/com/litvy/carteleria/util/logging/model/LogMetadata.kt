package com.litvy.carteleria.util.logging.model

// Información del entorno donde se ejecuta la aplicación
data class LogMetadata(
    // Información de la aplicación
    val versionName: String, // Versión de la aplicación
    val versionCode: Long, // Codigo de la versión
    // Información de Android del dispositivo
    val sdkInt: Int, // Nivel de API
    val androidVersion: String, // Versión android del dispositivo
    // Información del dispositivo
    val manufacturer: String, // Fabricante del dispositivo
    val brand: String, // Marca del dispositivo
    val model: String // Modelo del dispositivo
)