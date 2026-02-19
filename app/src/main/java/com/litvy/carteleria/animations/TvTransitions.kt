package com.litvy.carteleria.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.AnimationConstants
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

    fun <T> slideRight(ms: Int = 700) = TvTransition<T> {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(ms)) togetherWith
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
    }

    fun <T> slideDown(ms: Int = 700) = TvTransition<T> {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(ms)) togetherWith
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down)
    }

    fun <T> random(ms: Int = 700) = TvTransition<T> {

        val options = listOf<(AnimatedContentTransitionScope<T>) -> ContentTransform>(
            { fadeIn(tween(ms)) togetherWith fadeOut(tween(ms)) },
            { scaleIn(tween(ms)) togetherWith scaleOut(tween(ms)) },
            {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(ms)
                ) togetherWith slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(ms)
                )
            },
            {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(ms)
                ) togetherWith slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(ms)
                )
            },
            {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(ms)
                ) togetherWith slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(ms)
                )
            },
            {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(ms)
                ) togetherWith slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(ms)
                )
            }
        )

        options.random()(this)
    }

}
