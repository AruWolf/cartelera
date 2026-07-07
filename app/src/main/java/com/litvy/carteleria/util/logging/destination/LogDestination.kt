package com.litvy.carteleria.util.logging.destination

import com.litvy.carteleria.util.logging.model.LogEntry

interface LogDestination {
    fun dispatch(entry: LogEntry)
}