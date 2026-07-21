package com.litvy.carteleria.domain.slides

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
}

fun resolveImageSlideDuration(
    slide: Slide,
    globalImageDuration: Long
): Long {
    return slide.customDurationMs ?: globalImageDuration
}
