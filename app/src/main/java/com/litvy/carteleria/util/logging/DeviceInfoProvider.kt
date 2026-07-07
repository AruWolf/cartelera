package com.litvy.carteleria.util.logging

import com.litvy.carteleria.util.logging.model.LogMetadata

// Interfaz para obtener información del dispositivo y entorno de ejecución
interface DeviceInfoProvider {
    fun getMetadata(): LogMetadata
}