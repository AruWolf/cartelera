package com.litvy.carteleria.util.logging

import com.litvy.carteleria.util.logging.destination.LogDestination
import com.litvy.carteleria.util.logging.model.LogEntry

class DefaultLogDispatcher(
    destinations: Collection<LogDestination>
): LogDispatcher {
    private val destionations = destinations.toList()

    override fun dispatch(entry: LogEntry) {
        destionations.forEach { destination ->
            try {
                destination.dispatch(entry)
            } catch (_: Exception) {

            }
        }
    }
}