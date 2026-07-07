package com.litvy.carteleria.util.logging

import com.litvy.carteleria.util.logging.model.LogSession

interface SessionManager {
    // Devuelve la sesión activa
    fun currentSession(): LogSession
    // Inicia una nueva sesión
    fun start()
    // Finaliza la sesión abierta
    fun finish()
    // Finaliza la sesión indicando que ocurrio un crash
    fun crash()
}