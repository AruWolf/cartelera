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
    fun Render(modifier: Modifier = Modifier) {

        var index by remember(slides) { mutableStateOf(0) }
        val currentSlide = slides[index]

        LaunchedEffect(index, slides, speed) {
            val duration = (currentSlide.durationMs * speed.multiplier).toLong()
            delay(duration)
            index = (index + 1) % slides.size
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
