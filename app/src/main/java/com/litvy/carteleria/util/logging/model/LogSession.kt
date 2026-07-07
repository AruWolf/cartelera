package com.litvy.carteleria.util.logging.model

// Clase que representa un inicio y cierre de sesión de usuario
data class LogSession(
    val id: String,
    val startTimestamp: Long, // Fecha y hora de inicio de la sesión
    val endTimestamp: Long? = null, // Fecha y hora de finalización de la sesión
    val crashed: Boolean = false, // Indica si la sesión ha finalizado debido a un error
    val metadata: LogMetadata // Datos adicionales de la sesión(Dispositivo y aplicación)
)