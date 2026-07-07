package com.litvy.carteleria.util.logging.model

// Categorias de capas/procesos de los que pueden surgir los logs
enum class LogCategory {
    SYSTEM, // Logs del sistema
    UI, // Logs de la interfaz de usuario
    PLAYBACK, // Logs de la lógica de slides, transiciones y reproducción
    MEDIA, // Logs de la gestión de medios(imagenes y videos)
    USB, // Logs de la gestión de USB
    SYNC, // Logs de la sincronización(Drive)
    UPDATE, // Logs de actualizacion de aplicación
    STORAGE, // Logs de almacenamiento
    CONFIGURATION, // Logs de configuración
    SECURITY, // Logs de seguridad
    UNKNOWN // Logs desconocidos
}