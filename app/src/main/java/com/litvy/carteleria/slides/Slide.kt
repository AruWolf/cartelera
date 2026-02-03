package com.litvy.carteleria.slides

import androidx.compose.runtime.Composable

interface Slide {
    /** Unique id for debugging and stability */
    val id: String

    /** Optional key to pick a transition from a map (e.g. "fade", "scale") */
    val transitionKey: String?

    /** How long this slide stays on screen */
    val durationMs: Long

    @Composable
    fun Render()
}
