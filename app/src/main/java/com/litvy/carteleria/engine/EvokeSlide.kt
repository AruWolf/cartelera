package com.litvy.carteleria.engine

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.litvy.carteleria.animations.TvTransition
import com.litvy.carteleria.slides.ExternalImageSlide
import com.litvy.carteleria.slides.Slide
import com.litvy.carteleria.slides.resolveImageSlideDuration
import kotlinx.coroutines.delay

class EvokeSlide(
    private val slides: List<Slide>,
    private val transition: TvTransition<Slide>,
    private val globalImageDurationMs: Long
) {
    @Composable
    fun Render(
        modifier: Modifier = Modifier,
        currentIndex: Int,
        currentSlideOverride: Slide? = null,
        isPaused: Boolean,
        onAutoNext: () -> Unit
    ) {

        if (slides.isEmpty() || currentIndex !in slides.indices) return
        val currentSlide = currentSlideOverride ?: slides[currentIndex]

        LaunchedEffect(currentSlide.id, currentIndex, isPaused, globalImageDurationMs, currentSlide.customDurationMs) {

            if (isPaused) return@LaunchedEffect

            if (currentSlide is ExternalImageSlide) {
                delay(resolveImageSlideDuration(currentSlide, globalImageDurationMs))
                onAutoNext()
            }
        }

        Box(modifier = modifier.fillMaxSize()) {

            AnimatedContent(
                targetState = currentSlide,
                transitionSpec = {
                    transition.transform(this)
                },
                label = "tv-slideshow"
            ) { slide ->

                slide.Render(
                    isPaused = isPaused,
                    onFinished = {
                        if (slide.id == currentSlide.id) onAutoNext()
                    }
                )
            }
        }
    }
}

