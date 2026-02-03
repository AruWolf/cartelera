package com.litvy.carteleria.util

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.litvy.carteleria.animations.TvTransition
import com.litvy.carteleria.slides.Slide
import kotlinx.coroutines.delay

class EvokeSlide(
    private val slides: List<Slide>,
    private val transitions: Map<String, TvTransition<Slide>>,
    private val defaultTransition: TvTransition<Slide>,
    private val transitionMs: Int = 700
) {
    init {
        require(slides.isNotEmpty()) { "EvokeSlide requires at least 1 slide." }
    }

    @Composable
    fun Render(modifier: Modifier = Modifier) {
        var currentIndex by remember { mutableStateOf(0) }

        // IMPORTANT: targetState type is Slide, so transitionSpec scope is Slide ✅
        val currentSlide = slides[currentIndex]

        LaunchedEffect(slides) {
            while (true) {
                delay(currentSlide.durationMs)
                currentIndex = (currentIndex + 1) % slides.size
            }
        }

        Box(modifier = modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = currentSlide,
                transitionSpec = {
                    val key = targetState.transitionKey
                    val picked = if (key != null) transitions[key] else null
                    (picked ?: defaultTransition).transform(this)
                },
                label = "tv-slideshow"
            ) { slide ->
                slide.Render()
            }
        }
    }
}
