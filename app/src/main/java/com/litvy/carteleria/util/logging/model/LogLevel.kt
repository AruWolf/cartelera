package com.litvy.carteleria.util.logging.model

enum class LogLevel {
    VERBOSE, // Mucho detalle, solo para desarrollo
    DEBUG, // Información útil para depuración
    INFO, // Eventos normales de app
    WARNING, // Eventos que pueden ocasionar problemas, pero no frenan la app
    ERROR, // Fallo de una operación concreta
    FATAL // Error irrecuperable, que suele terminar en crash
}