package com.litvy.carteleria.util.logging

import com.litvy.carteleria.util.logging.model.LogEntry

interface LogDispatcher {
    fun dispatch(entry: LogEntry)
}