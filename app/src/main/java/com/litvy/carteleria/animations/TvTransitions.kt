package com.litvy.carteleria.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

object TvTransitions {

    fun <T> fade(ms: Int = 700) = TvTransition<T> {
        fadeIn(tween(ms)) togetherWith fadeOut(tween(ms))
    }

    fun <T> scale(ms: Int = 700) = TvTransition<T> {
        scaleIn(tween(ms)) togetherWith scaleOut(tween(ms))
    }

    fun <T> slideLeft(ms: Int = 700) = TvTransition<T> {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(ms)) togetherWith
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(ms))
    }

    fun <T> slideUp(ms: Int = 700) = TvTransition<T> {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(ms)) togetherWith
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(ms))
    }
}
