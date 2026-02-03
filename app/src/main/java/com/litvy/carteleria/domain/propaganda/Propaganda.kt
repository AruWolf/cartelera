package com.litvy.carteleria.domain.propaganda

import androidx.compose.runtime.Composable
import com.litvy.carteleria.slides.Slide

interface Propaganda {
    fun slides(): List<@Composable () -> Unit>
}
