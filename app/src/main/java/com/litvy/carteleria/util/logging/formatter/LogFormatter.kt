package com.litvy.carteleria.util.logging.formatter

import com.litvy.carteleria.util.logging.model.LogEntry

// Convierte la entrada de un log en un archivo persistible
interface LogFormatter {
    fun format(entry: LogEntry): String
}