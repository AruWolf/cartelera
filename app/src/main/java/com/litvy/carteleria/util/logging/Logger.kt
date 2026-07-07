package com.litvy.carteleria.util.logging

import com.litvy.carteleria.util.logging.model.LogCategory

// Contrato publico
interface Logger{
    fun verbose(
        category: LogCategory,
        message: String,
        tag: String? = null,
        throwable: Throwable? = null
    )

    fun debug(
        category: LogCategory,
        message: String,
        tag: String? = null,
        throwable: Throwable? = null
    )

    fun info(
        category: LogCategory,
        message: String,
        tag: String? = null,
        throwable: Throwable? = null
    )

    fun warning(
        category: LogCategory,
        message: String,
        tag: String? = null,
        throwable: Throwable? = null
    )

    fun error(
        category: LogCategory,
        message: String,
        tag: String? = null,
        throwable: Throwable? = null
    )

    fun fatal(
        category: LogCategory,
        message: String,
        tag: String? = null,
        throwable: Throwable? = null
    )
}