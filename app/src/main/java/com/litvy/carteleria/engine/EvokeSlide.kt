package com.litvy.carteleria.engine

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.litvy.carteleria.animations.TvTransition
import com.litvy.carteleria.slides.Slide
import com.litvy.carteleria.slides.SlideSpeed
import kotlinx.coroutines.delay

class EvokeSlide(
    private val slides: List<Slide>,
    private val transition: TvTransition<Slide>,
    private val speed: SlideSpeed
) {

    init {
        require(slides.isNotEmpty()) { "EvokeSlide requires at least one slide" }
    }

    @Composable
    fun Render(
        modifier: Modifier = Modifier,
        currentIndex: Int,
        isPaused: Boolean,
        onAutoNext: () -> Unit
    ) {

        if (slides.isEmpty() || currentIndex !in slides.indices) return
        val currentSlide = slides[currentIndex]

        LaunchedEffect(currentIndex, isPaused, speed) {

            if (isPaused) return@LaunchedEffect

            val duration =
                (currentSlide.durationMs * speed.multiplier).toLong()

            delay(duration)

            if (!isPaused) {
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

                slide.Render()
            }
        }
    }
}