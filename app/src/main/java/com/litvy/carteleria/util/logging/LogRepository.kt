package com.litvy.carteleria.util.logging

import com.litvy.carteleria.util.logging.model.LogEntry
import com.litvy.carteleria.util.logging.model.LogSession

interface LogRepository {
    // Metodo de guardado de logs
    fun save(entry: LogEntry)
    // Metodo de guardado de sesiones
    fun saveSession(session: LogSession)
    // Metodo para actualizar sesiones
    fun updateSession(session: LogSession)
    // Metodo para consultar sesiones
    fun getSessions(): List<LogSession>
    // Metodo para consultar logs de una sesion
    fun getLogs(sessionId: String): List<LogEntry>
    fun getLatest(limit: Int): List<LogEntry>
    // Metodo para consultar errores
    fun getErrors(): List<LogEntry>
    // Metodo para limpiar los registros
    fun clear()
}