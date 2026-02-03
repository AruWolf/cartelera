package com.litvy.carteleria.slides

import androidx.compose.runtime.Composable

sealed interface Slide {
    @Composable
    fun Render()
}
