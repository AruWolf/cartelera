package com.litvy.carteleria.util.logging.exporter

import java.io.File

interface LogExporter {
    // Exporta los logs disponibles
    fun export(): File
}