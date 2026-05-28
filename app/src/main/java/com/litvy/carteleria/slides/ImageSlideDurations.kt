package com.litvy.carteleria.slides

object ImageSlideDurations {
    const val DEFAULT_GLOBAL_DURATION_MS = 5000L

    val allowedValuesMs = listOf(
        5000L,
        10000L,
        15000L,
        20000L,
        25000L,
        30000L,
        45000L,
        60000L,
        90000L,
        120000L,
        180000L,
        240000L,
        300000L
    )

    fun isAllowed(durationMs: Long): Boolean {
        return durationMs in allowedValuesMs
    }

    fun labelFor(durationMs: Long): String {
        val seconds = durationMs / 1000L
        return when (seconds) {
            60L -> "1 minuto"
            120L -> "2 minutos"
            180L -> "3 minutos"
            240L -> "4 minutos"
            300L -> "5 minutos"
            else -> "$seconds segundos"
        }
    }
}

fun resolveImageSlideDuration(
    slide: Slide,
    globalImageDuration: Long
): Long {
    return slide.customDurationMs ?: globalImageDuration
}
