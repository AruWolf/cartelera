package com.litvy.carteleria.util.logging

// Responsable de capturar excepciones no controladas por la aplicación.
interface CrashHandler {
    fun register()
}