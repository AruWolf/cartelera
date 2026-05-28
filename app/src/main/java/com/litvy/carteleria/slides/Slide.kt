package com.litvy.carteleria.slides

import androidx.compose.runtime.Composable

interface Slide {

    val id: String
    val transitionKey: String?
    val durationMs: Long?
    val customDurationMs: Long?

    @Composable
    fun Render(
        isPaused: Boolean, // Necesario para pausar videos
        onFinished: (() -> Unit)? // Marca el final de un video
    )
}
