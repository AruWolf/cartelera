package com.litvy.carteleria.ui.animations

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform

class TvTransition<T>(
    val transform: AnimatedContentTransitionScope<T>.() -> ContentTransform
)
