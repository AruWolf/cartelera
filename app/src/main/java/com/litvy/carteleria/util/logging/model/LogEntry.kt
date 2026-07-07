package com.litvy.carteleria.util.logging.model

// Representa un unico evento registrado por el sistema
data class LogEntry(
    val id: Long,
    val sessionId: String, // Sesión a la que pertenece el log
    val timestamp: Long, // Fecha y hora del log
    val level: LogLevel, // Nivel del log(WARNING, ERROR, FATAL, ETC.)
    val category: LogCategory, // Categoria del log(SYSTEM, UI, PLAYBACK, ETC.)
    val tag: String? = null, // Etiqueta del log(Indica la clase/función donde se originó el log)
    val message: String, // Descripción del evento
    val throwable: Throwable? = null, // Excepción asociada al evento
    val threadName: String // Nombre del hilo donde se originó el log
)
